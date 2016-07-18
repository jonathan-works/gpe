package br.com.infox.epp.login;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;

@Stateless
public class CookieCaptchaSearch {

	public <K>  K getSingleResult(TypedQuery<K> typedQuery) {
		List<K> result = typedQuery.setMaxResults(1).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
	public CookieCaptcha findByClientId(String clientId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CookieCaptcha> cq = cb.createQuery(CookieCaptcha.class);
        Root<CookieCaptcha> cookieCaptcha = cq.from(CookieCaptcha.class);
        cq.select(cookieCaptcha);
        cq.where(
            cb.equal(cookieCaptcha.get(CookieCaptcha_.clientId), clientId)
        );
        return getSingleResult(getEntityManager().createQuery(cq));		
	}
	
	public Long getTentativasLoginsInvalidos(Integer cookieId, Date since) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<LoginInvalido> loginInvalido = cq.from(LoginInvalido.class);
        //Path<CookieCaptcha> cookieCaptcha = loginInvalido.join(LoginInvalido_.cookieCaptcha);
        cq.select(cb.count(loginInvalido.get(LoginInvalido_.id)));
        cq.where(
            cb.equal(loginInvalido.get(LoginInvalido_.cookieCaptcha), cookieId),
            cb.greaterThanOrEqualTo(loginInvalido.get(LoginInvalido_.data), since)
        );
        return getSingleResult(getEntityManager().createQuery(cq));	
	}
	
	public List<LoginInvalido> listTentativasLoginInvalido(Integer cookieId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<LoginInvalido> cq = cb.createQuery(LoginInvalido.class);
        Root<LoginInvalido> loginInvalido = cq.from(LoginInvalido.class);
        //Path<CookieCaptcha> cookieCaptcha = loginInvalido.join(LoginInvalido_.cookieCaptcha);
        cq.where(
            cb.equal(loginInvalido.get(LoginInvalido_.cookieCaptcha), cookieId)
        );
        return getEntityManager().createQuery(cq).getResultList();		
	}
	
    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }	

}
