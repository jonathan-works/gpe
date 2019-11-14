package br.com.infox.epp.processo.linkExterno;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.joda.time.DateTime;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.exception.SystemException;
import br.com.infox.core.exception.SystemExceptionFactory;
import br.com.infox.core.net.UrlBuilder;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.system.Parametros;
import br.com.infox.jwt.JWT;
import br.com.infox.jwt.JWTBuilder;
import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.claims.JWTRegisteredClaims;
import br.com.infox.jwt.encryption.Algorithm;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.security.rsa.RSAErrorCodes;
import br.com.infox.security.rsa.RSAUtil;

@Stateless
public class LinkAplicacaoExternaService {

    private static final LogProvider LOG = Logging.getLogProvider(LinkAplicacaoExternaService.class);
    
	@Inject
    @GenericDao
    private Dao<LinkAplicacaoExterna, Integer> dao;
    
    public LinkAplicacaoExterna findById(Integer id) {
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void salvar(LinkAplicacaoExterna entity) {
        entity.setAtivo(Boolean.TRUE);
        if (entity.getId() != null) {
            dao.update(dao.findById(entity.getId()));
        } else {
            dao.persist(entity);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(LinkAplicacaoExterna entity) {
        entity.setAtivo(Boolean.FALSE);
        dao.update(entity);
    }
    
    String appendQueriesToUrl(String urlString, Collection<Entry<String,String>> queries){
        return new UrlBuilder(urlString).queries(queries).build();
    }
    
    public String appendJWTTokenToUrlQuery(LinkAplicacaoExterna linkAplicacaoExterna, Collection<Entry<JWTClaim,Object>> claims){
        try {
        	String jwtToken = generateTokenFor(claims);
        	return new UrlBuilder(linkAplicacaoExterna.getUrl()).query("epp.auth.jwt", jwtToken).build();
        } catch (SystemException e){
        	if (e.getErrorCode() instanceof RSAErrorCodes){
        		LOG.warn("Erro ao gerar token JWT", e);
        		return new UrlBuilder(linkAplicacaoExterna.getUrl()).build();
        	}
        	throw e;
        }
    }

    private byte[] getPrivateKey(){
        String base64RsaKey = Parametros.EPP_API_RSA_PRIVATE_KEY.getValue();
        if (base64RsaKey == null || base64RsaKey.isEmpty()){
            throw SystemExceptionFactory.create(RSAErrorCodes.INVALID_PRIVATE_KEY_STRUCTURE)
            .set(Parametros.EPP_API_RSA_PRIVATE_KEY.getLabel(), base64RsaKey);
        }
        return RSAUtil.getPrivateKeyFromBase64(base64RsaKey).getEncoded();
    }
    
    String generateTokenFor(Collection<Entry<JWTClaim, Object>> claims) {
        JWTBuilder jwtBuilder = JWT.builder();
        DateTime issuedDate = new DateTime();
        
        jwtBuilder.addClaim(JWTRegisteredClaims.ISSUER, Beans.getReference(PathResolver.class).getUrlProject());
        String string = "uid";
        jwtBuilder.addClaim(JWTRegisteredClaims.JWT_ID, string);
        jwtBuilder.addClaim(JWTRegisteredClaims.ISSUED_AT, issuedDate.toDate().getTime() / 1000);
        jwtBuilder.addClaim(JWTRegisteredClaims.EXPIRATION_DATE, issuedDate.plusMinutes(1).toDate().getTime() / 1000);
        jwtBuilder.setAlgorithm(Algorithm.RS256).setPrivateKey(getPrivateKey());
        for (Iterator<Entry<JWTClaim, Object>> iterator = claims.iterator(); iterator.hasNext();) {
            Entry<JWTClaim, Object> entry = iterator.next();
            jwtBuilder.addClaim(entry.getKey(), entry.getValue());
        }
        return jwtBuilder.build();
    }
    
}
