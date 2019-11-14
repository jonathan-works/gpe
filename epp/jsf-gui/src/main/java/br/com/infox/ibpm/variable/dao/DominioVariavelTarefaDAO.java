package br.com.infox.ibpm.variable.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@Stateless
@AutoCreate
@Name(DominioVariavelTarefaDAO.NAME)
public class DominioVariavelTarefaDAO extends DAO<DominioVariavelTarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "dominioVariavelTarefaDAO";

}
