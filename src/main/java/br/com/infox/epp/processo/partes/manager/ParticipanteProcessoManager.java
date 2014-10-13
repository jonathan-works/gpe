package br.com.infox.epp.processo.partes.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.dao.ParticipanteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@AutoCreate
@Name(ParticipanteProcessoManager.NAME)
public class ParticipanteProcessoManager extends Manager<ParticipanteProcessoDAO, ParticipanteProcesso> {

    public static final String NAME = "participanteProcessoManager";
    private static final long serialVersionUID = 1L;

    public void incluir(ProcessoEpa processoEpa, Pessoa pessoa) throws DAOException {
    	ParticipanteProcesso participanteProcesso = new ParticipanteProcesso(processoEpa, pessoa);
    	participanteProcesso.setNome(pessoa.getNome());
        persist(participanteProcesso);
    }
    
    public ParticipanteProcesso getParticipanteProcessoByPessoaProcesso(Pessoa pessoa, Processo processo){
    	return getDao().getParticipanteProcessoByPessoaProcesso(pessoa, processo);
    }

}
