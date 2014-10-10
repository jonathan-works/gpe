package br.com.infox.epp.processo.partes.dao;

import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_PESSOA;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARAM_PROCESSO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(ParticipanteProcessoDAO.NAME)
public class ParticipanteProcessoDAO extends DAO<ParticipanteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "participanteProcessoDAO";
    
    public ParticipanteProcesso getParticipanteProcessoByPessoaProcesso(Pessoa pessoa, Processo processo){
    	Map<String, Object> params = new HashMap<>();
    	params.put(PARAM_PESSOA, pessoa);
    	params.put(PARAM_PROCESSO, processo);
    	return getNamedSingleResult(PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO, params);
    }
}
