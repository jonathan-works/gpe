package br.com.infox.epp.test.it.fluxo.crud;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCategoriaFluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCrudAction;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.dao.NaturezaCategoriaFluxoDAO;
import br.com.infox.epp.fluxo.dao.NaturezaDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.NotUpdatableEntityRemoveSuccessTest;
import br.com.infox.epp.test.crud.NotUpdatablePersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class NaturezaCategoriaFluxoCrudActionIT extends AbstractCrudTest<NaturezaCategoriaFluxo> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(NaturezaCategoriaFluxoCrudAction.class, FluxoCrudAction.class, 
                        FluxoManager.class, FluxoDAO.class,NaturezaCrudAction.class,
                        CategoriaCrudAction.class,FluxoCrudActionIT.class,
                        NaturezaCrudActionIT.class, NaturezaManager.class,
                        NaturezaDAO.class,CategoriaManager.class,CategoriaDAO.class,
                        NaturezaCategoriaFluxoManager.class, NaturezaCategoriaFluxoDAO.class,
                        CategoriaCrudActionIT.class, ParteProcessoEnum.class,
                        NotUpdatableEntityRemoveSuccessTest.class, NotUpdatablePersistSuccessTest.class)
        .createDeployment();
    }

    @Override
    protected String getComponentName() {
        return NaturezaCategoriaFluxoCrudAction.NAME;
    }

    private static final ActionContainer<NaturezaCategoriaFluxo> initEntity = new ActionContainer<NaturezaCategoriaFluxo>() {
        @Override
        public void execute(CrudActions<NaturezaCategoriaFluxo> crudActions) {
            final NaturezaCategoriaFluxo entity = getEntity();
            crudActions.setEntityValue("natureza", entity.getNatureza());// *
            crudActions.setEntityValue("categoria", entity.getCategoria());// *
            crudActions.setEntityValue("fluxo", entity.getFluxo());// *
        }
    };

    public static final List<NaturezaCategoriaFluxo> getSuccessfullyPersisted(ActionContainer<NaturezaCategoriaFluxo> action, String suffix, ServletContext servletContext, HttpSession session) throws Exception {
        final ArrayList<NaturezaCategoriaFluxo> list = new ArrayList<>();
        final String formattedSuffix = format("ncf{0}", suffix);
        final List<Natureza> naturezaList = NaturezaCrudActionIT.getSuccessfullyPersisted(null, formattedSuffix, servletContext, session);
        final List<Categoria> persistCategoria = CategoriaCrudActionIT.getSuccessfullyPersisted(null, formattedSuffix, servletContext, session);
        final List<Fluxo> persistFluxo =  FluxoCrudActionIT.getSuccessfullyPersisted(null, formattedSuffix, servletContext, session);
        for (final Natureza natureza : naturezaList) {
            for (final Categoria categoria : persistCategoria) {
                for (final Fluxo fluxo : persistFluxo) {
                    final NaturezaCategoriaFluxo entity = new NaturezaCategoriaFluxo(natureza, categoria, fluxo);
                    final NotUpdatablePersistSuccessTest<NaturezaCategoriaFluxo> test = new NotUpdatablePersistSuccessTest<>(NaturezaCategoriaFluxoCrudAction.NAME, initEntity);
                    final NaturezaCategoriaFluxo naturezaCategoriaFluxo = test.runTest(action, entity, servletContext, session);
                    list.add(naturezaCategoriaFluxo);
                }
            }
        }
        return list;
    }
    
    @Override
    protected ActionContainer<NaturezaCategoriaFluxo> getInitEntityAction() {
        return initEntity;
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        getSuccessfullyPersisted(null, "pers-suc", servletContext, session);
    }
    
    @Test
    public void persistFailTest() throws Exception {
        final String suffix = "pers-fail";
        final List<Natureza> naturezaList = NaturezaCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        final List<Categoria> persistCategoria = CategoriaCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        final List<Fluxo> persistFluxo =  FluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        for (final Categoria categoria : persistCategoria) {
            for (final Fluxo fluxo : persistFluxo) {
                persistFail.runTest(new NaturezaCategoriaFluxo(null, categoria, fluxo), servletContext, session);
            }
        }
        for (final Natureza natureza : naturezaList) {
            for (final Fluxo fluxo : persistFluxo) {
                persistFail.runTest(new NaturezaCategoriaFluxo(natureza, null, fluxo), servletContext, session);
            }
        }
        for (final Natureza natureza : naturezaList) {
            for (final Categoria categoria : persistCategoria) {
                persistFail.runTest(new NaturezaCategoriaFluxo(natureza, categoria, null), servletContext, session);
            }
        }
        persistFail.runTest(new NaturezaCategoriaFluxo(null, null, null), servletContext, session);
    }

    @Test
    public void removeSuccessTest() throws Exception {
        final String suffix = "rem-suc";
        final List<Natureza> naturezaList = NaturezaCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        final List<Categoria> persistCategoria = CategoriaCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        final List<Fluxo> persistFluxo =  FluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        for (final Natureza natureza : naturezaList) {
            for (final Categoria categoria : persistCategoria) {
                for (final Fluxo fluxo : persistFluxo) {
                    NotUpdatableEntityRemoveSuccessTest<NaturezaCategoriaFluxo> test = new NotUpdatableEntityRemoveSuccessTest<NaturezaCategoriaFluxo>(NaturezaCategoriaFluxoCrudAction.NAME, initEntity) {
                        @Override
                        protected boolean checkManagedEntity() {
                            String query = "select o.idNaturezaCategoriaFluxo from NaturezaCategoriaFluxo o order by o.idNaturezaCategoriaFluxo desc";
                            Integer id = invokeMethod(NaturezaCategoriaFluxoManager.NAME, "getSingleResult", Integer.class, new Class[] {String.class, Map.class}, query, null);
                            if (id == null) {
                                return false;
                            }
                            invokeMethod("setId", Void.class, new Class[] {Object.class}, id);
                            return true;
                        }
                    };
                    test.runTest(new NaturezaCategoriaFluxo(natureza, categoria, fluxo), servletContext, session);
                }
            }
        }
    }
}
