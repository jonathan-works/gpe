package br.com.infox.core.certificado;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.itx.component.Util;
import br.com.itx.util.FileUtil;

public final class CertificadoLog {
	private static final String FILE_NAME = "certificado.log";
	private static final char BARRA = File.separatorChar;
	private static final String BR = System.getProperty("line.separator");
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
	private static File logFileDir;
	
	private CertificadoLog(){
	    super();
	}
	 
	private static synchronized void initLogDir() throws IOException {
		if (logFileDir == null || !logFileDir.exists()) {
			logFileDir = new File(getLogDir() + getFilename());
			if (!logFileDir.exists()) {
				logFileDir.createNewFile();
			}
		}
	}
	
	private static String getLogDir() {
		String dir = new Util().getContextRealPath() + BARRA + "WEB-INF" + 
			BARRA + "log" + BARRA;
		File logDir = new File(dir);
		if (!logDir.exists() || !logDir.isDirectory()) {
			logDir.mkdirs();
		}
		return dir;
	}
	
	public static synchronized void executeLog(String log) {
		BufferedWriter writer = null;
		try {
			initLogDir();
			System.out.println(logFileDir);
			writer = new BufferedWriter(new FileWriter(logFileDir, true));
			writer.write("[");
			writer.write(DF.format(new Date()) + "]: ");
			writer.write(log);
			writer.write(BR);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtil.close(writer);
		}
	}	

	public static void executeLog(Certificate[] certChain) {
		try {
			executeLog("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static String getTextLogFile() throws IOException {
		initLogDir();
		if (logFileDir != null && logFileDir.exists()) {
			Reader r = null;
			BufferedReader reader = null;
			try {
				r = new FileReader(logFileDir);
				reader = new BufferedReader(r);
				
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();
				while (line != null) {
					sb.append(line);
					sb.append(BR);
					line = reader.readLine();
				}
				return sb.toString();
			} finally {
				FileUtil.close(r);
				FileUtil.close(reader);
			}
		}
		return null;
	}
	
	protected static String getFilename() {
		return FILE_NAME;
	}
	
}