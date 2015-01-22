package br.com.infox.epp.julgamento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.julgamento.dao.SalaDAO;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@AutoCreate
@Name(SalaManager.NAME)
public class SalaManager extends Manager<SalaDAO, Sala> {

    public static final String NAME = "salaManager";
    private static final long serialVersionUID = 1L;
    
    public List<Sala> listSalaByColegiada(UnidadeDecisoraColegiada colegiada) {
    	return getDao().listSalaByColegiada(colegiada);
    }
    
    public List<Sala> findAllAtivo() {
        return getDao().findAllAtivo();
    }

}
