package br.com.infox.core.report;

import java.io.IOException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EncodingUtils;

import br.com.infox.core.exception.FailResponseAction;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Singleton
@Startup
@Named
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class RequestInternalPageService implements Serializable {

	public static final LogProvider LOG = Logging.getLogProvider(RequestInternalPageService.class);
	
	private static final long serialVersionUID = 1L;
	public static final String KEY_HEADER_NAME = "X-Key";

	@Inject
	private ApplicationServerService applicationServerService;
	
	private String contextPath;
	private UUID key;
    
	private final static BitSet bitSet = new BitSet(256);
	
	static{
        for (int i = 'a'; i <= 'z'; i++) {
            bitSet.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            bitSet.set(i);
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            bitSet.set(i);
        }
        // special chars
        bitSet.set('&');
        bitSet.set('=');
        bitSet.set('/');
        bitSet.set('?');
        bitSet.set('-');
        bitSet.set('_');
        bitSet.set('.');
        bitSet.set('*');
        // blank to be replaced with +
        bitSet.set(' ');
    }

	@PostConstruct
	private void init() {
		key = UUID.randomUUID();
	}

	public UUID getKey() {
		return key;
	}

	public boolean isValid(String candidateKey) {
		try {
			UUID candidate = UUID.fromString(candidateKey);
			return key.equals(candidate);
		} catch (NullPointerException | IllegalArgumentException e) {
			return false;
		}
	}

	public String getInternalPage(String pagePath) throws HttpException, IOException {
	    String fullPath = applicationServerService.getBaseResquestUrl() + getContextPath() + pagePath;
		return requestInternalPage(fullPath);
	}

	public byte[] getInternalPageAsPdf(String pagePath) throws HttpException, IOException {
        String fullPath = applicationServerService.getBaseResquestUrl() + getContextPath() + pagePath;
        return requestInternalPageAsPdf(fullPath);
    }
 
	private String requestInternalPage(String fullPath) throws IOException,	HttpException {
	    HttpClient httpclient = HttpClients.createDefault();
	    HttpGet httpGet = new HttpGet(fullPath);
	    httpGet.addHeader(KEY_HEADER_NAME, getKey().toString());
	    HttpResponse response = httpclient.execute(httpGet);
		Header errorHeader = response.getFirstHeader(FailResponseAction.HEADER_ERROR_RESPONSE);
		if (errorHeader != null && !errorHeader.getValue().isEmpty()) {
		    throw new BusinessException("A requisição interna falhou");
		}
		return IOUtils.toString(response.getEntity().getContent());
	}

	private byte[] requestInternalPageAsPdf(String fullPath) throws IOException, HTTPException {
	    HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(fullPath);
        httpGet.addHeader(KEY_HEADER_NAME, getKey().toString());
        HttpResponse response = httpclient.execute(httpGet);
        Header errorHeader = response.getFirstHeader(FailResponseAction.HEADER_ERROR_RESPONSE);
	    if (errorHeader != null && !errorHeader.getValue().isEmpty()) {
	        throw new BusinessException("A requisição interna falhou");
	    }
	    return IOUtils.toByteArray(response.getEntity().getContent());
	}

	public String getContextPath() {
		return this.contextPath;
	}
	
	public String getResquestUrlRest() {
        return applicationServerService.getBaseResquestUrl() + contextPath + "/rest";
    }

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

    public static String encodeQuery(String unescaped, String charset) {
        return encode(unescaped, bitSet, charset);
    }

    public static String encode(String unescaped, BitSet allowed, String charset) {
        byte[] rawdata = URLCodec.encodeUrl(allowed, EncodingUtils.getBytes(unescaped, charset));
        return EncodingUtils.getAsciiString(rawdata);
    }
  
}
