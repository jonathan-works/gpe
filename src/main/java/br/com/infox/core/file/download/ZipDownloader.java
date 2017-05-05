package br.com.infox.core.file.download;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.google.common.net.MediaType;

import br.com.infox.core.file.download.FileDownloader.Exporter;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import lombok.Getter;
import lombok.Setter;

@Stateless
public class ZipDownloader {
	
	private static final SimpleDateFormat formatoSufixoData = new SimpleDateFormat("yyyyMMdd_HHmm");
	
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
	
	protected String getNomeArquivo(Integer numeroDocumento, String descricao, String extensao) {
		String nomeArquivo = String.format("%s.%s", descricao, extensao);
		if(numeroDocumento != null) {
			nomeArquivo = String.format("%04d-%s", numeroDocumento, nomeArquivo);
		}		
		return nomeArquivo;
	}
	
	protected String getNomeArquivo(Integer idDocumentoBin) {
		DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
		Documento documento = documentoBin.getDocumentoList().iterator().next();
		Integer numero = documento.getNumeroDocumento();
		String descricao = documento.getDescricao();

		return getNomeArquivo(numero, descricao, documentoBin.getExtensao());
	}
	
	protected void exportDocumento(Integer idDocumentoBin, OutputStream outputStream, ZipOutputStream zos) throws IOException {
		DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
		zos.putNextEntry(new ZipEntry(documentoBin.getNomeArquivo()));
		fileDownloader.export(documentoBin, outputStream);
		outputStream.flush();
		zos.closeEntry();
	}
	
	protected void exportDocumentos(List<Integer> idsDocumentoBin, OutputStream outputStream, ZipOutputStream zos) throws IOException {
		for(Integer idDocumentoBin : idsDocumentoBin) {
			exportDocumento(idDocumentoBin, outputStream, zos);
		}		
	}
	
	public void exportZipDocumentos(List<Integer> idsDocumentoBin, OutputStream outputStream) throws IOException {
		try(ZipOutputStream zos = new ZipOutputStream(outputStream)) {
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
