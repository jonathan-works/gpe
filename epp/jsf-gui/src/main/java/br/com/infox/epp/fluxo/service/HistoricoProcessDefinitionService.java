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
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.epp.fluxo.entity.HistoricoProcessDefinition;
import br.com.infox.epp.fluxo.entity.HistoricoProcessDefinition_;
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
	@GenericDao
	private Dao<DefinicaoProcesso, Long> definicaoProcessoDao;
	
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public DefinicaoProcesso restaurar(HistoricoProcessDefinition historicoProcessDefinition) {
		DefinicaoProcesso definicaoProcesso = historicoProcessDefinition.getDefinicaoProcesso();

		String oldBpmn = definicaoProcesso.getBpmn();
		String oldProcessDefinition = definicaoProcesso.getXml();
		String oldSvg = definicaoProcesso.getSvg();

		registrarHistorico(definicaoProcesso);
		definicaoProcesso.setBpmn(historicoProcessDefinition.getBpmn());
		definicaoProcesso.setXml(historicoProcessDefinition.getProcessDefinition());
		definicaoProcesso.setSvg(historicoProcessDefinition.getSvg());
		try {
			return definicaoProcessoDao.update(definicaoProcesso);
		} catch (Exception e) {
		    definicaoProcesso.setBpmn(oldBpmn);
		    definicaoProcesso.setXml(oldProcessDefinition);
		    definicaoProcesso.setSvg(oldSvg);
			LOG.error("", e);
			throw new BusinessRollbackException("Erro ao restaurar a definição", e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void registrarHistorico(DefinicaoProcesso definicaoProcesso) {
		if (definicaoProcesso.getXml() != null) {
			HistoricoProcessDefinition novoHistorico = new HistoricoProcessDefinition();
			novoHistorico.setDefinicaoProcesso(definicaoProcesso);
			novoHistorico.setBpmn(definicaoProcesso.getBpmn());
			novoHistorico.setProcessDefinition(definicaoProcesso.getXml());
			novoHistorico.setSvg(definicaoProcesso.getSvg());
			novoHistorico.setRevisao(getMaiorRevisao(definicaoProcesso) + 1);
			
			if (getTotalHistoricos(definicaoProcesso) >= 20) {
				historicoProcessDefinitionDao.remove(getPrimeiroHistorico(definicaoProcesso));
			}
			
			historicoProcessDefinitionDao.persist(novoHistorico);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void limparHistoricos(DefinicaoProcesso definicaoProcesso) {
		try {
			historicoProcessDefinitionDao.getEntityManager().createQuery("delete from HistoricoProcessDefinition where definicaoProcesso = :definicaoProcesso")
				.setParameter("definicaoProcesso", definicaoProcesso).executeUpdate();
			historicoProcessDefinitionDao.getEntityManager().flush();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	private long getTotalHistoricos(DefinicaoProcesso definicaoProcesso) {
		CriteriaBuilder cb = historicoProcessDefinitionDao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<HistoricoProcessDefinition> root = query.from(HistoricoProcessDefinition.class);
		query.select(cb.count(root));
		query.where(cb.equal(root.get(HistoricoProcessDefinition_.definicaoProcesso), definicaoProcesso));
		return historicoProcessDefinitionDao.getEntityManager().createQuery(query).getSingleResult();
	}
	
	private HistoricoProcessDefinition getPrimeiroHistorico(DefinicaoProcesso definicaoProcesso) {
		CriteriaBuilder cb = historicoProcessDefinitionDao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<HistoricoProcessDefinition> query = cb.createQuery(HistoricoProcessDefinition.class);
		Root<HistoricoProcessDefinition> root = query.from(HistoricoProcessDefinition.class);
		query.where(cb.equal(root.get(HistoricoProcessDefinition_.definicaoProcesso), definicaoProcesso));
		
		Subquery<Date> subquery = query.subquery(Date.class);
		Root<HistoricoProcessDefinition> subRoot = subquery.from(HistoricoProcessDefinition.class);
		subquery.select(cb.least(subRoot.get(HistoricoProcessDefinition_.dataAlteracao)));
		subquery.where(cb.equal(subRoot.get(HistoricoProcessDefinition_.definicaoProcesso), definicaoProcesso));
		
		query.where(query.getRestriction(), cb.equal(root.get(HistoricoProcessDefinition_.dataAlteracao), subquery));
		
		return historicoProcessDefinitionDao.getEntityManager().createQuery(query).getSingleResult();
	}
	
	private int getMaiorRevisao(DefinicaoProcesso definicaoProcesso) {
		CriteriaBuilder cb = historicoProcessDefinitionDao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
		Root<HistoricoProcessDefinition> root = query.from(HistoricoProcessDefinition.class);
		query.where(cb.equal(root.get(HistoricoProcessDefinition_.definicaoProcesso), definicaoProcesso));
		query.select(cb.coalesce(cb.max(root.get(HistoricoProcessDefinition_.revisao)), 0));
		
		return historicoProcessDefinitionDao.getEntityManager().createQuery(query).getSingleResult();
	}
}
