package br.com.infox.core.export;

import java.io.PrintWriter;
import java.io.Writer;

import br.com.infox.core.export.CsvWriterBuilder.CsvBaseBuilder;
import br.com.infox.core.export.CsvWriterBuilder.CsvRecordBuilder;

public class CsvWriter implements CsvRecordBuilder, CsvBaseBuilder, AutoCloseable {

	public static final char DEFAULT_SEPARATOR = ',';
	public static final char DEFAULT_QUOTE = '"';
	public static final char DEFAULT_ESCAPE = '"';
	public static final String DEFAULT_LINE_END = "\n";
	
	private PrintWriter printWriter;
	private char separator = DEFAULT_SEPARATOR;
	private boolean first = true;
	private String lineEnd = DEFAULT_LINE_END;
		
	public CsvWriter(Writer writer, char separator) {
		this.separator = separator;
		this.printWriter = new PrintWriter(writer);
	}
	
	public CsvWriter(Writer writer) {
		this(writer, DEFAULT_SEPARATOR);
	}
	
	@Override
	public CsvRecordBuilder write(String value) {
		value = value.replace(String.valueOf(DEFAULT_QUOTE), String.valueOf(new char[] {DEFAULT_ESCAPE, DEFAULT_QUOTE}));
		if(!first) {
			printWriter.write(separator);
		}
		first = false;
		printWriter.write(DEFAULT_QUOTE + value + DEFAULT_QUOTE);
		
		return this;
	}

	@Override
	public CsvBaseBuilder flush() {
		printWriter.flush();
		return this;
	}

	@Override
	public void close() {
		printWriter.close();
	}

	@Override
	public CsvRecordBuilder endRecord() {
		first = true;
		printWriter.write(lineEnd);
		return this;
	}
}
