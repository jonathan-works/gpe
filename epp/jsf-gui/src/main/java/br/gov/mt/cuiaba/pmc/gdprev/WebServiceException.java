package br.gov.mt.cuiaba.pmc.gdprev;

import javax.xml.ws.WebFault;
import javax.xml.ws.http.HTTPException;

import lombok.Getter;

@Getter
@WebFault(name="WebServiceException")
public class WebServiceException extends HTTPException {

	private static final long serialVersionUID = 1L;
	
	private final String code;
	private final String message;
	
	public WebServiceException(int statusCode, String code, String message) {
		super(statusCode);
		this.code = code;
		this.message = message;
	}

}
