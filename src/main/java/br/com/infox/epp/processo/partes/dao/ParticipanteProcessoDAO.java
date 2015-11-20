package br.com.infox.epp.processo.partes.dao;

import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_PAI_TIPO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_TIPO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.EXISTE_PARTICIPANTE_FILHO_BY_PROCESSO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_ID_PARTICIPANTE;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_PARTICIPANTE_PAI;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_PESSOA;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_PESSOA_PARTICIPANTE_FILHO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_PROCESSO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_TIPO_PARTE;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_TYPED_NAME;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTES_BY_PROCESSO_PARTICIPANTE_FILHO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTES_PROCESSO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTES_PROCESSOS_BY_PARTIAL_NAME;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTES_PROCESSO_RAIZ;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTE_BY_PESSOA_FETCH;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PESSOA_BY_PARTICIPANTE_PROCESSO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso_;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(ParticipanteProcessoDAO.NAME)
@Stateless
public class ParticipanteProcessoDAO extends DAO<ParticipanteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "participanteProcessoDAO";
    
    public void lockPessimistic(Processo processo){
    	if (!getEntityManager().contains(processo)){
    		processo = getEntityManager().merge(processo);
    	}
    	getEntityManager().lock(processo, LockModeType.PESSIMISTIC_READ);
    }
    
    public ParticipanteProcesso getParticipanteProcessoByPessoaProcesso(Pessoa pessoa, Processo processo){
    	Map<String, Object> params = new HashMap<>();
    	params.put(PARAM_PESSOA, pessoa);
    	params.put(PARAM_PROCESSO, processo);
    	return getNamedSingleResult(PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO, params);
    }
    
    
    public List<Pessoa> getPessoasFisicasParticipantesProcesso(Processo processo){
		String query = "select distinct p from ParticipanteProcesso pp "
				+ "inner join  pp.pessoa p "
				+ "where pp.processo = :processo and p.tipoPessoa = '"+TipoPessoaEnum.F+ "' ";
		return getEntityManager().createQuery(query,Pessoa.class).setParameter("processo", processo).getResultList();
    }
    public boolean existeParticipanteByPessoaProcessoPaiTipo(Pessoa pessoa, 
    		Processo processo, ParticipanteProcesso pai, TipoParte tipo){
    	Map<String, Object> params = new HashMap<>(4);
    	params.put(PARAM_PESSOA, pessoa);
    	params.put(PARAM_PROCESSO, processo);
    	params.put(PARAM_TIPO_PARTE, tipo);
    	if (pai == null) {
    		return (Long) getNamedSingleResult(EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_TIPO, params) > 0;
    	} else {
    		params.put(PARAM_PARTICIPANTE_PAI, pai);
        	return (Long) getNamedSingleResult(EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_PAI_TIPO, params) > 0;
    	}
    }
    
    public List<ParticipanteProcesso> getParticipantesProcesso(Processo processo) {
    	Map<String, Object> params = new HashMap<>();
    	params.put(PARAM_PROCESSO, processo);
    	return getNamedResultList(PARTICIPANTES_PROCESSO, params);
    }
    
    public List<ParticipanteProcesso> getParticipantesProcessoRaiz(Processo processo) {
    	Map<String, Object> params = new HashMap<>();
    	params.put(PARAM_PROCESSO, processo);
    	return getNamedResultList(PARTICIPANTES_PROCESSO_RAIZ, params);
    }

    public List<ParticipanteProcesso> getParticipantesByProcessoPessoaParticipanteFilho(Processo processo,
            PessoaFisica pessoaParticipanteFilho) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PROCESSO, processo);
        params.put(PARAM_PESSOA_PARTICIPANTE_FILHO, pessoaParticipanteFilho);
        return getNamedResultList(PARTICIPANTES_BY_PROCESSO_PARTICIPANTE_FILHO, params);
    }
    
    
    public List<ParticipanteProcesso> getParticipantesProcessosByPartialName(String typedName, int maxResult) {
    	Map<String, Object> params = new HashMap<>();
    	params.put(PARAM_TYPED_NAME, typedName);
    	return getResultList(PARTICIPANTES_PROCESSOS_BY_PARTIAL_NAME, params,0,maxResult);
    }

    public boolean existeParticipanteFilhoByParticipanteProcesso(Processo processo,
            ParticipanteProcesso participantePai, PessoaFisica pessoaParticipanteFilho) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PROCESSO, processo);
        params.put(PARAM_PARTICIPANTE_PAI, participantePai);
        params.put(PARAM_PESSOA_PARTICIPANTE_FILHO, pessoaParticipanteFilho);
        return (Long) getNamedSingleResult(EXISTE_PARTICIPANTE_FILHO_BY_PROCESSO, params) > 0;
    }

	public List<ParticipanteProcesso> getParticipantesByTipo(Processo processo, TipoParte tipoParte) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParticipanteProcesso> query = cb.createQuery(ParticipanteProcesso.class);
		Root<ParticipanteProcesso> participante = query.from(ParticipanteProcesso.class);
		Predicate predicate = cb.equal(participante.get(ParticipanteProcesso_.processo), processo);
		predicate = cb.and(predicate, cb.equal(participante.get(ParticipanteProcesso_.tipoParte), tipoParte));
		predicate = cb.and(predicate, cb.isTrue(participante.get(ParticipanteProcesso_.ativo)));
		query.where(predicate);
		query.orderBy(cb.asc(participante.get(ParticipanteProcesso_.nome)));
		query.select(participante);
		
		return getEntityManager().createQuery(query).getResultList();
	}

	public Pessoa getPessoaByParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
	    Map<String, Object> params = new HashMap<>();
	    params.put(PARAM_ID_PARTICIPANTE, participanteProcesso.getId());
	    return getNamedSingleResult(PESSOA_BY_PARTICIPANTE_PROCESSO, params);
	}

	public List<ParticipanteProcesso> getParticipanteByPessoaFetch(Integer idProcesso, Integer idPessoa) {
		Map<String, Object> params = new HashMap<>();
		params.put(PARAM_PESSOA, idPessoa);
		params.put(PARAM_PROCESSO, idProcesso);
		return getNamedResultList(PARTICIPANTE_BY_PESSOA_FETCH, params);
	}
}
