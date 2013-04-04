package br.com.infox.epa.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ProcessInstance;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.query.ProcessoEpaQuery;
import br.com.infox.epa.type.TipoPessoaEnum;
import br.com.infox.ibpm.entity.Fluxo;
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
		String hql = "select o from ProcessoEpa o where o.idProcesso = :idProcesso";
		return (ProcessoEpa) entityManager.createQuery(hql)
				.setParameter("idProcesso", processo.getIdProcesso())
				.getSingleResult();
	}
	
	public List<PessoaFisica> getPessoaFisicaList(){
		Long idjbpm_ = ProcessInstance.instance().getId();
		String busca = "select pe from ProcessoEpa pe where pe.idJbpm = :idJbpm";
		Query query = EntityUtil.createQuery(busca.toString()).setParameter("idJbpm", idjbpm_);
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
		Long idjbpm_ = ProcessInstance.instance().getId();
		String busca = "select pe from ProcessoEpa pe where pe.idJbpm = :idJbpm";
		Query query = EntityUtil.createQuery(busca.toString()).setParameter("idJbpm", idjbpm_);
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
		String busca = "select pe from ProcessoEpa pe where pe.idJbpm = :idJbpm";
		Query query = EntityUtil.createQuery(busca.toString()).setParameter("idJbpm", processo.getIdJbpm());
		ProcessoEpa pe = EntityUtil.getSingleResult(query);
		return (pe != null) && (pe.hasPartes());
	}
	
}