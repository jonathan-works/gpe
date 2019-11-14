/**
 * 
 */
package br.com.infox.epp.certificado.dao;

import static br.com.infox.epp.certificado.query.CertificateSignatureGroupQuery.GET_BY_TOKEN;
import static br.com.infox.epp.certificado.query.CertificateSignatureGroupQuery.PARAM_TOKEN;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;

/**
 * @author erikliberal
 *
 */
@Stateless
@AutoCreate
@Name(CertificateSignatureGroupDAO.NAME)
public class CertificateSignatureGroupDAO extends DAO<CertificateSignatureGroup>{

    public static final String NAME = "certificateSignatureGroupDAO";
    private static final long serialVersionUID = 1L;
    
    public CertificateSignatureGroup getByToken(String token) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TOKEN, token);
        return getNamedSingleResult(GET_BY_TOKEN, parameters);
    }

}
