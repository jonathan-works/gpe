package br.com.infox.epp.processo.partes.dao;

import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(ParticipanteProcessoDAO.NAME)
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
}
