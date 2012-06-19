package br.com.infox.epa.webservice.globalweather.client.service;

/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FileDataDownload {

    final static int size = 1024;

    private String fileAddress;
    private File fileDownload;
    private int fileSize;

    public FileDataDownload(String fileAddress, File fileDownload) {
        this.fileAddress = fileAddress;
        this.fileDownload = fileDownload;
    }

    public File getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(File fileDownload) {
        this.fileDownload = fileDownload;
    }

    

    private void FileDownload(String fileAddress, File file)
            throws Exception {
        OutputStream os = null;
        URLConnection URLConn = null;

        // URLConnection class represents a communication link between the
        // application and a URL.

        InputStream is = null;
        try {
            URL fileUrl;
            byte[] buf;
            int byteRead, byteWritten = 0;
            fileUrl = new URL(fileAddress);
            os = new BufferedOutputStream(new FileOutputStream(file));
            //The URLConnection object is created by invoking the


            // openConnection method on a URL.
            URLConn = fileUrl.openConnection();
            fileSize = URLConn.getContentLength();
            is = URLConn.getInputStream();
            buf = new byte[size];
            while ((byteRead = is.read(buf)) != -1) {
                os.write(buf, 0, byteRead);
                byteWritten += byteRead;
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    }
    
    public static String getUrlAsString(String fileAddress)
			throws Exception {
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
	} catch (Exception e) {
		throw new Exception(e);
	} finally {
		try {
			if (br != null) { br.close(); };
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	return sb.toString();
}        
    
    public static List<String> getUrlAsList(String fileAddress)
    			throws Exception {
    	List<String> list = new ArrayList<String>();
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
    			list.add(line);
    			line = br.readLine();
    		}
    	} catch (Exception e) {
    		throw new Exception(e);
    	} finally {
    		try {
    			br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return list;
    }    


}