package br.com.infox.epa.webservice.globalweather.client.service;

/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import br.com.itx.util.FileUtil;

public class FileDataDownload {

    final static int size = 1024;

    private File fileDownload;

    public FileDataDownload(File fileDownload) {
        this.fileDownload = fileDownload;
    }

    public File getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(File fileDownload) {
        this.fileDownload = fileDownload;
    }

    public static String getUrlAsString(String fileAddress)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		URLConnection URLConn = null;
	
		// URLConnection class represents a communication link between the
		// application and a URL.
		BufferedReader br = null;
		try {
			URL fileUrl;
			fileUrl = new URL(fileAddress);
			//The URLConnection object is created by invoking the
	
			URLConn = fileUrl.openConnection();
			br = new BufferedReader(new InputStreamReader(URLConn.getInputStream()));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			FileUtil.close(br);
		}
		return sb.toString();
	}        
    
    public static List<String> getUrlAsList(String fileAddress) throws IOException {
    	List<String> list = new ArrayList<String>();
    	URLConnection urlConn = null;

    	// URLConnection class represents a communication link between the
    	// application and a URL.
    	BufferedReader br = null;
    	try {
    		URL fileUrl;
    		fileUrl = new URL(fileAddress);
    		//The URLConnection object is created by invoking the

    		urlConn = fileUrl.openConnection();
    		br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
    		String line = br.readLine();
    		while (line != null) {
    			list.add(line);
    			line = br.readLine();
    		}
    	} catch (IOException e) {
    		throw new IOException(e);
    	} finally {
    		FileUtil.close(br);
    	}
    	return list;
    }    


}