package br.com.infox.epp.test.it.access.crud;

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
import org.jboss.seam.security.Role;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.crud.PapelCrudAction;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Permissao;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class PapelCrudActionIT extends AbstractCrudTest<Papel>{

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(PapelCrudAction.class,RolesMap.class,PapelManager.class,
                    RecursoManager.class,PapelDAO.class,RecursoDAO.class)
        .createDeployment();
    }

    @Override
    protected void initEntity(final Papel entity, final CrudActions<Papel> crud) {
        crud.setEntityValue("identificador", entity.getIdentificador()); //req
        crud.setEntityValue("nome", entity.getNome()); // req
    }

    @Override
    protected String getComponentName() {
        return PapelCrudAction.NAME;
    }
    
    private final RunnableTest<Papel> persistSuccess = new RunnableTest<Papel>(PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            
            this.newInstance();
            initEntity(entity, this);
            assertEquals("persisted", PERSISTED, this.save());

            final Integer id = this.getId();
            assertNotNull("id", id);
            this.newInstance();
            assertNull("nullId", this.getId());
            this.setId(id);
            assertEquals("Compare", true, compareEntityValues(entity, this));
            
            final boolean roleExists = IdentityManager.instance().roleExists(entity.getIdentificador());
            assertEquals("roleExists", true, roleExists);
            
            setEntity(this.getInstance());
        }
    };
    
    @Test
    public void persistSuccessTest() throws Exception {
        persistSuccess.runTest(new Papel("Administrador Admin","admin"), servletContext, session);
        persistSuccess.runTest(new Papel("Gestor","gestor"), servletContext, session);
        persistSuccess.runTest(new Papel("Comprador","comprador"), servletContext, session);
        persistSuccess.runTest(new Papel("Colaborador","colab"), servletContext, session);
        persistSuccess.runTest(new Papel("Redator","redator"), servletContext, session);
        persistSuccess.runTest(new Papel("Vendedor","vendedor"), servletContext, session);
    }
    /*
    private String jsonPapel(Papel papel) {
        return format("'{'id:{0},nome:{1},role:{2},ativo:{3}'}'", papel.getIdPapel(), papel.getNome(), papel.getIdentificador(), papel.getAtivo());
    }
    */
    private final RunnableTest<Papel> persistFail = new RunnableTest<Papel>(PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            this.newInstance();
            initEntity(entity, this);
            
            assertEquals("persisted", true, !PERSISTED.equals(this.save()));

            final Integer id = this.getId();
            assertNull("id", id);
            assertEquals("roleExists", true, !IdentityManager.instance().roleExists(entity.getIdentificador()));
        }
    };
    
    private final RunnableTest<Papel> updateSuccess = new RunnableTest<Papel>(PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            
            this.resetInstance(entity.getIdPapel());
            this.setEntityValue("nome", entity.getNome()+".changed");
            assertEquals("updateNome", UPDATED, this.save());
            assertEquals("ends with .changed", true, this.getInstance().getNome().endsWith(".changed"));
        }
    };

    private final RunnableTest<Recurso> createRoles = new RunnableTest<Recurso>(RecursoManager.NAME) {
        @Override
        protected void testComponent() throws Exception {
            for (int i = 0; i < 25; i++) {
                createRole(format("/pages/RecursoTeste/recurso{0}.seam", i));
            }
        }
        
        private void createRole(final String pageRole) {
            if (invokeMethod("existsRecurso", Boolean.TYPE, pageRole)){
                return;
            }
            final Recurso recurso = new Recurso(pageRole, pageRole);
            try {
                final Class<?>[] types = {Object.class};
                invokeMethod("persist", Recurso.class, types, recurso);
            } catch (final Exception e) {
                if (e instanceof DAOException) {
                } else {
                    throw e;
                }
            }
            final Permission permission = new Permission(pageRole, "access", new Role("admin"));
            PermissionManager.instance().getPermissionStore().grantPermission(permission);
        }
    };
    
    private final RunnableTest<Papel> removeSuccess = new RunnableTest<Papel>(PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            final Papel instance = this.resetInstance(entity.getIdPapel());
            assertEquals("removed",true, REMOVED.equals(this.remove(instance)));
            
            assertEquals("roleExists", false, IdentityManager.instance().roleExists(entity.getIdentificador()));
        }
    };
    
    
    private final void addRecursos(final Papel papel, final String... recursos) throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                this.resetInstance(getEntity().getIdPapel());
                this.setComponentValue("activeInnerTab", "recursosTab");
                final ArrayList<String> recursoList = new ArrayList<>((List<String>)this.getComponentValue("recursos"));
                for (final String recurso : recursos) {
                    recursoList.add(recurso);
                }
                this.setComponentValue("recursos", recursoList);
                final String save = this.save();
                assertEquals("updated", Boolean.TRUE, Boolean.valueOf(UPDATED.equals(save)));

                final Papel instance = this.resetInstance(getEntity().getIdPapel());
                setEntity(instance);
                
                final List<Permissao> permissoesFromRole = PermissionManager.instance().getPermissoesFromRole(new Role(papel.getIdentificador()));
                for(final String recurso : recursos) {
                    assertEquals("Permissão", Boolean.TRUE, recursoExisteEmPermissoes(recurso, permissoesFromRole));
                }
            }
        }.runTest(papel, servletContext, session);
    }
    
    private Boolean recursoExisteEmPermissoes(final String recurso, final List<Permissao> permissoesFromRole) {
        Boolean found = Boolean.FALSE;
        for (final Permissao obj : permissoesFromRole) {
            found = Boolean.valueOf(recurso.equals(obj.getAlvo()));
            if (found) {
                break;
            }
        }
        return found;
    }
    
    private final void addLicenciadores(final Papel papel, final Papel... licenciadores) throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                this.resetInstance(getEntity().getIdPapel());
                this.setComponentValue("activeInnerTab", "papeisTab");
                final ArrayList<String> licenciadorList = new ArrayList<String>((List<String>)this.getComponentValue("papeis"));
                for (final Papel licenciador : licenciadores) {
                    licenciadorList.add(licenciador.getIdentificador());    
                }
                this.setComponentValue("papeis", licenciadorList);
                assertEquals("não possui permissão",Boolean.FALSE, Boolean.valueOf(IdentityManager.instance().getRoleGroups(papel.getIdentificador()).containsAll(licenciadorList)));
                assertEquals("updated", Boolean.TRUE, Boolean.valueOf(UPDATED.equals(this.save())));
                final Papel instance = this.resetInstance(getEntity().getIdPapel());
                setEntity(instance);
                assertEquals("possui permissão",Boolean.TRUE, Boolean.valueOf(IdentityManager.instance().getRoleGroups(papel.getIdentificador()).containsAll(licenciadorList)));
            }
        }.runTest(papel, servletContext, session);
    }
    
    private final void addHerdeiros(final Papel entity, final Papel... herdeiros) throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                resetInstance(entity.getIdPapel());
                setComponentValue("activeInnerTab", "herdeirosTab");
                final ArrayList<String> herdeiroList = new ArrayList<>((List<String>)this.getComponentValue("membros"));
                for (final Papel herdeiro : herdeiros) {
                    herdeiroList.add(herdeiro.getIdentificador());
                    assertEquals("herdeiroNãoRecebePermissão", Boolean.FALSE, Boolean.valueOf(IdentityManager.instance().getRoleGroups(herdeiro.getIdentificador()).contains(entity.getIdentificador())));
                }
                setComponentValue("membros", herdeiroList);
                
                assertEquals("updated", true, UPDATED.equals(save()));
                
                for (final String herdeiro : herdeiroList) {
                    assertEquals("herdeiroRecebePermissão", Boolean.TRUE, Boolean.valueOf(IdentityManager.instance().getRoleGroups(herdeiro).contains(entity.getIdentificador())));
                }
                setEntity(resetInstance(entity.getIdPapel()));
            }
        }.runTest(servletContext, session);
    }
    
    private final void removeHerdeiros(final Papel papel, final Papel... herdeiros) throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                this.setComponentValue("activeInnerTab", "herdeirosTab");
                int idPapel = papel.getIdPapel();
                resetInstance(idPapel);
                final ArrayList<String> membros = new ArrayList<>((List<String>)this.getComponentValue("membros"));
                for (final Papel herdeiro : herdeiros) {
                    membros.remove(herdeiro.getIdentificador());
                }
                this.setComponentValue("membros", membros);
                assertEquals("updated", true, UPDATED.equals(save()));
                for (final Papel herdeiro : herdeiros) {
                    assertEquals("herdeiroRecebePermissão", Boolean.FALSE, Boolean.valueOf(IdentityManager.instance().getRoleGroups(herdeiro.getIdentificador()).contains(papel.getIdentificador())));
                }
                setEntity(resetInstance(idPapel));
            }
        }.runTest(servletContext, session);
    }
    
    @Test
    public void removeSuccessTest() throws Exception {
        final Papel admin = persistSuccess.runTest(new Papel("Admin.rem.suc","admin.rem.suc"), servletContext, session);
        final Papel gestor = persistSuccess.runTest(new Papel("Gestor.rem.suc","gestor.rem.suc"), servletContext, session);
        final Papel comprador = persistSuccess.runTest(new Papel("Comprador.rem.suc","comprador.rem.suc"), servletContext, session);
        final Papel colaborador = persistSuccess.runTest(new Papel("Colaborador.rem.suc","colab.rem.suc"), servletContext, session);
        final Papel redator = persistSuccess.runTest(new Papel("Redator.rem.suc","redator.rem.suc"), servletContext, session);
        final Papel vendedor = persistSuccess.runTest(new Papel("Vendedor.rem.suc","vendedor.rem.suc"), servletContext, session);
        
        addHerdeiros(redator, admin);
        addHerdeiros(gestor, admin);
        addHerdeiros(comprador, gestor);
        addHerdeiros(vendedor, gestor);
        addHerdeiros(colaborador, comprador, vendedor);

        removeHerdeiros(colaborador, comprador);
        
        removeSuccess.runTest(admin, servletContext, session);
        removeSuccess.runTest(gestor, servletContext, session);
        removeSuccess.runTest(redator, servletContext, session);
        removeSuccess.runTest(vendedor, servletContext, session);
        removeSuccess.runTest(colaborador, servletContext, session);
        removeSuccess.runTest(comprador, servletContext, session);
    }
    
    private final RunnableTest<Papel> updateFail = new RunnableTest<Papel>(PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();

            this.resetInstance(entity.getIdPapel());
            this.setEntityValue("nome", fillStr(entity.getNome()+".changed",LengthConstants.NOME_PADRAO+1));
            assertEquals("updateNome", false, UPDATED.equals(this.save()));
            assertEquals("ends with .changed", false, this.getInstance().getNome().endsWith(".changed"));

            this.resetInstance(entity.getIdPapel());
            this.setEntityValue("identificador", fillStr(entity.getIdentificador()+".changed",LengthConstants.DESCRICAO_PADRAO+1));
            assertEquals("updateIdentificador", false, UPDATED.equals(this.save()));
            assertEquals("ends with .changed", false, this.getInstance().getNome().endsWith(".changed"));

            this.resetInstance(entity.getIdPapel());
            this.setEntityValue("identificador", null);
            assertEquals("updateIdentificador", false, UPDATED.equals(this.save()));
            assertEquals("ends with .changed", false, this.getInstance().getNome().endsWith(".changed"));

//            resetInstance(entity.getIdPapel());
//            setEntityValue("identificador", entity.getIdentificador()+".changed");
//            assertEquals("isManaged", Boolean.TRUE, invokeMethod("isManaged", Boolean.class));
//            assertEquals("updateIdentificador regular", false, UPDATED.equals(save()));
//            assertEquals("ends with .changed", false, getInstance().getNome().endsWith(".changed"));
        }
    };
    
    @Test
    public void updateFailTest() throws Exception {
        final Papel admin = persistSuccess.runTest(new Papel("Admin.upd.fail","admin.upd.fail"), servletContext, session);
        final Papel gestor = persistSuccess.runTest(new Papel("Gestor.upd.fail","gestor.upd.fail"), servletContext, session);
        final Papel comprador = persistSuccess.runTest(new Papel("Comprador.upd.fail","comprador.upd.fail"), servletContext, session);
        final Papel colaborador = persistSuccess.runTest(new Papel("Colaborador.upd.fail","colab.upd.fail"), servletContext, session);
        final Papel redator = persistSuccess.runTest(new Papel("Redator.upd.fail","redator.upd.fail"), servletContext, session);
        final Papel vendedor = persistSuccess.runTest(new Papel("Vendedor.upd.fail","vendedor.upd.fail"), servletContext, session);
        
        updateFail.runTest(admin, servletContext, session);
        updateFail.runTest(gestor, servletContext, session);
        updateFail.runTest(comprador, servletContext, session);
        updateFail.runTest(colaborador, servletContext, session);
        updateFail.runTest(redator, servletContext, session);
        updateFail.runTest(vendedor, servletContext, session);
    }
    
    @Test
    public void updateSuccessTest() throws Exception {
        final Papel admin = persistSuccess.runTest(new Papel("Admin.upd.suc","admin.upd.suc"), servletContext, session);
        final Papel gestor = persistSuccess.runTest(new Papel("Gestor.upd.suc","gestor.upd.suc"), servletContext, session);
        final Papel comprador = persistSuccess.runTest(new Papel("Comprador.upd.suc","comprador.upd.suc"), servletContext, session);
        final Papel colaborador = persistSuccess.runTest(new Papel("Colaborador.upd.suc","colab.upd.suc"), servletContext, session);
        final Papel redator = persistSuccess.runTest(new Papel("Redator.upd.suc","redator.upd.suc"), servletContext, session);
        final Papel vendedor = persistSuccess.runTest(new Papel("Vendedor.upd.suc","vendedor.upd.suc"), servletContext, session);

        createRoles.runTest(servletContext, session);
        updateSuccess.runTest(admin, servletContext, session);
        updateSuccess.runTest(gestor, servletContext, session);
        updateSuccess.runTest(comprador, servletContext, session);
        updateSuccess.runTest(colaborador, servletContext, session);
        updateSuccess.runTest(redator, servletContext, session);
        updateSuccess.runTest(vendedor, servletContext, session);
        
        addHerdeiros(redator, admin);
        addHerdeiros(gestor, admin);
        addLicenciadores(colaborador, gestor, comprador, vendedor);
        final ArrayList<String> recursos = new ArrayList<>(0);
        for (int i = 0; i < 25; i++) {
            recursos.add(format("/pages/RecursoTeste/recurso{0}.seam", i));
        }
        addRecursos(admin, recursos.toArray(new String[recursos.size()]));
        addRecursos(gestor, pickRandom(recursos, 15));
        addRecursos(comprador, pickRandom(recursos, 10));
        addRecursos(vendedor, pickRandom(recursos, 9));
        addRecursos(colaborador, pickRandom(recursos, 5));
        addRecursos(redator, pickRandom(recursos, 1));
    }
    
    private String[] pickRandom(final List<String> resources, final int ammount) {
        final ArrayList<String> resultList = new ArrayList<>();
        final int size = resources.size();
        
        for (int i = 0; i < ammount; i++) {
            resultList.add(resources.get((int)(Math.random()*size)));
        }
        
        return resultList.toArray(new String[ammount]);
    }
    
    @Test
    public void persistFailTest() throws Exception{
        persistFail.runTest(new Papel(fillStr("Administrador Admin.fail",LengthConstants.NOME_PADRAO+1),"adminRole"), servletContext, session);
        persistFail.runTest(new Papel(null,fillStr("admin",LengthConstants.DESCRICAO_PADRAO+1)), servletContext, session);
        persistFail.runTest(new Papel("Gestor",null), servletContext, session);
    }
    
}
