package br.com.infox.epp.fluxo.service;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.HistoricoProcessDefinition;
import br.com.infox.epp.fluxo.entity.HistoricoProcessDefinition_;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class HistoricoProcessDefinitionService {
	
	private static final LogProvider LOG = Logging.getLogProvider(HistoricoProcessDefinitionService.class);
	
	@Inject
	@GenericDao
	private Dao<HistoricoProcessDefinition, Long> historicoProcessDefinitionDao;
	
	@Inject
	private FluxoManager fluxoManager;
	
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Fluxo restaurar(HistoricoProcessDefinition historicoProcessDefinition) {
		Fluxo fluxo = historicoProcessDefinition.getFluxo();

		String oldBpmn = fluxo.getBpmn();
		String oldProcessDefinition = fluxo.getXml();
		String oldSvg = fluxo.getSvg();

		registrarHistorico(fluxo);
		fluxo.setBpmn(historicoProcessDefinition.getBpmn());
		fluxo.setXml(historicoProcessDefinition.getProcessDefinition());
		fluxo.setSvg(historicoProcessDefinition.getSvg());
		try {
			return fluxoManager.update(fluxo);
		} catch (Exception e) {
			fluxo.setBpmn(oldBpmn);
			fluxo.setXml(oldProcessDefinition);
			fluxo.setSvg(oldSvg);
			LOG.error("", e);
			throw new BusinessRollbackException("Erro ao restaurar a definição", e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void registrarHistorico(Fluxo fluxo) {
		if (fluxo.getXml() != null) {
			HistoricoProcessDefinition novoHistorico = new HistoricoProcessDefinition();
			novoHistorico.setFluxo(fluxo);
			novoHistorico.setBpmn(fluxo.getBpmn());
			novoHistorico.setProcessDefinition(fluxo.getXml());
			novoHistorico.setSvg(fluxo.getSvg());
			novoHistorico.setRevisao(getMaiorRevisao(fluxo) + 1);
			
			if (getTotalHistoricos(fluxo) >= 20) {
				historicoProcessDefinitionDao.remove(getPrimeiroHistorico(fluxo));
			}
			
			historicoProcessDefinitionDao.persist(novoHistorico);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void limparHistoricos(Fluxo fluxo) {
		try {
			historicoProcessDefinitionDao.getEntityManager().createQuery("delete from HistoricoProcessDefinition where fluxo = :fluxo")
				.setParameter("fluxo", fluxo).executeUpdate();
			historicoProcessDefinitionDao.getEntityManager().flush();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	private long getTotalHistoricos(Fluxo fluxo) {
		CriteriaBuilder cb = historicoProcessDefinitionDao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<HistoricoProcessDefinition> root = query.from(HistoricoProcessDefinition.class);
		query.select(cb.count(root));
		query.where(cb.equal(root.get(HistoricoProcessDefinition_.fluxo), fluxo));
		return historicoProcessDefinitionDao.getEntityManager().createQuery(query).getSingleResult();
	}
	
	private HistoricoProcessDefinition getPrimeiroHistorico(Fluxo fluxo) {
		CriteriaBuilder cb = historicoProcessDefinitionDao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<HistoricoProcessDefinition> query = cb.createQuery(HistoricoProcessDefinition.class);
		Root<HistoricoProcessDefinition> root = query.from(HistoricoProcessDefinition.class);
		query.where(cb.equal(root.get(HistoricoProcessDefinition_.fluxo), fluxo));
		
		Subquery<Date> subquery = query.subquery(Date.class);
		Root<HistoricoProcessDefinition> subRoot = subquery.from(HistoricoProcessDefinition.class);
		subquery.select(cb.least(subRoot.get(HistoricoProcessDefinition_.dataAlteracao)));
		subquery.where(cb.equal(subRoot.get(HistoricoProcessDefinition_.fluxo), fluxo));
		
		query.where(query.getRestriction(), cb.equal(root.get(HistoricoProcessDefinition_.dataAlteracao), subquery));
		
		return historicoProcessDefinitionDao.getEntityManager().createQuery(query).getSingleResult();
	}
	
	private int getMaiorRevisao(Fluxo fluxo) {
		CriteriaBuilder cb = historicoProcessDefinitionDao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
		Root<HistoricoProcessDefinition> root = query.from(HistoricoProcessDefinition.class);
		query.where(cb.equal(root.get(HistoricoProcessDefinition_.fluxo), fluxo));
		query.select(cb.coalesce(cb.max(root.get(HistoricoProcessDefinition_.revisao)), 0));
		
		return historicoProcessDefinitionDao.getEntityManager().createQuery(query).getSingleResult();
	}
}
