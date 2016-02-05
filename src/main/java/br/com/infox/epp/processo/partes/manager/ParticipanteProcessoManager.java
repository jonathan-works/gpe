package br.com.infox.epp.processo.partes.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.dao.ParticipanteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso_;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@Stateless
@AutoCreate
@Name(ParticipanteProcessoManager.NAME)
public class ParticipanteProcessoManager extends Manager<ParticipanteProcessoDAO, ParticipanteProcesso> {

    public static final String NAME = "participanteProcessoManager";
    private static final long serialVersionUID = 1L;

    public ParticipanteProcesso getParticipanteProcessoByPessoaProcesso(Pessoa pessoa, Processo processo){
    	return getDao().getParticipanteProcessoByPessoaProcesso(pessoa, processo);
    }
    
    public List<ParticipanteProcesso> getParticipantesProcessoByPartialName(String typedName,int limit){
    	return getDao().getParticipantesProcessosByPartialName(typedName,limit);
    }
    public boolean existeParticipanteByPessoaProcessoPaiTipo(Pessoa pessoa, 
    		Processo processo, ParticipanteProcesso pai, TipoParte tipo) {
    	return getDao().existeParticipanteByPessoaProcessoPaiTipo(pessoa, processo, pai, tipo);
    }
    
    public boolean existeParticipanteByPessoaProcessoPaiTipoLock(Pessoa pessoa, 
    		Processo processo, ParticipanteProcesso pai, TipoParte tipo) {
    	getDao().lockPessimistic(processo);
    	return existeParticipanteByPessoaProcessoPaiTipo(pessoa, processo, pai, tipo);
    }
    
    public List<ParticipanteProcesso> getParticipantesProcesso(Processo processo) {
    	return getDao().getParticipantesProcesso(processo);
    }
    
    public List<ParticipanteProcesso> getParticipantesProcessoRaiz(Processo processo) {
    	return getDao().getParticipantesProcessoRaiz(processo);
    }
    
    public List<ParticipanteProcesso> getParticipantesByProcessoPessoaParticipanteFilho(Processo processo, PessoaFisica pessoaParticipanteFilho) {
        return getDao().getParticipantesByProcessoPessoaParticipanteFilho(processo, pessoaParticipanteFilho);
    }
    
    public boolean existeParticipanteFilhoByParticipanteProcesso(Processo processo, ParticipanteProcesso participantePai, PessoaFisica pessoaParticipanteFilho) {
        return getDao().existeParticipanteFilhoByParticipanteProcesso(processo, participantePai, pessoaParticipanteFilho);
    }
    
    public List<ParticipanteProcesso> getParticipantesByTipo(Processo processo, TipoParte tipoParte) {
    	return getDao().getParticipantesByTipo(processo, tipoParte);
    }
    
    public List<Pessoa> getPessoasParticipantesProcesso(Processo processo){
    	return getDao().getPessoasFisicasParticipantesProcesso(processo);
    }
    
    //ver se vai usar
    public List<ParticipanteProcesso> getAllParticipantesByParticipantePai(ParticipanteProcesso participantePai) {
		CriteriaBuilder cb = getDao().getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParticipanteProcesso> query = cb.createQuery(ParticipanteProcesso.class);
		Root<ParticipanteProcesso> participante = query.from(ParticipanteProcesso.class);
		query.where(cb.like(participante.get(ParticipanteProcesso_.caminhoAbsoluto), 
				cb.literal("%:caminhoAbsolutoPai%")));
		TypedQuery<ParticipanteProcesso> typedQuery = getDao().getEntityManager().createQuery(query);
		typedQuery.setParameter("caminhoAbsolutoPai", participantePai.getCaminhoAbsoluto());
		return typedQuery.getResultList();
	}
    
    public void gravarListaParticipantes(List<ParticipanteProcesso> participantes) {
		for (ParticipanteProcesso participanteProcesso : participantes) {
			getDao().getEntityManager().persist(participanteProcesso);
		}
		getDao().getEntityManager().flush();
	}
    
}
