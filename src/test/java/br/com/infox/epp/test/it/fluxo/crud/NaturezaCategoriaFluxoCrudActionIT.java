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

import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCategoriaFluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCrudAction;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class NaturezaCategoriaFluxoCrudActionIT extends AbstractCrudTest<NaturezaCategoriaFluxo> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(NaturezaCategoriaFluxoCrudAction.class, FluxoCrudAction.class, 
                        FluxoManager.class, FluxoDAO.class,NaturezaCrudAction.class,
                        CategoriaCrudAction.class,FluxoCrudActionIT.class)
        .createDeployment();
    }

    @Override
    protected String getComponentName() {
        return NaturezaCategoriaFluxoCrudAction.NAME;
    }

    @Override
    protected void initEntity(final NaturezaCategoriaFluxo entity,
            final CrudActions<NaturezaCategoriaFluxo> crudActions) {
        crudActions.setEntityValue("natureza", entity.getNatureza());// *
        crudActions.setEntityValue("categoria", entity.getCategoria());// *
        crudActions.setEntityValue("fluxo", entity.getFluxo());// *
    }

    private static final Boolean[] booleans = { Boolean.TRUE, Boolean.FALSE };
    private static final Boolean[] allBooleans = { null, Boolean.TRUE,
        Boolean.FALSE };

    private final InternalRunnableTest<Categoria> persistCategoriaSuccess = new InternalRunnableTest<Categoria>(CategoriaCrudAction.NAME) {

        private void initEntity(final Categoria entity) {
            setEntityValue("categoria", entity.getCategoria());
            setEntityValue("ativo", entity.getAtivo());
        }

        @Override
        protected void testComponent() throws Exception {
            final Categoria entity = getEntity();
            newInstance();
            this.initEntity(entity);
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());
        }
    };

    private final InternalRunnableTest<Natureza> persistNaturezaSuccess = new InternalRunnableTest<Natureza>(NaturezaCrudAction.NAME) {
        private void initEntity(final Natureza entity) {
            setEntityValue("natureza", entity.getNatureza()); // *
            setEntityValue("hasPartes", entity.getHasPartes());
            setEntityValue("ativo", entity.getAtivo());
        }

        @Override
        protected void testComponent() throws Exception {
            final Natureza entity = getEntity();
            newInstance();
            this.initEntity(entity);
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());
        }
    };

    private final InternalRunnableTest<Fluxo> persistFluxoSuccess = new InternalRunnableTest<Fluxo>(FluxoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Fluxo entity = getEntity();
            newInstance();
            this.initEntity(entity);
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());
        }

        private void initEntity(final Fluxo entity) {
            setEntityValue("codFluxo", entity.getCodFluxo());//* validator
            setEntityValue("fluxo", entity.getFluxo()); //*
            setEntityValue("qtPrazo", entity.getQtPrazo()); //*
            setEntityValue("dataInicioPublicacao", entity.getDataInicioPublicacao());//*
            setEntityValue("dataFimPublicacao", entity.getDataFimPublicacao());
            setEntityValue("publicado", entity.getPublicado());//*
            setEntityValue("ativo", entity.getAtivo());//*
        }
    };

    private String combine(final String base, final String suffix, final int id) {
        return format("{0} {1} {2}", base, suffix, id);
    }

    private List<Natureza> persistNatureza(final String suffix) throws Exception {
        int id = 0;
        final List<Natureza> naturezas = new ArrayList<>();
        for (final Boolean hasParte : booleans) {
            for (final Boolean ativo : booleans) {
                final Natureza prePersisted = new Natureza(combine("Natureza", suffix, ++id), hasParte, ativo);
                final Natureza afterPersisted = persistNaturezaSuccess.runTest(prePersisted);
                naturezas.add(afterPersisted);
            }
        }
        return naturezas;
    }

    private List<Categoria> persistCategoria(final String suffix) throws Exception {
        int id = 0;
        final List<Categoria> categorias = new ArrayList<>();
        for (final Boolean ativo : booleans) {
            final Categoria prePersisted = new Categoria(combine("Categoria", suffix, ++id), ativo);
            final Categoria afterPersisted = persistCategoriaSuccess.runTest(prePersisted);
            categorias.add(afterPersisted);
        }
        return categorias;
    }

    private void executeRunnableTestSuccessList(final String suffix, final InternalRunnableTest<NaturezaCategoriaFluxo> runnableTest) throws Exception {
        final List<Natureza> naturezaList = persistNatureza(suffix);
        final List<Categoria> persistCategoria = persistCategoria(suffix);
        final List<Fluxo> persistFluxo =  FluxoCrudActionIT.persistFluxo(suffix, servletContext, session);
        for (final Natureza natureza : naturezaList) {
            for (final Categoria categoria : persistCategoria) {
                for (final Fluxo fluxo : persistFluxo) {
                    runnableTest.runTest(new NaturezaCategoriaFluxo(natureza, categoria, fluxo));
                }
            }
        }
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        executeRunnableTestSuccessList("pers-suc", persistSuccess);
    }
    
    @Test
    public void persistFailTest() throws Exception {
        final String suffix = "pers-fail";
        final List<Natureza> naturezaList = persistNatureza(suffix);
        final List<Categoria> persistCategoria = persistCategoria(suffix);
        final List<Fluxo> persistFluxo =  FluxoCrudActionIT.persistFluxo(suffix, servletContext, session);
        for (final Categoria categoria : persistCategoria) {
            for (final Fluxo fluxo : persistFluxo) {
                persistFail.runTest(new NaturezaCategoriaFluxo(null, categoria, fluxo));
            }
        }
        for (final Natureza natureza : naturezaList) {
            for (final Fluxo fluxo : persistFluxo) {
                persistFail.runTest(new NaturezaCategoriaFluxo(natureza, null, fluxo));
            }
        }
        for (final Natureza natureza : naturezaList) {
            for (final Categoria categoria : persistCategoria) {
                persistFail.runTest(new NaturezaCategoriaFluxo(natureza, categoria, null));
            }
        }
        persistFail.runTest(new NaturezaCategoriaFluxo(null, null, null));
    }

    @Test
    public void removeSuccessTest() throws Exception {
        executeRunnableTestSuccessList("rem-suc", removeSuccess);
    }
}
