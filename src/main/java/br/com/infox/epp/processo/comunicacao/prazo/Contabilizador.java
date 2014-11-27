package br.com.infox.epp.processo.comunicacao.prazo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.ibpm.process.definition.annotations.DefinitionAvaliable;

@Name(Contabilizador.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@DefinitionAvaliable
public class Contabilizador {
    public static final String NAME = "contabilizador";
    
    @In
    private ComunicacaoService comunicacaoService;
    
    /**
     * Calcula o prazo para tomar ciência de uma Comunicação
     * 
     * @param idProcesso : id do processo em questão
     * @throws DAOException 
     */
    public void contabilizarPrazo(Integer idProcesso) throws DAOException {
//        contabilizarPrazoService.contabilizarPrazoCiencia(comunicacao);
    }
}
