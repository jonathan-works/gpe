package br.com.infox.core.file.download;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
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
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
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
	protected DocumentoBinManager documentoBinManager;
	
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
	
	protected String getNomeArquivo(Integer idDocumentoBin, Set<String> nomesUtilizados) {
		final Pattern pattern = Pattern.compile("^(.+)\\.([^\\.]+)$");
		
		DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
		Documento documento = documentoBin.getDocumentoList().iterator().next();
		Integer numero = documento.getNumeroDocumento();
		String nomeArquivoOriginal = documentoBin.getNomeArquivo();

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
	
	protected void exportDocumento(Integer idDocumentoBin, OutputStream outputStream, ZipOutputStream zos, String zipEntryName) throws IOException {
		DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
		zos.putNextEntry(new ZipEntry(zipEntryName));
		fileDownloader.export(documentoBin, outputStream);
		outputStream.flush();
		zos.closeEntry();
	}
	
	protected void exportDocumentos(List<Integer> idsDocumentoBin, OutputStream outputStream, ZipOutputStream zos) throws IOException {
		Set<String> nomesUtilizados = new HashSet<>();
		
		EntityManager em = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentoBin> cq = cb.createQuery(DocumentoBin.class);
		Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
		documentoBin.fetch(DocumentoBin_.documentoList);
		cq.where(documentoBin.get(DocumentoBin_.id).in(idsDocumentoBin));
		
		for(Integer idDocumentoBin : idsDocumentoBin) {
			String nomeArquivo = getNomeArquivo(idDocumentoBin, nomesUtilizados);
			exportDocumento(idDocumentoBin, outputStream, zos, nomeArquivo);
			nomesUtilizados.add(nomeArquivo);
		}		
	}
	
	public void exportZipDocumentos(List<Integer> idsDocumentoBin, OutputStream outputStream) throws IOException {
		try(ZipOutputStream zos = new ZipOutputStream(outputStream, Charset.forName(ENCODING))) {
			zos.setLevel(compressionLevel);
			BufferedOutputStream bos = new BufferedOutputStream(zos);
			ProgressOutputStream pos = new ProgressOutputStream(bos, null);

			exportDocumentos(idsDocumentoBin, pos, zos);
			
			outputStream.flush();
		}
	}
	
	public class ZipDocumentosExporter implements Exporter {

		private List<Integer> idsDocumentoBin;
		
		public ZipDocumentosExporter(List<Integer> idsDocumentoBin) {
			super();
			this.idsDocumentoBin = idsDocumentoBin;
		}

		@Override
		public void export(OutputStream outputStream) throws IOException {
			exportZipDocumentos(idsDocumentoBin, outputStream);
		}
	}
	
	public void downloadZipDocumentos(List<Integer> idsDocumentoBin, String filename) throws IOException {
		Exporter exporter = new ZipDocumentosExporter(idsDocumentoBin);
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
	
	protected Processo getProcesso(Integer idDocumentoBin) {
		DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
		return documentoBin.getDocumentoList().get(0).getPasta().getProcesso();
	}
	
	protected String getNumeroProcesso(List<Integer> idsDocumentoBin) {
		if(idsDocumentoBin.isEmpty()) {
			return null;
		}
		return getProcesso(idsDocumentoBin.iterator().next()).getNumeroProcesso();
	}
	
	protected String getNomeArquivoPadrao(List<Integer> idsDocumentoBin) {
		String numeroProcesso = getNumeroProcesso(idsDocumentoBin);
		return getNomeArquivoPadrao(numeroProcesso);		
	}
	
	public void downloadZipDocumentos(List<Integer> idsDocumentoBin) throws IOException {
		if(idsDocumentoBin.isEmpty()) {
			return;
		}
		String numeroProcesso = getNumeroProcesso(idsDocumentoBin);
		String nomeArquivo = getNomeArquivoPadrao(numeroProcesso);
		
		downloadZipDocumentos(idsDocumentoBin, nomeArquivo);
	}
	

}
