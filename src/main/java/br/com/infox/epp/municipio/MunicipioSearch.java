package br.com.infox.epp.municipio;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.system.Parametros;

@Stateless
public class MunicipioSearch {
	
	private EntityManager getEntityManager(){
		return EntityManagerProducer.getEntityManager();
	}
	
	public Municipio getByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Municipio> cq = cb.createQuery(Municipio.class);
		
		Root<Municipio> municipio = cq.from(Municipio.class);
		
		cq = cq.select(municipio).where(cb.equal(municipio.get(Municipio_.codigo), codigo));
		
		try {
			return getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Municipio> getMunicipiosAtivosEstadoSistema() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Municipio> query = cb.createQuery(Municipio.class);
		Root<Municipio> from = query.from(Municipio.class);
		Join<Municipio, Estado> estado = from.join(Municipio_.estado);
		query.where(cb.isTrue(from.get(Municipio_.ativo)));
		if (Parametros.CODIGO_UF_SISTEMA != null && !Parametros.CODIGO_UF_SISTEMA.getValue().trim().isEmpty() &&
				!Parametros.CODIGO_UF_SISTEMA.getValue().equals("-1")) {
			query.where(query.getRestriction(), cb.equal(estado.get(Estado_.codigo), Parametros.CODIGO_UF_SISTEMA.getValue()));
		}
		query.orderBy(cb.asc(from.get(Municipio_.nome)));
		return getEntityManager().createQuery(query).getResultList();
	}
	
	public List<Municipio> getMunicipiosAtivosByCodigoUf(String codigoUf) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Municipio> query = cb.createQuery(Municipio.class);
		Root<Municipio> from = query.from(Municipio.class);
		Join<Municipio, Estado> estado = from.join(Municipio_.estado, JoinType.INNER);
		query.where(cb.isTrue(from.get(Municipio_.ativo)), cb.equal(estado.get(Estado_.codigo), codigoUf));
		query.orderBy(cb.asc(from.get(Municipio_.nome)));
		return getEntityManager().createQuery(query).getResultList();
	}
}
