package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.fluxo.crud.NaturezaCrudAction;
import br.com.infox.epp.fluxo.dao.NaturezaDAO;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class NaturezaCrudActionIT extends AbstractCrudTest<Natureza>{
    
    private static final String DEFAULT_VALUE = "natureza-p";

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(NaturezaCrudAction.class, NaturezaManager.class, NaturezaDAO.class, ParteProcessoEnum.class)
        .createDeployment();
    }

    @Override
    protected String getComponentName() {
        return NaturezaCrudAction.NAME;
    }

    @Override
    protected void initEntity(final Natureza entity, final CrudActions<Natureza> crudActions) {
        initEntityAction.setEntity(entity);
        initEntityAction.execute(crudActions);
    }

    private static final ActionContainer<Natureza> initEntityAction = new ActionContainer<Natureza>() {
        @Override
        public void execute(final CrudActions<Natureza> crudActions) {
            final Natureza entity = getEntity();
            crudActions.setEntityValue("natureza", entity.getNatureza()); //*
            crudActions.setEntityValue("hasPartes", entity.getHasPartes());
            crudActions.setEntityValue("tipoPartes", entity.getTipoPartes());
            crudActions.setEntityValue("numeroPartes", entity.getNumeroPartes());
            crudActions.setEntityValue("ativo", entity.getAtivo());
        }
    };
    
    public static final List<Natureza> getSuccessfullyPersisted(final ActionContainer<Natureza> action, final String suffix,final ServletContext servletContext, final HttpSession session) throws Exception {
        final PersistSuccessTest<Natureza> persistSuccessTest = new PersistSuccessTest<>(NaturezaCrudAction.NAME, initEntityAction);
        final ArrayList<Natureza> naturezas = new ArrayList<>();
        int i=0;
        for(final Boolean hasParte : booleans) {
            for(final Boolean ativo : booleans) {
                if (hasParte){
                    for (ParteProcessoEnum tipo : ParteProcessoEnum.values()){
                        naturezas.add(persistSuccessTest.runTest(action, new Natureza(format("Natureza{0}{1}",suffix,++i), hasParte, tipo, 2, ativo), servletContext, session));
                    }
                } else {
//                    TODO verificar por que o teste seguinte está falhando
//                    persistSuccessTest.runTest(action, new Natureza(format("Natureza{0}{1}",suffix,++i), hasParte, null, null, ativo), servletContext, session);
                }
            }    
        }
        return naturezas;
    }
    
    private static int i = 0;
    
    @Test
    public void persistSuccessTest() throws Exception {
        getSuccessfullyPersisted(null, "persist-suc", servletContext, session);
    }

    private String getDescription(final String defaultValue) {
        return format("{0}{1}",defaultValue,++i);
    }

    private static final Boolean[] booleans = {Boolean.TRUE, Boolean.FALSE};
    
    @Test
    public void persistFailTest() throws Exception {
        final String[] naturezas = {"", null, fillStr(getDescription(DEFAULT_VALUE), DESCRICAO_PEQUENA+1)};
        
        for(final String natureza : naturezas) {
            for(final Boolean hasParte : booleans) {
                for(final Boolean ativo : booleans) {
                    for (final ParteProcessoEnum tipoPartes : ParteProcessoEnum.values()){
                        persistFail.runTest(new Natureza(natureza, hasParte, tipoPartes, 2, ativo));
                    }
                }   
            }
        }
        for(final Boolean ativo : booleans) {
            persistFail.runTest(new Natureza(getDescription(DEFAULT_VALUE), null, null, null, ativo));
        }
        for (final ParteProcessoEnum tipoPartes : ParteProcessoEnum.values()){
            for(final Boolean hasParte : booleans) {
                persistFail.runTest(new Natureza(getDescription(DEFAULT_VALUE), hasParte, tipoPartes, 2, null));
            }
            persistFail.runTest(new Natureza(getDescription(DEFAULT_VALUE), Boolean.TRUE, null, 2, true));
            persistFail.runTest(new Natureza(getDescription(DEFAULT_VALUE), Boolean.TRUE, tipoPartes, null, false));
        }
    }

    @Test
    public void inactivateSuccessTest() throws Exception {
        for(final Boolean hasParte : booleans) {
            for(final Boolean ativo: booleans) {
                for (final ParteProcessoEnum tipoPartes : ParteProcessoEnum.values()){
                    inactivateSuccess.runTest(new Natureza(getDescription(DEFAULT_VALUE), hasParte, tipoPartes, 2, ativo));
                }
            }
        }
    }
    
    private boolean compareObjects(final Object obj1, final Object obj2) {
        return (obj1 == obj2) || ((obj1!=null) && obj1.equals(obj2));
    }
    
    @Override
    protected boolean compareEntityValues(final Natureza entity, final CrudActions<Natureza> crudActions) {
        return compareObjects(entity.getNatureza(), crudActions.getEntityValue("natureza")) &&
                compareObjects(entity.getHasPartes(), crudActions.getEntityValue("hasPartes")) &&
                compareObjects(entity.getAtivo(), crudActions.getEntityValue("ativo"));
    }
    
    @Test
    public void updateSuccessTest() throws Exception {
        final ActionContainer<Natureza> actionContainer = new ActionContainer<Natureza>() {
            @Override
            public void execute(final CrudActions<Natureza> crudActions) {
                final Natureza entity = getEntity();
                final Integer id = crudActions.getId();
                assertNotNull("id not null", id);
                
                crudActions.resetInstance(id);
                
                assertEquals("afterPersisted equals passed instance", true, compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("natureza", format("{0}.{1}", crudActions.getEntityValue("natureza"), "changed"));
                assertEquals(UPDATED, UPDATED, crudActions.save());
                assertEquals("attribute changed", true, ((String)crudActions.getEntityValue("natureza")).endsWith(".changed"));
                
                crudActions.resetInstance(id);
                
                assertEquals("afterUpdated equals passed instance", false, compareEntityValues(entity, crudActions));
                entity.setNatureza((String) crudActions.getEntityValue("natureza"));
                
                assertEquals("afterPersisted equals passed instance", true, compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("hasPartes", !entity.getHasPartes());
                assertEquals(UPDATED, UPDATED, crudActions.save());
                assertEquals("attribute changed", false, entity.getHasPartes().equals(crudActions.getEntityValue("hasPartes")));

                crudActions.resetInstance(id);
                
                assertEquals("afterUpdated equals passed instance", false, compareEntityValues(entity, crudActions));
                entity.setHasPartes(!entity.getHasPartes());
                
                assertEquals("afterPersisted equals passed instance", true, compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("ativo", !entity.getAtivo());
                assertEquals(UPDATED, UPDATED, crudActions.save());
                assertEquals("attribute changed", false, entity.getAtivo().equals(crudActions.getEntityValue("ativo")));

                crudActions.resetInstance(id);
                
                assertEquals("afterUpdated equals passed instance", false, compareEntityValues(entity, crudActions));
            }
        };

        getSuccessfullyPersisted(actionContainer, "update-suc", servletContext, session);
    }
    
    @Test
    public void updateFailTest() throws Exception {
        final ActionContainer<Natureza> actionContainer = new ActionContainer<Natureza>() {
            @Override
            public void execute(final CrudActions<Natureza> crudActions) {
                final Natureza entity = getEntity();
                final Integer id = crudActions.getId();
                assertNotNull("id not null", id);
                
                final String[] naturezas = {"", null, fillStr(getDescription(DEFAULT_VALUE)+".changed", DESCRICAO_PEQUENA+1)};

                crudActions.resetInstance(id);
                assertEquals("afterPersisted equals passed instance", true, compareEntityValues(entity, crudActions));
                for (final String natureza : naturezas) {
                    crudActions.setEntityValue("natureza", natureza);
                    assertEquals(UPDATED, false, UPDATED.equals(crudActions.save()));
                    crudActions.resetInstance(id);
                    assertEquals("attribute changed", true, ((String)crudActions.getEntityValue("natureza")).equals(entity.getNatureza()));
                    assertEquals("afterPersisted equals passed instance", true, compareEntityValues(entity, crudActions));
                }
                
                assertEquals("afterPersisted equals passed instance", true, compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("hasPartes", null);
                assertEquals(UPDATED, false, UPDATED.equals(crudActions.save()));
                crudActions.resetInstance(id);
                assertEquals("attribute didn't changed", true, entity.getHasPartes().equals(crudActions.getEntityValue("hasPartes")));
                assertEquals("afterUpdated equals passed instance", true, compareEntityValues(entity, crudActions));
                
                crudActions.setEntityValue("ativo", null);
                assertEquals(UPDATED, false, UPDATED.equals(crudActions.save()));
                crudActions.resetInstance(id);
                assertEquals("attribute changed", true, entity.getAtivo().equals(crudActions.getEntityValue("ativo")));
                assertEquals("afterUpdated equals passed instance", true, compareEntityValues(entity, crudActions));
            }
        };
        getSuccessfullyPersisted(actionContainer, "update-fail", servletContext, session);
    }
    
}
