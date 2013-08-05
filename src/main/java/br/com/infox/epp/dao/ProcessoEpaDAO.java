package br.com.infox.epp.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.query.ProcessoEpaQuery;
import br.com.infox.epp.type.TipoPessoaEnum;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Item;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.ibpm.entity.Processo;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.HibernateUtil;

/**
 * Classe DAO para a entidade ProcessoEpa
 * @author Daniel
 *
 */
@Name(ProcessoEpaDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoEpaDAO extends GenericDAO {

	private static final long serialVersionUID = 8899227886410190168L;
	private static final LogProvider LOG = Logging.getLogProvider(ProcessoEpaDAO.class);
	public static final String NAME = "processoEpaDAO";

	public List<ProcessoEpa> listAllNotEnded() {
		List<ProcessoEpa> resultList = getNamedResultList
			(ProcessoEpaQuery.LIST_ALL_NOT_ENDED, null);
		return resultList;
	}

	public List<ProcessoEpa> listNotEnded(Fluxo fluxo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ProcessoEpaQuery.PARAM_FLUXO, fluxo);
		return getNamedResultList
					(ProcessoEpaQuery.LIST_NOT_ENDED_BY_FLUXO, map);
	}
	
	public ProcessoEpa getProcessoEpaByProcesso(Processo processo){
		return entityManager.find(ProcessoEpa.class, processo.getIdProcesso());
	}
	
	public List<PessoaFisica> getPessoaFisicaList(){
		Long idJbpm = ProcessInstance.instance().getId();
		String busca = "select pe from ProcessoEpa pe where pe.idJbpm = :idJbpm";
		Query query = EntityUtil.createQuery(busca).setParameter("idJbpm", idJbpm);
		ProcessoEpa pe = EntityUtil.getSingleResult(query);
		List<PessoaFisica> pessoaFisicaList = new ArrayList<PessoaFisica>();
		for (ParteProcesso parte : pe.getPartes()){
			if (parte.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.F)){
				pessoaFisicaList.add((PessoaFisica) HibernateUtil.removeProxy(parte.getPessoa()));
			}
		}
		return pessoaFisicaList;
	}
	
	public List<PessoaJuridica> getPessoaJuridicaList(){
		Long idJbpm = ProcessInstance.instance().getId();
		String busca = "select pe from ProcessoEpa pe where pe.idJbpm = :idJbpm";
		Query query = EntityUtil.createQuery(busca).setParameter("idJbpm", idJbpm);
		ProcessoEpa pe = EntityUtil.getSingleResult(query);
		List<PessoaJuridica> pessoaJuridicaList = new ArrayList<PessoaJuridica>();
		for (ParteProcesso parte : pe.getPartes()){
			if (parte.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.J)){
				pessoaJuridicaList.add((PessoaJuridica) HibernateUtil.removeProxy(parte.getPessoa()));
			}
		}
		return pessoaJuridicaList;
	}
	
	public boolean hasPartes(Processo processo){
		return hasPartes(processo.getIdJbpm());
	}
	
	public boolean hasPartes(Long idJbpm){
		String busca = "select pe from ProcessoEpa pe where pe.idJbpm = :idJbpm";
		Query query = EntityUtil.createQuery(busca).setParameter("idJbpm",idJbpm);
		ProcessoEpa pe = EntityUtil.getSingleResult(query);
		return (pe != null) && (pe.hasPartes());
	}
	
	
	/**
	 * Quando um processo necessita de partes, não é permitido inativar todas
	 * as partes do processo de uma vez.
	 * Esse método retorna falso (não há permissão de inativar) se o processo
	 * possuir uma única parte ativa no momento.
	 * */
	public Boolean podeInativarPartes(ProcessoEpa processoEpa){
		String hql = "select count(*) from ParteProcesso partes where partes.processo = :processoEpa and partes.ativo = true";
		return (Boolean) (((Long) EntityUtil.createQuery(hql).setParameter("processoEpa", processoEpa).getSingleResult()).compareTo(1L) > 0);
	}
	
	public Item getItemDoProcesso(int idProcesso){
		String query = "select o.itemDoProcesso from ProcessoEpa o where o.idProcesso =:idProcesso";
		return (Item) entityManager.createQuery(query).setParameter("idProcesso", idProcesso).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
    public Map<String, Object> getTempoGasto(ProcessoEpa processoEpa) {
		Query q = entityManager.createQuery(ProcessoEpaQuery.TEMPO_GASTO_PROCESSO_EPP_QUERY)
				.setParameter("idProcesso", processoEpa.getIdProcesso());
		Map<String,Object> result = null;
		try {
			result = (Map<String, Object>) q.getSingleResult();			
		} catch(NoResultException e) {
		    LOG.info(".getTempoGasto()", e);
		}
		return result;
	}
}