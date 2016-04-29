package br.com.infox.core.report;

import java.io.IOException;
import java.io.Serializable;
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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

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
		HttpClient client = new HttpClient();
		HttpMethod getMethod = new GetMethod(fullPath);
		getMethod.addRequestHeader(KEY_HEADER_NAME, getKey().toString());
		client.executeMethod(getMethod);
		Header errorHeader = getMethod.getResponseHeader(FailResponseAction.HEADER_ERROR_RESPONSE);
		if (errorHeader != null && !errorHeader.getValue().isEmpty()) {
		    throw new BusinessException("A requisição interna falhou");
		}
		return getMethod.getResponseBodyAsString();
	}

	private byte[] requestInternalPageAsPdf(String fullPath) throws IOException, HTTPException {
	    HttpClient client = new HttpClient();
	    HttpMethod getMethod = new GetMethod(fullPath);
	    getMethod.addRequestHeader(KEY_HEADER_NAME, getKey().toString());
	    client.executeMethod(getMethod);
	    Header errorHeader = getMethod.getResponseHeader(FailResponseAction.HEADER_ERROR_RESPONSE);
	    if (errorHeader != null && !errorHeader.getValue().isEmpty()) {
	        throw new BusinessException("A requisição interna falhou");
	    }
	    return getMethod.getResponseBody();
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

}
