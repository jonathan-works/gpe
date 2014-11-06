package br.com.infox.epp.processo.partes.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.dao.ParticipanteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@AutoCreate
@Name(ParticipanteProcessoManager.NAME)
public class ParticipanteProcessoManager extends Manager<ParticipanteProcessoDAO, ParticipanteProcesso> {

    public static final String NAME = "participanteProcessoManager";
    private static final long serialVersionUID = 1L;

    public ParticipanteProcesso getParticipanteProcessoByPessoaProcesso(Pessoa pessoa, Processo processo){
    	return getDao().getParticipanteProcessoByPessoaProcesso(pessoa, processo);
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
}
