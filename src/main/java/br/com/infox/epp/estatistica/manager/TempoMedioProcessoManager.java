package br.com.infox.epp.estatistica.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.estatistica.entity.TempoMedioProcesso;

@Name(TempoMedioProcessoManager.NAME)
@AutoCreate
public class TempoMedioProcessoManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    private static final Class<TempoMedioProcesso> CLASS = TempoMedioProcesso.class;
    public static final String NAME = "tempoMedioProcessoManager";
    
    public List<TempoMedioProcesso> findAll(){
        return findAll(CLASS);
    }

}
