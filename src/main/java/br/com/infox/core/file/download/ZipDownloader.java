package br.com.infox.core.file.download;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.file.download.FileDownloader.Exporter;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import lombok.Getter;
import lombok.Setter;

@Stateless
public class ZipDownloader {
	
	private static final SimpleDateFormat formatoSufixoData = new SimpleDateFormat("yyyyMMdd_HHmm");
	
	public static final String ENCODING = Charsets.UTF_8.toString();
	
	@Inject
	protected ParametroManager parametroManager;
	@Inject
	protected FileDownloader fileDownloader;
	@Inject
	protected DocumentoManager documentoManager;
	
	protected static String nomeSistema;
	
	@Getter @Setter
	private int compressionLevel = 6;
	
	public static class ProgressOutputStream extends OutputStream {

		public static interface WriteProgressListener {
			public void bytesWritten(int bytes);
		}

		private OutputStream stream;
		private WriteProgressListener listener;
		private boolean insideWrite = false;

		public ProgressOutputStream(OutputStream stream, WriteProgressListener listener) {
			super();
			this.stream = stream;
			this.listener = listener;
		}

		@Override
		public void write(int b) throws IOException {
			stream.write(b);
			synchronized(this) {
				if(listener != null && !insideWrite) {
					listener.bytesWritten(1);                               
				}
			}
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			synchronized(this) {
				insideWrite = true;
				super.write(b, off, len);
				insideWrite = false;
			}
			if(listener != null) {
				listener.bytesWritten(len);
			}
		}

		@Override
		public void flush() throws IOException {
			stream.flush();
		}
	}
	
	protected String getNomeArquivo(Integer numeroDocumento, String nomeArquivoOriginal) {
		String nomeArquivo = nomeArquivoOriginal;
		if(numeroDocumento != null) {
			nomeArquivo = String.format("%04d-%s", numeroDocumento, nomeArquivo);
		}		
		return nomeArquivo;
	}
	
