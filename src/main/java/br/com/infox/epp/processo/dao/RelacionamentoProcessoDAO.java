package br.com.infox.epp.processo.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;

@AutoCreate
@Name(RelacionamentoProcessoDAO.NAME)
public class RelacionamentoProcessoDAO extends DAO<RelacionamentoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "relacionamentoProcessoDAO";

}
