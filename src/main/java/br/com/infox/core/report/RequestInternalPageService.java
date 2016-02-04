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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import br.com.infox.core.exception.FailResponseAction;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.seam.exception.BusinessException;

@Singleton
@Startup
@Named
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class RequestInternalPageService implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String KEY_HEADER_NAME = "X-Key";
	
	@Inject
	private ApplicationServerService applicationServerService;
	
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
		String path = applicationServerService.getResquestUrl(pagePath);
		return requestInternalPage(path);
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
	
}
