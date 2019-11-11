package br.com.infox.core.export;

public interface CsvWriterBuilder {

	public static interface CsvWriteBuilder {
		CsvRecordBuilder write(String value); 
	}
	
	public static interface CsvRecordBuilder extends CsvWriteBuilder {
		CsvRecordBuilder endRecord(); 
	}
	
	public static interface CsvBaseBuilder  extends CsvWriteBuilder {
		CsvBaseBuilder flush();
		void close();
	}
	
}
