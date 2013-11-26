package br.com.infox.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jboss.seam.util.RandomStringUtils;

import br.com.infox.core.exception.ExcelExportException;
import br.com.itx.component.FileHome;
import br.com.itx.component.Util;
import br.com.itx.util.FileUtil;

public class ExcelExportUtil {
	private XLSTransformer transformer = new XLSTransformer();
	private Map<String, Object> bean = new HashMap<String, Object>();

	private final String urlTemplate;
	private final String fileNameDownload;
	private final String fileNameTemp = criarNomeArquivoTemporario();

	public ExcelExportUtil(String urlTemplate, String fileNameDownload) {
		this.urlTemplate = urlTemplate;
		this.fileNameDownload = fileNameDownload;
	}
	
	public void download() throws ExcelExportException {
		transformXLSToFileTemp();

		downloadXLSFileTemp();
		
		File tempFile = new File(fileNameTemp);
		tempFile.delete();
	}
	
	public static void downloadXLS(String urlTemplate, Map<String, Object> bean, String fileNameDownload) throws ExcelExportException {
		ExcelExportUtil util = new ExcelExportUtil(urlTemplate, fileNameDownload);
		util.setBean(bean);
		util.download();
	}
	
	private boolean createDir() throws IOException {
		boolean result = false;

		File f = new File(fileNameTemp.substring(0, fileNameTemp.lastIndexOf('/')+1 ));
		if (!f.exists()) {
			result = f.mkdirs();
		}
		return result;
	}
	
	private void transformXLSToFileTemp() throws ExcelExportException {
		try {
			createDir();
			transformer.transformXLS(urlTemplate, getBean(), fileNameTemp);
		} catch (ParsePropertyException e) {
			throw new ExcelExportException(e);
		} catch (InvalidFormatException e) {
			throw new ExcelExportException(e);
		} catch (IOException e) {
			throw new ExcelExportException(e);
		}
	}
	
	private String criarNomeArquivoTemporario() {
		StringBuilder sb = new StringBuilder();
		sb.append(new Util().getContextRealPath());
		sb.append("/WEB-INF/temp/");
		sb.append(MessageFormat.format("{0,date,kkmmss}", new Date()));
		sb.append(RandomStringUtils.randomAlphanumeric(6));
		sb.append(".xls");
		return sb.toString();
	}
	
	private void downloadXLSFileTemp() throws ExcelExportException {
		byte[] content = readFile(new File(fileNameTemp));
		
        FileHome home = FileHome.instance();
        home.setData(content);
        home.setContentType("application/xls");
        home.setFileName(fileNameDownload);
        home.download();
	}
	
	private byte[] readFile(File arquivo) throws ExcelExportException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(arquivo);
			byte[] bytes = new byte[fis.available()];
	        fis.read(bytes, 0, fis.available());
	        return bytes;
		} catch (FileNotFoundException e) {
			throw new ExcelExportException(e);
		} catch (IOException e) {
			throw new ExcelExportException(e);
		} finally {
			FileUtil.close(fis);
		}
	}

	public void setColumnsToHide(short[] columnsToHide) {
		transformer.setColumnsToHide(columnsToHide);
	}

	public short[] getColumnsToHide() {
		return transformer.getColumnsToHide();
	}

	public void setBean(Map<String, Object> bean) {
		this.bean = bean;
	}

	public Map<String, Object> getBean() {
		return bean;
	}
}
