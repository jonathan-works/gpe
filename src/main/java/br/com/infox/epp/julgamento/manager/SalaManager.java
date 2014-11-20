package br.com.infox.epp.julgamento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.julgamento.dao.SalaDAO;
import br.com.infox.epp.julgamento.entity.Sala;

@AutoCreate
@Name(SalaManager.NAME)
public class SalaManager extends Manager<SalaDAO, Sala> {

    public static final String NAME = "salaManager";
    private static final long serialVersionUID = 1L;

}
