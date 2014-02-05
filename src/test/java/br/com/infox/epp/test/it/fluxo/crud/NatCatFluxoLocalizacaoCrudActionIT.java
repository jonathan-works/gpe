package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NatCatFluxoLocalizacaoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCategoriaFluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCrudAction;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class NatCatFluxoLocalizacaoCrudActionIT extends AbstractCrudTest<NatCatFluxoLocalizacao>{
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses()
        .createDeployment();
    }
    
    @Override
    protected String getComponentName() {
        return NatCatFluxoLocalizacaoCrudAction.NAME;
    }

    @Override
    protected void initEntity(final NatCatFluxoLocalizacao entity,
            final CrudActions<NatCatFluxoLocalizacao> crudActions) {
        crudActions.setComponentValue("naturezaCategoriaFluxo", entity.getNaturezaCategoriaFluxo());
    }

    @Test
    public void persistSuccess() throws Exception {
        
    }
}