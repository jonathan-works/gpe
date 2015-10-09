package br.com.infox.core.exception;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

@Named
@RequestScoped
public class FailResponseAction {

    public final static String HEADER_ERROR_RESPONSE = "X-errorResponse";
    
    public void putHeader() {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.setHeader(HEADER_ERROR_RESPONSE, "true");
    }
}
