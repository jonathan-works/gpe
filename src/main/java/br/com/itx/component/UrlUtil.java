package br.com.itx.component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class UrlUtil {
    
    private UrlUtil(){
        super();
    }
	
	public static InputStream getInputStreamUrl(String fileAddress) throws IOException {
		URLConnection urlConn = null;

		// URLConnection class represents a communication link between the
		// application and a URL.
		URL fileUrl = new URL(fileAddress);

		// openConnection method on a URL.
		urlConn = fileUrl.openConnection();
		return urlConn.getInputStream();
	}	

}
