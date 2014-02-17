package br.com.infox.epp.processo.partes.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.dao.ParteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;

@Name(ParteProcessoManager.NAME)
@AutoCreate
public class ParteProcessoManager extends Manager<ParteProcessoDAO, ParteProcesso> {

    public static final String NAME = "parteProcessoManager";
    private static final long serialVersionUID = 1L;

    public void incluir(ProcessoEpa processoEpa, Pessoa pessoa) throws DAOException {
        persist(new ParteProcesso(processoEpa, pessoa));
    }

}
