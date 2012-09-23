package jhn.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Log implements AutoCloseable {
	private static final boolean AUTOFLUSH = true;
	private PrintStream[] logs;
	
	public Log() {
		this(System.out);
	}
	
	public Log(String filename) throws FileNotFoundException {
		this(new PrintStream(new FileOutputStream(filename), AUTOFLUSH));
	}
	
	public Log(PrintStream logTo, String filename) throws FileNotFoundException {
		this(logTo, new PrintStream(new FileOutputStream(filename), AUTOFLUSH));
	}
	
	public Log(PrintStream... logTo) {
		this.logs = logTo;
	}
	
	public void flush() {
		for(PrintStream log : logs) {
			log.flush();
		}
	}
	
	public void print(char c) {
		for(PrintStream log : logs) {
			log.print(c);
		}
		flush();
	}
	
	public void print(int i) {
		for(PrintStream log : logs) {
			log.print(i);
		}
		flush();
	}
	
	public void print(Object o) {
		for(PrintStream log : logs) {
			log.print(o);
		}
		flush();
	}
	public void println(Object o) {
		for(PrintStream log : logs) {
			log.println(o);
		}
	}
	public void println() {
		for(PrintStream log : logs) {
			log.println();
		}
	}
	
	public void println(int x) {
		for(PrintStream log : logs) {
			log.println(x);
		}
	}
	
	public void println(double x) {
		for(PrintStream log : logs) {
			log.println(x);
		}
	}
	
	@Override
	public void close() {
		for(PrintStream log : logs) {
			log.close();
		}
	}
	
	public PrintStream[] logs() {
		return logs;
	}
}
