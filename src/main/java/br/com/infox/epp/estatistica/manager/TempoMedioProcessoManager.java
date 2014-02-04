package br.com.infox.epp.estatistica.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.estatistica.dao.TempoMedioProcessoDAO;
import br.com.infox.epp.estatistica.entity.TempoMedioProcesso;

@Name(TempoMedioProcessoManager.NAME)
@AutoCreate
public class TempoMedioProcessoManager extends Manager<TempoMedioProcessoDAO, TempoMedioProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tempoMedioProcessoManager";
    
}
