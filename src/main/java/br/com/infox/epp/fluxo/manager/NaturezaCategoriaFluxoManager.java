package br.com.infox.epp.fluxo.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.NaturezaCategoriaFluxoDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Stateless
@Name(NaturezaCategoriaFluxoManager.NAME)
@AutoCreate
@Stateless
public class NaturezaCategoriaFluxoManager extends Manager<NaturezaCategoriaFluxoDAO, NaturezaCategoriaFluxo> {

    private static final long serialVersionUID = -1441750117108371132L;

    public static final String NAME = "naturezaCategoriaFluxoManager";

    @In
    private NaturezaCategoriaFluxoDAO naturezaCategoriaFluxoDAO;

    public List<NaturezaCategoriaFluxo> listByNatureza(Natureza natureza) {
        return naturezaCategoriaFluxoDAO.listByNatureza(natureza);
    }

    public NaturezaCategoriaFluxo getByRelationship(Natureza natureza,
            Categoria categoria, Fluxo fluxo) {
        return naturezaCategoriaFluxoDAO.getByRelationship(natureza, categoria, fluxo);
    }

    public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(
            Fluxo fluxo) {
        return naturezaCategoriaFluxoDAO.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
    }
    
    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxoByDsNatAndDsCat(String dsNatureza, String dsCategoria){
        return getDao().getNaturezaCategoriaFluxoByDsNatAndDsCat(dsNatureza, dsCategoria);
    }
    
    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxoDisponiveis(String dsNatureza, String dsCategoria) {
		return getDao().getNaturezaCategoriaFluxoDisponiveis(dsNatureza, dsCategoria);
	}
}
