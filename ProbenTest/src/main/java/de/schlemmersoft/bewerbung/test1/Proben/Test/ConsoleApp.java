package de.schlemmersoft.bewerbung.test1.Proben.Test;

import java.io.PrintStream;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import de.schlemmersoft.bewerbung.test1.Proben.Public.GenericProbe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;
import de.schlemmersoft.bewerbung.test1.Proben.SQL.ProbenSQL;
import de.schlemmersoft.bewerbung.test1.Proben.Test.ConsoleApp.SyntaxError;
import de.schlemmersoft.bewerbung.test1.Proben.Vector.ProbenVector;

/**
 *
 */

/**
 * @author Tobias Schlemmer
 *
 */
public final class ConsoleApp {
	static ProbenAPI<Integer> api;

	static class IntProbe extends GenericProbe<Integer> {

		public IntProbe(String i, ZonedDateTime t) {
			super(i, t);
		}

		public IntProbe(String i, ZonedDateTime t, int v) {
			super(i, t, Integer.valueOf(v));
		}

		public IntProbe(IntProbe probe) {
			super(probe);
		}

		public IntProbe(String string, ZonedDateTime now, Integer Integer) {
			super(string, now, Integer);
		}

		@Override
		public Interpretation getInterpretation() {
			if (value == null)
				return Interpretation.FUZZY;
			switch (Integer.signum(value.intValue())) {
			case -1:
				return Interpretation.BAD;
			case 0:
				return Interpretation.FUZZY;
			case 1:
				return Interpretation.GOOD;
			}
			// dead code
			return null;
		}

		@Override
		protected Integer cloneValue(Integer v) {
			if (v == null)
				return null;
			int tmp = v.intValue();
			return Integer.valueOf(tmp);
		}

