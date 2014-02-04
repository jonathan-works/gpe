package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PostgreSQLErrorCode;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.itx.util.ComponentUtil;

@Name(NatCatFluxoLocalizacaoCrudAction.NAME)
public class NatCatFluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao, NatCatFluxoLocalizacaoManager> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "natCatFluxoLocalizacaoCrudAction";
    
    private NaturezaCategoriaFluxo naturezaCategoriaFluxoCorrente;
    
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
				getManager().persistWithChildren(getInstance());
			} catch (DAOException e) {
				LOG.error(null, e);
				FacesMessages.instance().clear();
				FacesMessages.instance().add(e.getLocalizedMessage());
				return e.getPostgreSQLErrorCode().toString();
			}
        }
        if (getManager().existsNatCatFluxoLocalizacao(getInstance().getNaturezaCategoriaFluxo(), getInstance().getLocalizacao())) {
        	FacesMessages.instance().clear();
        	FacesMessages.instance().add("#{messages['constraintViolation.uniqueViolation']}");
        	return PostgreSQLErrorCode.UNIQUE_VIOLATION.toString();
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
