/**
 * 
 */
package br.com.infox.certificado.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;

import com.google.gson.Gson;

/**
 * @author erikliberal
 *
 */
@WebServlet(urlPatterns = CertificateServlet.SERVLET_PATH)
public class CertificateServlet extends HttpServlet {
	public static final String SERVLET_PATH = "/certificadodigital/signature";
	public static final String SIGNATURE_CONTEXT_KEY = "signatureContext";
    private static final long serialVersionUID = 1L;

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        new ContextualHttpServletRequest(request) {
            @Override
            public void process() throws Exception {
            	Gson gson = new Gson();
            	CertificateSignatureBundleBean bundle = gson.fromJson(request.getReader(), CertificateSignatureBundleBean.class);
            	
                Map<String, CertificateSignatureBean> value = new HashMap<>();
                List<CertificateSignatureBean> signatureBeanList = bundle.getSignatureBeanList();
                for (CertificateSignatureBean certificateSignatureBean : signatureBeanList) {
                    value.put(certificateSignatureBean.getMessageHash(), certificateSignatureBean);
                }
                Contexts.getApplicationContext().set(SIGNATURE_CONTEXT_KEY, value);
            }
        }.run();
    }
}