		@Override
		public IntProbe clone() throws CloneNotSupportedException {
			return (IntProbe) super.clone();
		}

		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("IntProbe {Id='");
			buf.append(id);
			buf.append("', ");
			buf.append(time);
			buf.append(", ");
			buf.append(value);
			buf.append(" (");
			buf.append(getInterpretation());
			buf.append(")}");
			return buf.toString();
		}
	}

	static class IntProbenVector extends ProbenVector<Integer, IntProbe> {

		IntProbenVector() {
			super();
		}

		@Override
		protected IntProbe createElement(String id, ZonedDateTime time, Integer value) {
			if (value == null)
				return new IntProbe(id, time);
			return new IntProbe(id, time, value.intValue());
		}

		@Override
		public String toString() {
			return data.toString();
		}

	}

	private static final Pattern datePattern = Pattern
			.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?[-+]\\d{2}:\\d{2}\\[[A-Za-z/]+\\]");

	static class SyntaxError extends Throwable {

		/**
		 *
		 */
		private static final long serialVersionUID = -7333225075293667921L;
	}

	abstract static class Command {
		void errorUsage() throws SyntaxError {
			System.err.print("Invalid command. Systax: ");
			usage(System.err);
			print(System.err);
			throw new SyntaxError();
		}

		void printUsage(PrintStream stream, String command, String usage) {
			stream.printf("%-8s %s\n", command, usage);
		}

		abstract void usage(PrintStream stream);

		abstract void print(PrintStream stream);

		abstract boolean parse(Scanner sc) throws SyntaxError;

		boolean execute() {
			System.out.println("Internal error.");
			throw new UnsupportedOperationException("Not implemented, yet.");
		}
	}

	static class helpCommand extends Command {
		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "help", "");
			for (Command command : commands.values()) {
				if (command != this)
					command.usage(stream);
			}
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("# Got: help\n");
		}

		@Override
		public boolean parse(Scanner sc) {
			return true;
		}

		@Override
		boolean execute() {
			usage(System.out);
			return true;
		}
	}

	static class quitCommand extends Command {
		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "quit", "");
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("# exiting...\n");
		}

		@Override
		public boolean parse(Scanner sc) {
			print(System.out);
			return true;
		}

		@Override
		boolean execute() {
			return false;
		}
	}

	static class commentCommand extends Command {
		String comment;

		@Override
		public void usage(PrintStream stream) {
			stream.printf("# [this line will be output verbatim]\n");
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("# %s\n", comment);
		}

		@Override
		public boolean parse(Scanner sc) {
			comment = sc.nextLine();
			return false;
		}

		@Override
		boolean execute() {
			print(System.out);
			return true;
		}
	}

	static class addCommand extends Command {
		String id = null;
		ZonedDateTime time = null;
		Integer value = null;

		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "add", "[id] date [number]");
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("added %s %s %s\n", id == null ? "<null>" : id, time == null ? "<null>" : time.toString(),
					value == null ? "<null>" : value.toString());
		}

		@Override
		public boolean parse(Scanner sc) throws SyntaxError {
			id = null;
			time = null;
			value = null;
			if (!sc.hasNext()) {
				errorUsage();
				return true;
			}
			if (!sc.hasNext(datePattern))
				id = sc.next();
			if (!sc.hasNext(datePattern)) {
				errorUsage();
				return true;
			}
			time = ZonedDateTime.parse(sc.next(datePattern));
			if (sc.hasNextInt()) {
				value = Integer.valueOf(sc.nextInt());
			}
			print(System.out);
			return true;
		}

		@Override
		boolean execute() {
			if (time == null)
				return true;
			Probe<Integer> sample = null;
			if (id == null) {
				sample = api.add(time);
				System.out.printf("New id %s\n", sample.getID());
			} else
				sample = api.add(id, time);
			if (value != null)
				sample.setValue(value);
			return true;
		}
	}

	static class addvalueCommand extends Command {
		String id = null;
		int value = 0;

		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "addvalue", "id [date] value");
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("# Got: addvalue %s %d\n", id == null ? "<null>" : id, value);
		}

		@Override
		public boolean parse(Scanner sc) throws SyntaxError {
			id = null;
			value = 0;

			if (!sc.hasNext()) {
				errorUsage();
				id = null;
				return true;
			}
			id = sc.next();
			if (!sc.hasNextInt()) {
				errorUsage();
				id = null;
				return true;
			}
			value = sc.nextInt();
			print(System.out);
			return true;
		}

		@Override
		boolean execute() {
			if (id == null)
				return true;
			try {
				Probe<Integer> sample = api.get(id);
				sample.setValue(value);
			} catch (NoSuchElementException e) {
				System.err.println("The sample could not be found.");
			}
			return true;
		}
	}

	static class removeCommand extends Command {
		String id = null;

		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "remove", "id [date]");
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("# Got: remove %s\n", id == null ? "<null>" : id);

		}

		@Override
		public boolean parse(Scanner sc) throws SyntaxError {
			id = null;
			if (!sc.hasNext()) {
				errorUsage();
				return true;
			}
			id = sc.next();
			print(System.out);
			return true;
		}

		@Override
		boolean execute() {
			try {
				api.remove(id);
			} catch (NoSuchElementException e) {
				System.err.println("The sample could not be found.");
			}
			return true;
		}
	}

	static class listCommand extends Command {
		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "list", "");
		}

		@Override
		public void print(PrintStream stream) {
			stream.println("# Got: list");
		}

		@Override
		public boolean parse(Scanner sc) {
			print(System.out);
			return true;
		}

		@Override
		boolean execute() {
			Iterator<Probe<Integer>> it = api.iterator();
			Probe<Integer> sample;
			while (it.hasNext()) {
				sample = it.next();
				Integer value = sample.getValue();
				System.out.printf("%-36s\t%s\t%s\n", sample.getID(), sample.getTime(), value == null ? "" : value);
			}
			return true;
		}

	}

	static class rangeCommand extends Command {
		Integer from;
		Integer to;

		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "range", "from to");
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("# Got: range %s %s\n", from == null ? "<null>" : from.toString(),
					to == 0 ? "<null>" : to.toString());
		}

		@Override
		public boolean parse(Scanner sc) throws SyntaxError {
			if (!sc.hasNextInt()) {
				errorUsage();
				return true;
			}
			from = sc.nextInt();
			if (!sc.hasNextInt()) {
				errorUsage();
				return true;
			}
			to = sc.nextInt();
			print(System.out);
			return true;
		}

		@Override
		boolean execute() {
			Iterator<Probe<Integer>> it = api.range(from, to).iterator();
			Probe<Integer> sample;
			while (it.hasNext()) {
				sample = it.next();
				Integer value = sample.getValue();
				System.out.printf("%-36s\t%s\t%s\n", sample.getID(), sample.getTime(), value == null ? "" : value);
			}
			return true;
		}
	}

	static class resultCommand extends Command {
		Interpretation result;

		static final Map<String, Interpretation> valueMap = Map.of("GOOD", Interpretation.GOOD, "FUZZY",
				Interpretation.FUZZY, "BAD", Interpretation.BAD);

		@Override
		public void usage(PrintStream stream) {
			printUsage(stream, "result", "GOOD|FUZZY|BAD");
		}

		@Override
		public void print(PrintStream stream) {
			stream.printf("# Got: result %s\n", result == null ? "<null>" : result.toString());
		}

		@Override
		public boolean parse(Scanner sc) throws SyntaxError {
			result = null;
			if (!sc.hasNext()) {
				errorUsage();
				return true;
			}
			result = valueMap.get(sc.next());
			if (result == null) {
				errorUsage();
				return true;
			}
			print(System.out);
			return true;
		}

		@Override
		boolean execute() {
			Iterator<Probe<Integer>> it = api.result(result).iterator();
			Probe<Integer> sample;
			while (it.hasNext()) {
				sample = it.next();
				Integer value = sample.getValue();
				System.out.printf("%-36s\t%s\t%s\n", sample.getID(), sample.getTime(), value == null ? "" : value);
			}
			return true;
		}
	}

	static Map<String, Command> commands = Map.of("quit", new quitCommand(), "add", new addCommand(), "addvalue",
			new addvalueCommand(), "remove", new removeCommand(), "list", new listCommand(), "range",
			new rangeCommand(), "result", new resultCommand(), "help", new helpCommand(), "#", new commentCommand());

	/**
	 * The acutual main program. This method is separated from the main function in
	 * order to keep exception handling visible to the the test runner.
	 *
	 * @param args The program args. The first one must be a JDBC URI and the
	 *             secound one is the database table. If less arguments are given,
	 *             the vector based implementation is used.
	 * @throws SQLException An SQL exception has been thrown by the JDBC backend.
	 * @throws SyntaxError A syntax error lead to the termination of the program.
	 */
	public static void mainProgram(String[] args) throws SQLException, SyntaxError {
		boolean useSQL = false;
		if (args != null && args.length >= 2)
			useSQL = true;
		if (useSQL) {
			System.out.printf("# database jdbc:sqlite:%s, table %s\n",
					  args[0],
					  args[1]);
			ProbenSQL sqlapi = new ProbenSQL("jdbc:sqlite:" + args[0], args[1]);
			sqlapi.createTable();
			api = sqlapi;
		} else {
			System.out.printf("# in-Memory solution\n");
			api = new ConsoleApp.IntProbenVector();
		}
		Scanner sc = new Scanner(System.in);
		while (sc.hasNext()) {
			String commandName = sc.next();
			Command command = commands.get(commandName);
			boolean skipGarbage = true;
			if (command != null) {
				skipGarbage = command.parse(sc);
				if (!command.execute())
					return;
			} else {
				System.err.printf("Unknown command %s.\n", commandName);
			}
			if (skipGarbage)
				sc.nextLine(); // ignore garbage
		}
		if (useSQL) {
			((ProbenSQL) api).close();
		}
	}

	/**
	 * The main program. It is mainly a wrapper around mainProgram and adds minimal
	 * exception handling.
	 *
	 * @param args Program arguments.
	 */
	public static void main(String[] args) {
		try {
			mainProgram(args);
			System.exit(0);
		} catch (SyntaxError e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			System.exit(-3);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(-4);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}

}
