package br.com.infox.core.export;

import java.io.StringWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import com.google.common.base.Charsets;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.list.CriteriaLazyDataModel.LoadListener;
import br.com.infox.core.list.CriteriaQuerySource;

public class CsvExporter {

	
	public static interface CsvGenerator<T> {
		void writeHeader(CsvWriter exporter); 
		void writeRow(CsvWriter exporter, T rowData); 
	}
	
	public static interface CsvExporterSourceBuilder<T> {
		CsvExporterWriterBuilder<T> source(CriteriaQuerySource<T> source);
		CsvExporterWriterBuilder<T> source(CriteriaQuery<T> source);
		CsvExporterWriterBuilder<T> source(List<T> source);
	}
	
	public static interface CsvExporterWriterBuilder<T> {
		CsvExporterFinalBuilder<T> writer(CsvGenerator<T> writer);
	}
	
	public static interface CsvExporterOpcionaisBuilder<T> {
		CsvExporterFinalBuilder<T> loadListener(LoadListener<T> loadListener);
	}
	
	public static interface CsvExporterFinalBuilder<T> extends CsvExporterOpcionaisBuilder<T> {
		void download(String fileName);
		void download(String fileName, char separator);
		String exportar();
		String exportar(char separator);
	}
	
	private static interface EntitiesLoader<T> {
		void setListener(LoadListener<T> listener);
		List<T> load();		
	}
	
	private static class EntitiesLoaderImpl<T> implements EntitiesLoader<T> {
		private CriteriaQuerySource<T> cqs;
		private CriteriaQuery<T> cq;
		private List<T> data;
		private LoadListener<T> listener;
		private EntityManager entityManager;

		public EntitiesLoaderImpl(EntityManager entityManager, CriteriaQuerySource<T> cqs) {
			super();
			this.entityManager = entityManager;
			this.cqs = cqs;
		}

		public EntitiesLoaderImpl(EntityManager entityManager, CriteriaQuery<T> cq) {
			super();
			this.entityManager = entityManager;
			this.cq = cq;
		}

		public EntitiesLoaderImpl(List<T> data) {
			super();
			this.data = data;
		}

		private List<T> load(CriteriaQuerySource<T> source) {
			CriteriaQuery<T> cq = source.createQuery(entityManager.getCriteriaBuilder());
			return load(cq);
		}
		
		private List<T> load(CriteriaQuery<T> source) {
			return load(entityManager.createQuery(source).getResultList());
		}
		
		private List<T> load(List<T> source) {
			if(listener != null) {
				listener.load(source);
			}
			return source;
		}
		
		@Override
		public List<T> load() {
			if(data != null) {
				return load(data);
			}
			else if(cq != null) {
				return load(cq);
			}
			else if(cqs != null) {
				return load(cqs);
			}
			else {
				throw new RuntimeException("Nenhum source definido");
			}
		}

		@Override
		public void setListener(LoadListener<T> listener) {
			this.listener = listener;
		}
	}
		
	public static class CsvExporterBuilderImpl<T> implements CsvExporterSourceBuilder<T>, CsvExporterWriterBuilder<T>, CsvExporterFinalBuilder<T> {

		private EntitiesLoader<T> loader;
		private CsvGenerator<T> writer;
		
		private EntityManager getEntityManager() {
			return EntityManagerProducer.getEntityManager();
		}
		
		@Override
		public CsvExporterFinalBuilder<T> loadListener(LoadListener<T> loadListener) {
			loader.setListener(loadListener);
			return this;
		}

		@Override
		public void download(String filename) {
			String csv = exportar(CsvWriter.DEFAULT_SEPARATOR);
			FileDownloader.download(csv.getBytes(Charsets.UTF_8), "text/csv", filename);					
		}
		
		@Override
		public void download(String filename, char separator) {
			String csv = exportar(separator);
			FileDownloader.download(csv.getBytes(Charsets.UTF_8), "text/csv", filename);					
		}

		@Override
		public String exportar() {
			return exportar(CsvWriter.DEFAULT_ESCAPE);
		}
		
		@Override
		public String exportar(char separator) {
			List<T> data = loader.load();
			String csv = exportarCsv(data, writer, separator);
			return csv;
		}

		@Override
		public CsvExporterFinalBuilder<T> writer(CsvGenerator<T> writer) {
			this.writer = writer;
			return this;
		}

		@Override
		public CsvExporterWriterBuilder<T> source(CriteriaQuerySource<T> source) {
			loader = new EntitiesLoaderImpl<T>(getEntityManager(), source);
			return this;
		}

		@Override
		public CsvExporterWriterBuilder<T> source(CriteriaQuery<T> source) {
			loader = new EntitiesLoaderImpl<T>(getEntityManager(), source);
			return this;
		}

		@Override
		public CsvExporterWriterBuilder<T> source(List<T> source) {
			loader = new EntitiesLoaderImpl<T>(source);
			return this;
		}
		
	}
		
	public static <T> CsvExporterSourceBuilder<T> builder(Class<T> classeExportada) {
		return new CsvExporterBuilderImpl<T>();	
	}
	
	public static <T> String exportarCsv(List<T> dataList, CsvGenerator<T> writer, char separator) {
		StringWriter sw = new StringWriter(); 
		
		try(CsvWriter exporter = new CsvWriter(sw, separator);) {
			writer.writeHeader(exporter);
			for(T data : dataList) {
				writer.writeRow(exporter, data);
			}
		}
		return sw.toString();
	}
}
