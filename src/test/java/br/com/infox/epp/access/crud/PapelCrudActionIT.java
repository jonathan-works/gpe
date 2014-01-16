package br.com.infox.epp.access.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class PapelCrudActionIT extends AbstractGenericCrudTest<Papel>{

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(PapelCrudAction.class,RolesMap.class,PapelManager.class,
                    RecursoManager.class,PapelDAO.class,RecursoDAO.class)
        .createDeployment();
    }

    @Override
    protected void initEntity(final Papel entity) {
        getCrudActions().setEntityValue("identificador", entity.getIdentificador()); //req
        getCrudActions().setEntityValue("nome", entity.getNome()); // req
    }

    @Override
    protected String getComponentName() {
        return PapelCrudAction.NAME;
    }
    
    private final RunnableTest<Papel> persistSuccess = new RunnableTest<Papel>() {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            
            CrudActions<Papel> crudActions = getCrudActions();
            
            crudActions.newInstance();
            initEntity(entity);
            assertEquals("persisted", PERSISTED, crudActions.save());

            final Integer id = crudActions.getId();
            assertNotNull("id", id);
            crudActions.newInstance();
            assertNull("nullId", crudActions.getId());
            crudActions.setId(id);
            assertEquals("Compare", true, compareEntityValues(entity));
            
            boolean roleExists = IdentityManager.instance().roleExists(entity.getIdentificador());
            assertEquals("roleExists", true, roleExists);
            
            setEntity(crudActions.getInstance());
        }
    };
    
    @Test
    public void persistSuccessTest() throws Exception {
        persistSuccess.runTest(new Papel("Administrador Admin","admin"));
        persistSuccess.runTest(new Papel("Gestor","gestor"));
        persistSuccess.runTest(new Papel("Comprador","comprador"));
        persistSuccess.runTest(new Papel("Colaborador","colab"));
        persistSuccess.runTest(new Papel("Redator","redator"));
        persistSuccess.runTest(new Papel("Vendedor","vendedor"));
    }
    
    private String jsonPapel(Papel papel) {
        return format("'{'id:{0},nome:{1},role:{2},ativo:{3}'}'", papel.getIdPapel(), papel.getNome(), papel.getIdentificador(), papel.getAtivo());
    }
    
    private final RunnableTest<Papel> persistFail = new RunnableTest<Papel>() {
        @Override
        protected void testComponent() throws Exception {
            final CrudActions<Papel> crudActions = getCrudActions();
            final Papel entity = getEntity();
            crudActions.newInstance();
            initEntity(entity);
            
            assertEquals("persisted", true, !PERSISTED.equals(crudActions.save()));

            final Integer id = crudActions.getId();
            assertNull("id", id);
            assertEquals("roleExists", true, !IdentityManager.instance().roleExists(entity.getIdentificador()));
        }
    };
    
    private final RunnableTest<Papel> updateSuccess = new RunnableTest<Papel>() {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            
            getCrudActions().resetInstance(entity.getIdPapel());
            getCrudActions().setEntityValue("nome", entity.getNome()+".changed");
            assertEquals("updateNome", UPDATED, getCrudActions().save());
            assertEquals("ends with .changed", true, getCrudActions().getInstance().getNome().endsWith(".changed"));
        }
    };

    
    
    private final RunnableTest<Papel> removeSuccess = new RunnableTest<Papel>() {
        @Override
        protected void testComponent() throws Exception {
            final CrudActions<Papel> crudActions = getCrudActions();
            final Papel entity = getEntity();
            crudActions.resetInstance(entity.getIdPapel());
            assertEquals("removed",true, REMOVED.equals(crudActions.remove(entity)));
            
            assertEquals("roleExists", false, IdentityManager.instance().roleExists(entity.getIdentificador()));
        }
    };
    
    private final void addHerdeiro(final Papel papel, final String... herdeiros) throws Exception {
        new RunnableTest<Papel>() {
            @Override
            protected void testComponent() throws Exception {
                final CrudActions<Papel> crudActions = getCrudActions();
                crudActions.resetInstance(getEntity().getIdPapel());
                crudActions.setComponentValue("activeInnerTab", "herdeirosTab");
                final ArrayList<String> membros = new ArrayList<String>((List<String>)crudActions.getComponentValue("membros"));
                for (String string : herdeiros) {
                    membros.add(string);    
                }
                crudActions.setComponentValue("membros", membros);
                assertEquals("updated", true, UPDATED.equals(crudActions.save()));
                setEntity(crudActions.resetInstance(getEntity().getIdPapel()));
            }
        }.runTest(papel);
    }
    
    private final void removeHerdeiro(final Papel papel, final String... herdeiros) throws Exception {
        new RunnableTest<Papel>() {
            @Override
            protected void testComponent() throws Exception {
                final CrudActions<Papel> crudActions = getCrudActions();
                crudActions.setComponentValue("activeInnerTab", "herdeirosTab");
                final ArrayList<String> membros = new ArrayList<>((List<String>)crudActions.getComponentValue("membros"));
                for (String string : herdeiros) {
                    membros.add(string);    
                }
                crudActions.setComponentValue("membros", membros);
                crudActions.save();
            }
        }.runTest(papel);
    }
    
    @Test
    public void removeSuccessTest() throws Exception {
        final Papel admin = persistSuccess.runTest(new Papel("Admin.rem.suc","admin.rem.suc"));
        final Papel gestor = persistSuccess.runTest(new Papel("Gestor.rem.suc","gestor.rem.suc"));
        final Papel comprador = persistSuccess.runTest(new Papel("Comprador.rem.suc","comprador.rem.suc"));
        final Papel colaborador = persistSuccess.runTest(new Papel("Colaborador.rem.suc","colab.rem.suc"));
        final Papel redator = persistSuccess.runTest(new Papel("Redator.rem.suc","redator.rem.suc"));
        final Papel vendedor = persistSuccess.runTest(new Papel("Vendedor.rem.suc","vendedor.rem.suc"));
        
        addHerdeiro(redator, admin.getIdentificador());
        addHerdeiro(gestor, admin.getIdentificador());
        addHerdeiro(comprador, gestor.getIdentificador());
        addHerdeiro(vendedor, gestor.getIdentificador());
        addHerdeiro(colaborador, comprador.getIdentificador(), vendedor.getIdentificador());

        removeSuccess.runTest(admin);
        removeSuccess.runTest(colaborador);
        removeSuccess.runTest(vendedor);
        removeSuccess.runTest(comprador);
        removeSuccess.runTest(gestor);
        removeSuccess.runTest(redator);
    }
    
    private final RunnableTest<Papel> updateFail = new RunnableTest<Papel>() {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();

            getCrudActions().resetInstance(entity.getIdPapel());
            getCrudActions().setEntityValue("nome", fillStr(entity.getNome()+".changed",LengthConstants.NOME_PADRAO+1));
            assertEquals("updateNome", false, UPDATED.equals(getCrudActions().save()));
            assertEquals("ends with .changed", false, getCrudActions().getInstance().getNome().endsWith(".changed"));

            getCrudActions().resetInstance(entity.getIdPapel());
            getCrudActions().setEntityValue("identificador", fillStr(entity.getIdentificador()+".changed",LengthConstants.DESCRICAO_PADRAO+1));
            assertEquals("updateIdentificador", false, UPDATED.equals(getCrudActions().save()));
            assertEquals("ends with .changed", false, getCrudActions().getInstance().getNome().endsWith(".changed"));

            getCrudActions().resetInstance(entity.getIdPapel());
            getCrudActions().setEntityValue("identificador", null);
            assertEquals("updateIdentificador", false, UPDATED.equals(getCrudActions().save()));
            assertEquals("ends with .changed", false, getCrudActions().getInstance().getNome().endsWith(".changed"));

//            getCrudActions().resetInstance(entity.getIdPapel());
//            getCrudActions().setEntityValue("identificador", entity.getIdentificador()+".changed");
//            assertEquals("isManaged", Boolean.TRUE, getCrudActions().invokeMethod("isManaged", Boolean.class));
//            assertEquals("updateIdentificador regular", false, UPDATED.equals(getCrudActions().save()));
//            assertEquals("ends with .changed", false, getCrudActions().getInstance().getNome().endsWith(".changed"));
        }
    };
    
    @Test
    public void updateFailTest() throws Exception {
        final Papel admin = persistSuccess.runTest(new Papel("Admin.upd.fail","admin.upd.fail"));
        final Papel gestor = persistSuccess.runTest(new Papel("Gestor.upd.fail","gestor.upd.fail"));
        final Papel comprador = persistSuccess.runTest(new Papel("Comprador.upd.fail","comprador.upd.fail"));
        final Papel colaborador = persistSuccess.runTest(new Papel("Colaborador.upd.fail","colab.upd.fail"));
        final Papel redator = persistSuccess.runTest(new Papel("Redator.upd.fail","redator.upd.fail"));
        final Papel vendedor = persistSuccess.runTest(new Papel("Vendedor.upd.fail","vendedor.upd.fail"));
        
        updateFail.runTest(admin);
        updateFail.runTest(gestor);
        updateFail.runTest(comprador);
        updateFail.runTest(colaborador);
        updateFail.runTest(redator);
        updateFail.runTest(vendedor);
    }
    
    @Test
    public void updateSuccessTest() throws Exception {
        final Papel admin = persistSuccess.runTest(new Papel("Admin.upd.suc","admin.upd.suc"));
        final Papel gestor = persistSuccess.runTest(new Papel("Gestor.upd.suc","gestor.upd.suc"));
        final Papel comprador = persistSuccess.runTest(new Papel("Comprador.upd.suc","comprador.upd.suc"));
        final Papel colaborador = persistSuccess.runTest(new Papel("Colaborador.upd.suc","colab.upd.suc"));
        final Papel redator = persistSuccess.runTest(new Papel("Redator.upd.suc","redator.upd.suc"));
        final Papel vendedor = persistSuccess.runTest(new Papel("Vendedor.upd.suc","vendedor.upd.suc"));
        
        updateSuccess.runTest(admin);
        updateSuccess.runTest(gestor);
        updateSuccess.runTest(comprador);
        updateSuccess.runTest(colaborador);
        updateSuccess.runTest(redator);
        updateSuccess.runTest(vendedor);
    }
    
    @Test
    public void persistFailTest() throws Exception{
        persistFail.runTest(new Papel(fillStr("Administrador Admin.fail",LengthConstants.NOME_PADRAO+1),"adminRole"));
        persistFail.runTest(new Papel(null,fillStr("admin",LengthConstants.DESCRICAO_PADRAO+1)));
        persistFail.runTest(new Papel("Gestor",null));
    }
    
}
