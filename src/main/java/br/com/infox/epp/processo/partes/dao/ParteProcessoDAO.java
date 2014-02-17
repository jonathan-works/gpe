package br.com.infox.epp.processo.partes.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;

@Name(ParteProcessoDAO.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class ParteProcessoDAO extends DAO<ParteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "parteProcessoDAO";
}
