package br.com.infox.epp.fluxo.crud;

import java.util.List;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(FluxoLocalizacaoCrudAction.NAME)
public class FluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao> {

    public static final String NAME = "fluxoLocalizacaoCrudAction";
    
    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo(){
        return getInstance().getNaturezaCategoriaFluxo();
    }
    
    public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo){
        getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
    }
    
    public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(Fluxo fluxo) {
        String hql = "select ncf from NaturezaCategoriaFluxo ncf " +
                "inner join ncf.natureza n " +
                "inner join ncf.categoria c " +
                "where n.ativo=true " +
                "and c.ativo=true " +
                "and ncf.fluxo=:fluxo";
        return EntityUtil.getEntityManager().createQuery(hql, NaturezaCategoriaFluxo.class)
                .setParameter("fluxo", fluxo)
                .getResultList();
    }
    
    @Override
    protected void afterSave() {
        super.afterSave();
        newInstance();
        clearTree();
    }

    private void clearTree() {
        LocalizacaoTreeHandler treeHandler = ComponentUtil.getComponent("localizacaoTree");
        treeHandler.clearTree();
    }
    
}
