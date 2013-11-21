package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.itx.util.ComponentUtil;

@Name(NatCatFluxoLocalizacaoCrudAction.NAME)
public class NatCatFluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao> {
    
    public static final String NAME = "natCatFluxoLocalizacaoCrudAction";
    
    private NaturezaCategoriaFluxo naturezaCategoriaFluxoCorrente;
    @In private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;
    
    private static final Log LOG = Logging.getLog(NatCatFluxoLocalizacaoCrudAction.class);
    
    public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo){
        getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
        naturezaCategoriaFluxoCorrente = naturezaCategoriaFluxo;
    }
    
    @Override
    public void newInstance() {
        super.newInstance();
        getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxoCorrente);
    }
    
    @Override
    public String save() {
        if (getInstance().getHeranca()) {
        	try {
				natCatFluxoLocalizacaoManager.persistWithChildren(getInstance());
			} catch (DAOException e) {
				LOG.error(null, e);
			}
        }
        return super.save();
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