	protected String getNomeArquivo(Integer idDocumento, Set<String> nomesUtilizados) {
		final Pattern pattern = Pattern.compile("^(.+)\\.([^\\.]+)$");
		
		Documento documento = documentoManager.find(idDocumento);
		DocumentoBin documentoBin = documento.getDocumentoBin();
		Integer numero = documento.getNumeroDocumento();
		String nomeArquivoOriginal = documentoBin.getNomeArquivo();
		if(!documentoBin.isBinario()) {
			nomeArquivoOriginal = documento.getClassificacaoDocumento().getDescricao() + ".pdf";
		}
		nomeArquivoOriginal = Normalizer.normalize(nomeArquivoOriginal, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

		String nomeArquivo = getNomeArquivo(numero, nomeArquivoOriginal);
		
		String retorno = nomeArquivo;
		int cont = 1;
		
		while(nomesUtilizados.contains(retorno)) {
			Matcher matcher = pattern.matcher(nomeArquivo);
			if(matcher.find()) {
				String basename = matcher.group(1);
				String extension = matcher.group(2);
				retorno = String.format("%s_%d.%s", basename, cont++, extension);
			}
			else {
				retorno = nomeArquivo + "_" + cont++;
			}
		}
		
		return retorno;
	}
	
	protected void exportDocumento(Integer idDocumento, OutputStream outputStream, ZipOutputStream zos, String zipEntryName) throws IOException {
		Documento documento = documentoManager.find(idDocumento);
		zos.putNextEntry(new ZipEntry(zipEntryName));
		fileDownloader.export(documento.getDocumentoBin(), outputStream);
		outputStream.flush();
		zos.closeEntry();
	}
	
	public static class DocumentosComparator implements Comparator<Documento> {

		private String numero;
		
		public DocumentosComparator() {
		}
		
		private Integer nullFirstCompare(Object o1, Object o2) {
			if(o1 == null && o2 == null) {
				return 0;
			}
			if(o1 == null) {
				return -1;
			}
			if(o2 == null) {
				return 1;
			}
			return null;
		}
		
		private <T extends Comparable<T>> int nullFirstSafeCompare(T o1, T o2) {
			Integer result = nullFirstCompare(o1, o2);
			if(result != null) {
				return result;
			}
			return o1.compareTo(o2);
		}
		
		@Override
		public int compare(Documento o1, Documento o2) {
			Integer result = nullFirstCompare(o1, o2);
			if(result != null) {
				return result;
			}
			
			result = nullFirstSafeCompare(o1.getNumeroDocumento(), o2.getNumeroDocumento());
			if(result != 0) {
				return result;
			}
			return nullFirstSafeCompare(o1.getDescricao(), o2.getDescricao());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((numero == null) ? 0 : numero.hashCode());
			return result;
		}
	}
	
	protected void exportDocumentos(List<Integer> idsDocumentos, OutputStream outputStream, ZipOutputStream zos) throws IOException {
		Set<String> nomesUtilizados = new HashSet<>();
		
		EntityManager em = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Documento> cq = cb.createQuery(Documento.class);
		Root<Documento> doc = cq.from(Documento.class);
		doc.fetch(Documento_.documentoBin);
		cq.where(doc.get(Documento_.id).in(idsDocumentos));
		
		List<Documento> documentos = new ArrayList<>();
		for(Integer idDocumento : idsDocumentos) {
			Documento documento = documentoManager.find(idDocumento);
			documentos.add(documento);
		}
		documentos.sort(new DocumentosComparator());
		
		for(Documento documento: documentos) {
			Integer idDocumento = documento.getId();
			String nomeArquivo = getNomeArquivo(idDocumento, nomesUtilizados);
			exportDocumento(idDocumento, outputStream, zos, nomeArquivo);
			nomesUtilizados.add(nomeArquivo);
		}		
	}
	
	public void exportZipDocumentos(List<Integer> idsDocumentos, OutputStream outputStream) throws IOException {
		try(ZipOutputStream zos = new ZipOutputStream(outputStream, Charset.forName(ENCODING))) {
			zos.setLevel(compressionLevel);
			BufferedOutputStream bos = new BufferedOutputStream(zos);
			ProgressOutputStream pos = new ProgressOutputStream(bos, null);

			exportDocumentos(idsDocumentos, pos, zos);
			
			outputStream.flush();
		}
	}
	
	public class ZipDocumentosExporter implements Exporter {

		private List<Integer> idsDocumentos;
		
		public ZipDocumentosExporter(List<Integer> idsDocumentos) {
			super();
			this.idsDocumentos = idsDocumentos;
		}

		@Override
		public void export(OutputStream outputStream) throws IOException {
			exportZipDocumentos(idsDocumentos, outputStream);
		}
	}
	
	public void downloadZipDocumentos(List<Integer> idsDocumentos, String filename) throws IOException {
		Exporter exporter = new ZipDocumentosExporter(idsDocumentos);
		fileDownloader.downloadDocumento(exporter, MediaType.ZIP.toString(), filename);
	}
	
	protected String getNomeArquivoPadrao(String numeroProcesso) {
		if(nomeSistema == null) {
			Parametro parametroNomeSistema = parametroManager.getParametro("nomeSistema");
			nomeSistema = parametroNomeSistema.getValorVariavel().replaceAll("-", "").toUpperCase();
		}
		numeroProcesso = numeroProcesso.replaceAll("-", "");
		String sufixoData = formatoSufixoData.format(new Date());
		
		return String.format("%s_%s_%s.zip", nomeSistema, numeroProcesso, sufixoData);
	}
	
	protected Processo getProcesso(Integer idDocumento) {
		Documento documento = documentoManager.find(idDocumento);
		return documento.getPasta().getProcesso();
	}
	
	protected String getNumeroProcesso(List<Integer> idsDocumentos) {
		if(idsDocumentos.isEmpty()) {
			return null;
		}
		return getProcesso(idsDocumentos.iterator().next()).getNumeroProcesso();
	}
	
	protected String getNomeArquivoPadrao(List<Integer> idsDocumentos) {
		String numeroProcesso = getNumeroProcesso(idsDocumentos);
		return getNomeArquivoPadrao(numeroProcesso);		
	}
	
	public void downloadZipDocumentos(List<Integer> idsDocumentos) throws IOException {
		if(idsDocumentos.isEmpty()) {
			return;
		}
		String numeroProcesso = getNumeroProcesso(idsDocumentos);
		String nomeArquivo = getNomeArquivoPadrao(numeroProcesso);
		
		downloadZipDocumentos(idsDocumentos, nomeArquivo);
	}
	

}
