/**
 * 
 */
package br.com.infox.epp.certificado.query;

/**
 * @author erikliberal
 *
 */
public interface CertificateSignatureGroupQuery {

    String PARAM_TOKEN = "token";
    String GET_BY_TOKEN = "CertificateSignatureGroup.getByToken";
    String GET_BY_TOKEN_QUERY = "select c from CertificateSignatureGroup c fetch all properties where c.token=:"
            + PARAM_TOKEN;

}