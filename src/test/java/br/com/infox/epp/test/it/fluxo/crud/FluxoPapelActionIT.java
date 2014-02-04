package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.crud.PapelCrudAction;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.crud.FluxoPapelAction;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.dao.FluxoPapelDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.FluxoPapelManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class FluxoPapelActionIT extends AbstractCrudTest<FluxoPapel> {
    private static final String FIELD_ATIVO = "ativo";
    private static final String FIELD_PUBLICADO = "publicado";
    private static final String FIELD_DT_FIM = "dataFimPublicacao";
    private static final String FIELD_DT_INICIO = "dataInicioPublicacao";
    private static final String FIELD_PRAZO = "qtPrazo";
    private static final String FIELD_DESC = "fluxo";
    private static final String FIELD_CODIGO = "codFluxo";
    private static final Boolean[] booleans = {TRUE, FALSE};
    private static final Boolean[] allBooleans = {TRUE, FALSE, null};
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(FluxoPapelAction.class, PapelTreeHandler.class, FluxoPapelManager.class, FluxoPapelDAO.class,
                PapelCrudAction.class,RolesMap.class,PapelManager.class,RecursoManager.class,PapelDAO.class,
                RecursoDAO.class,FluxoCrudAction.class, FluxoManager.class, FluxoDAO.class)
        .createDeployment();
    }
    
    @Override
    protected String getComponentName() {
        return FluxoPapelAction.NAME;
    }

    @Override
    protected void initEntity(final FluxoPapel entity,final CrudActions<FluxoPapel> crudActions) {
        crudActions.invokeMethod("init",Void.class, entity.getFluxo());
        crudActions.setEntityValue("papel", entity.getPapel());
    }
    
    private final InternalRunnableTest<FluxoPapel> persistSuccess = new InternalRunnableTest<FluxoPapel>() {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity= getEntity();
            newInstance();
            initEntity(entity, this);
            assertEquals("persist failed", PERSISTED, save());

            final Integer id = getId();
            assertNull("id not null", id);
        }
    };
    
    @Test
    public void persistSuccessTest() throws Exception {
        final String suffix = "perSuccess";
        final ArrayList<Papel> papeis = initPapeis(suffix);
        final ArrayList<Fluxo> fluxos = initFluxos(suffix);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel));        
            }
        }
    }
    
    @Test
    public void persistFailTest() throws Exception {
        final String suffix = "perFail";
        final ArrayList<Papel> papeis = initPapeis(suffix);
        final ArrayList<Fluxo> fluxos = initFluxos(suffix);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel));
                persistFail.runTest(new FluxoPapel(fluxo, papel));
            }
        }
    }
    
    @Test
    public void removeSuccessTest() throws Exception {
        final String suffix = "removeSucc";
        final ArrayList<Papel> papeis = initPapeis(suffix);
        final ArrayList<Fluxo> fluxos = initFluxos(suffix);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel));
            }
            removeSuccess.runTest(new FluxoPapel(fluxo, null));
        }
    }
    
    @Test
    public void removeFailTest() throws Exception {
        final String suffix = "removeFail";
        final ArrayList<Papel> papeis = initPapeis(suffix);
        final ArrayList<Fluxo> fluxos = initFluxos(suffix);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel));
            }
            final FluxoPapel entity = new FluxoPapel(fluxo, null);
            removeSuccess.runTest(entity);
            removeFail.runTest(entity);
        }
    }
    
    private final InternalRunnableTest<FluxoPapel> removeSuccess = new InternalRunnableTest<FluxoPapel>() {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity = getEntity();
            invokeMethod("init",Void.class, entity.getFluxo());
            List<FluxoPapel> list = getComponentValue("fluxoPapelList");
            final List<FluxoPapel> staticList = new ArrayList<>(list);
            for (final FluxoPapel fluxoPapel : staticList) {
                final String remove = remove(fluxoPapel);
                assertEquals("failed at remove(obj)", REMOVED, remove);
            }
            list = getComponentValue("fluxoPapelList");
            for (FluxoPapel fluxoPapel : staticList) {
                assertFalse("object still exists in list",list.contains(fluxoPapel));
            }
        }
    };
    
    private final InternalRunnableTest<FluxoPapel> removeFail = new InternalRunnableTest<FluxoPapel>() {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity = getEntity();
            invokeMethod("init",Void.class, entity.getFluxo());
            List<FluxoPapel> list = getComponentValue("fluxoPapelList");
            final List<FluxoPapel> staticList = new ArrayList<>(list);
            for (final FluxoPapel fluxoPapel : staticList) {
                final String remove = remove(fluxoPapel);
                assertFalse("succeeded at remove(obj)", REMOVED.equals(remove));
            }
            list = getComponentValue("fluxoPapelList");
            for (FluxoPapel fluxoPapel : staticList) {
                assertTrue("object doesn't exist in list",list.contains(fluxoPapel));
            }
        }
    };
    
    private final InternalRunnableTest<Fluxo> persistFluxo = new InternalRunnableTest<Fluxo>(FluxoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Fluxo entity = getEntity(); 
            newInstance();
            this.initEntity(entity);
            assertEquals("persist failed", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("nullid", id);
            newInstance();
            assertNull("Id not null", getId());
            setEntity(resetInstance(id));
        }
        
        private void initEntity(final Fluxo entity) {
            setEntityValue(FIELD_CODIGO, entity.getCodFluxo());//* validator
            setEntityValue(FIELD_DESC, entity.getFluxo()); //*
            setEntityValue(FIELD_PRAZO, entity.getQtPrazo()); //*
            setEntityValue(FIELD_DT_INICIO, entity.getDataInicioPublicacao());//*
            setEntityValue(FIELD_DT_FIM, entity.getDataFimPublicacao());
            setEntityValue(FIELD_PUBLICADO, entity.getPublicado());//*
            setEntityValue(FIELD_ATIVO, entity.getAtivo());//*
        }
    };
    
    private final InternalRunnableTest<Papel> persistPapel = new InternalRunnableTest<Papel>(PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            newInstance();
            initEntity(entity);
            assertEquals("persist failed", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id null", id);
            newInstance();
            assertNull("Id not null", getId());
            setId(id);
            
            boolean roleExists = IdentityManager.instance().roleExists(entity.getIdentificador());
            assertEquals("role doesn't Exist", true, roleExists);
            
            setEntity(getInstance());
        }
        
        private void initEntity(final Papel entity) {
            this.crudActions.setEntityValue("identificador", entity.getIdentificador()); //req
            this.crudActions.setEntityValue("nome", entity.getNome()); // req
        }
    };
    
    private ArrayList<Papel> initPapeis(final String suffix) throws Exception {
        final ArrayList<Papel> result = new ArrayList<>();
        result.add(persistPapel.runTest(new Papel("Gestor"+suffix,"gestor"+suffix)));
        result.add(persistPapel.runTest(new Papel("Administrador Admin"+suffix,"admin"+suffix)));
        result.add(persistPapel.runTest(new Papel("Comprador"+suffix,"comprador"+suffix)));
        result.add(persistPapel.runTest(new Papel("Colaborador"+suffix,"colaborador"+suffix)));
        return result;
    }
    
    private ArrayList<Fluxo> initFluxos(final String suffix) throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        final ArrayList<Fluxo> fluxos = new ArrayList<>();
        int id=0;
        for(final Date dataFim : datasFim) {
            for(final Boolean publicado: allBooleans) {
                for (final Boolean ativo:booleans) {
                    final String codigo = format("Fluxo.{0}.{1}", ++id, suffix);
                    fluxos.add(persistFluxo.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 5, dataInicio, dataFim, publicado, ativo)));
                }
            }
        }
        return fluxos;
    }
    
    private Date getIncrementedDate(final GregorianCalendar currentDate, final int field, final int ammount) {
        currentDate.add(field, ammount);
        return currentDate.getTime();
    }
}
