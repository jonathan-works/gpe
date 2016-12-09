package br.com.infox.epp.test.it.access.crud;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.com.infox.constants.LengthConstants;
import br.com.infox.constants.WarningConstants;
import br.com.infox.core.action.AbstractAction;
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
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import junit.framework.Assert;

//@RunWith(Arquillian.class)
public class PapelCrudActionIT extends AbstractCrudTest<Papel> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(PapelCrudAction.class,
                RolesMap.class, PapelManager.class, RecursoManager.class,
                PapelDAO.class, RecursoDAO.class).createDeployment();
    }

    private static final ActionContainer<Papel> initEntityAction = new ActionContainer<Papel>() {
        @Override
        public void execute(final CrudActions<Papel> crudActions) {
            final Papel entity = getEntity();
            crudActions.setEntityValue("identificador",
                    entity.getIdentificador()); // req
            crudActions.setEntityValue("nome", entity.getNome()); // req
        }
    };

    @Override
    protected ActionContainer<Papel> getInitEntityAction() {
        return PapelCrudActionIT.initEntityAction;
    }

    // @Override
    // protected void initEntity(final Papel entity, final CrudActions<Papel>
    // crud) {
    // crud.setEntityValue("identificador", entity.getIdentificador()); //req
    // crud.setEntityValue("nome", entity.getNome()); // req
    // }

    @Override
    protected String getComponentName() {
        return PapelCrudAction.NAME;
    }

    private static final RunnableTest<Papel> persistSuccess = new RunnableTest<Papel>(
            PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();

            this.newInstance();
            PapelCrudActionIT.initEntityAction.execute(entity, this);
            Assert.assertEquals("persisted", AbstractAction.PERSISTED,
                    this.save());

            final Integer id = this.getId();
            Assert.assertNotNull("id", id);
            this.newInstance();
            Assert.assertNull("nullId", this.getId());
            this.setId(id);

            final boolean roleExists = IdentityManager.instance().roleExists(
                    entity.getIdentificador());
            Assert.assertEquals("roleExists", true, roleExists);

            setEntity(this.getInstance());
        }
    };

    public static List<Papel> getSuccessFullyPersisted(
            final ActionContainer<Papel> action, final String suffix,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        final ArrayList<Papel> list = new ArrayList<>();
        list.add(PapelCrudActionIT.persistSuccess.runTest(new Papel(
                MessageFormat.format("Administrador Admin{0}", suffix),
                MessageFormat.format("admin{0}", suffix)), servletContext,
                session));
        list.add(PapelCrudActionIT.persistSuccess.runTest(
                new Papel(MessageFormat.format("Gestor{0}", suffix),
                        MessageFormat.format("gestor{0}", suffix)),
                servletContext, session));
        list.add(PapelCrudActionIT.persistSuccess.runTest(
                new Papel(MessageFormat.format("Comprador{0}", suffix),
                        MessageFormat.format("comprador{0}", suffix)),
                servletContext, session));
        list.add(PapelCrudActionIT.persistSuccess.runTest(
                new Papel(MessageFormat.format("Colaborador{0}", suffix),
                        MessageFormat.format("colab{0}", suffix)),
                servletContext, session));
        list.add(PapelCrudActionIT.persistSuccess.runTest(
                new Papel(MessageFormat.format("Redator{0}", suffix),
                        MessageFormat.format("redator{0}", suffix)),
                servletContext, session));
        list.add(PapelCrudActionIT.persistSuccess.runTest(
                new Papel(MessageFormat.format("Vendedor{0}", suffix),
                        MessageFormat.format("vendedor{0}", suffix)),
                servletContext, session));
        return list;
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        PapelCrudActionIT.getSuccessFullyPersisted(null, "per-suc",
                this.servletContext, this.session);
    }

    /*
     * private String jsonPapel(Papel papel) { return
     * format("'{'id:{0},nome:{1},role:{2},ativo:{3}'}'", papel.getIdPapel(),
     * papel.getNome(), papel.getIdentificador(), papel.getAtivo()); }
     */
    private final RunnableTest<Papel> persistFail = new RunnableTest<Papel>(
            PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            this.newInstance();
            PapelCrudActionIT.initEntityAction.execute(entity, this);

            Assert.assertEquals("persisted", true,
                    !AbstractAction.PERSISTED.equals(this.save()));

            final Integer id = this.getId();
            Assert.assertNull("id", id);
            Assert.assertEquals("roleExists", true, !IdentityManager.instance()
                    .roleExists(entity.getIdentificador()));
        }
    };

    private final RunnableTest<Papel> updateSuccess = new RunnableTest<Papel>(
            PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();

            this.resetInstance(entity.getIdPapel());
            this.setEntityValue("nome", entity.getNome() + ".changed");
            Assert.assertEquals("updateNome", AbstractAction.UPDATED,
                    this.save());
            Assert.assertEquals("ends with .changed", true, this.getInstance()
                    .getNome().endsWith(".changed"));
        }
    };

    private final RunnableTest<Recurso> createRoles = new RunnableTest<Recurso>(
            RecursoManager.NAME) {
        @Override
        protected void testComponent() throws Exception {
            for (int i = 0; i < 25; i++) {
                createRole(MessageFormat.format(
                        "/pages/RecursoTeste/recurso{0}.seam", i));
            }
        }

        private void createRole(final String pageRole) {
            if (invokeMethod("existsRecurso", Boolean.TYPE, pageRole)) {
                return;
            }
            final Recurso recurso = new Recurso(pageRole, pageRole);
            try {
                final Class<?>[] types = { Object.class };
                invokeMethod("persist", Recurso.class, types, recurso);
            } catch (final Exception e) {
                if (e instanceof DAOException) {
                } else {
                    throw e;
                }
            }
            final Permission permission = new Permission(pageRole, "access",
                    new Role("admin"));
            PermissionManager.instance().getPermissionStore()
                    .grantPermission(permission);
        }
    };

    private final RunnableTest<Papel> removeSuccess = new RunnableTest<Papel>(
            PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            final Papel instance = this.resetInstance(entity.getIdPapel());
            Assert.assertEquals("removed", true,
                    AbstractAction.REMOVED.equals(this.remove(instance)));

            Assert.assertEquals("roleExists", false, IdentityManager.instance()
                    .roleExists(entity.getIdentificador()));
        }
    };

    private final void addRecursos(final Papel papel, final String... recursos)
            throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                this.resetInstance(getEntity().getIdPapel());
                this.setComponentValue("activeInnerTab", "recursosTab");
                final ArrayList<String> recursoList = new ArrayList<>(
                        (List<String>) this.getComponentValue("recursos"));
                for (final String recurso : recursos) {
                    recursoList.add(recurso);
                }
                this.setComponentValue("recursos", recursoList);
                final String save = this.save();
                Assert.assertEquals("updated", Boolean.TRUE,
                        Boolean.valueOf(AbstractAction.UPDATED.equals(save)));

                final Papel instance = this.resetInstance(getEntity()
                        .getIdPapel());
                setEntity(instance);

                final List<Permissao> permissoesFromRole = PermissionManager
                        .instance().getPermissoesFromRole(
                                new Role(papel.getIdentificador()));
                for (final String recurso : recursos) {
                    Assert.assertEquals(
                            "Permissão",
                            Boolean.TRUE,
                            recursoExisteEmPermissoes(recurso,
                                    permissoesFromRole));
                }
            }
        }.runTest(papel, this.servletContext, this.session);
    }

    private Boolean recursoExisteEmPermissoes(final String recurso,
            final List<Permissao> permissoesFromRole) {
        Boolean found = Boolean.FALSE;
        for (final Permissao obj : permissoesFromRole) {
            found = Boolean.valueOf(recurso.equals(obj.getAlvo()));
            if (found) {
                break;
            }
        }
        return found;
    }

    private final void addLicenciadores(final Papel papel,
            final Papel... licenciadores) throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                this.resetInstance(getEntity().getIdPapel());
                this.setComponentValue("activeInnerTab", "papeisTab");
                final ArrayList<String> licenciadorList = new ArrayList<String>(
                        (List<String>) this.getComponentValue("papeis"));
                for (final Papel licenciador : licenciadores) {
                    licenciadorList.add(licenciador.getIdentificador());
                }
                this.setComponentValue("papeis", licenciadorList);
                Assert.assertEquals(
                        "não possui permissão",
                        Boolean.FALSE,
                        Boolean.valueOf(IdentityManager.instance()
                                .getRoleGroups(papel.getIdentificador())
                                .containsAll(licenciadorList)));
                Assert.assertEquals("updated", Boolean.TRUE, Boolean
                        .valueOf(AbstractAction.UPDATED.equals(this.save())));
                final Papel instance = this.resetInstance(getEntity()
                        .getIdPapel());
                setEntity(instance);
                Assert.assertEquals(
                        "possui permissão",
                        Boolean.TRUE,
                        Boolean.valueOf(IdentityManager.instance()
                                .getRoleGroups(papel.getIdentificador())
                                .containsAll(licenciadorList)));
            }
        }.runTest(papel, this.servletContext, this.session);
    }

    private final void addHerdeiros(final Papel entity,
            final Papel... herdeiros) throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                resetInstance(entity.getIdPapel());
                setComponentValue("activeInnerTab", "herdeirosTab");
                final ArrayList<String> herdeiroList = new ArrayList<>(
                        (List<String>) this.getComponentValue("membros"));
                for (final Papel herdeiro : herdeiros) {
                    herdeiroList.add(herdeiro.getIdentificador());
                    Assert.assertEquals(
                            "herdeiroNãoRecebePermissão",
                            Boolean.FALSE,
                            Boolean.valueOf(IdentityManager.instance()
                                    .getRoleGroups(herdeiro.getIdentificador())
                                    .contains(entity.getIdentificador())));
                }
                setComponentValue("membros", herdeiroList);

                Assert.assertEquals("updated", true,
                        AbstractAction.UPDATED.equals(save()));

                for (final String herdeiro : herdeiroList) {
                    Assert.assertEquals(
                            "herdeiroRecebePermissão",
                            Boolean.TRUE,
                            Boolean.valueOf(IdentityManager.instance()
                                    .getRoleGroups(herdeiro)
                                    .contains(entity.getIdentificador())));
                }
                setEntity(resetInstance(entity.getIdPapel()));
            }
        }.runTest(this.servletContext, this.session);
    }

    private final void removeHerdeiros(final Papel papel,
            final Papel... herdeiros) throws Exception {
        new RunnableTest<Papel>(PapelCrudAction.NAME) {
            @Override
            @SuppressWarnings(WarningConstants.UNCHECKED)
            protected void testComponent() throws Exception {
                this.setComponentValue("activeInnerTab", "herdeirosTab");
                final int idPapel = papel.getIdPapel();
                resetInstance(idPapel);
                final ArrayList<String> membros = new ArrayList<>(
                        (List<String>) this.getComponentValue("membros"));
                for (final Papel herdeiro : herdeiros) {
                    membros.remove(herdeiro.getIdentificador());
                }
                this.setComponentValue("membros", membros);
                Assert.assertEquals("updated", true,
                        AbstractAction.UPDATED.equals(save()));
                for (final Papel herdeiro : herdeiros) {
                    Assert.assertEquals(
                            "herdeiroRecebePermissão",
                            Boolean.FALSE,
                            Boolean.valueOf(IdentityManager.instance()
                                    .getRoleGroups(herdeiro.getIdentificador())
                                    .contains(papel.getIdentificador())));
                }
                setEntity(resetInstance(idPapel));
            }
        }.runTest(this.servletContext, this.session);
    }

    //@Test
    public void removeSuccessTest() throws Exception {
        final Papel admin = PapelCrudActionIT.persistSuccess.runTest(new Papel(
                "Admin.rem.suc", "admin.rem.suc"), this.servletContext,
                this.session);
        final Papel gestor = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Gestor.rem.suc", "gestor.rem.suc"),
                this.servletContext, this.session);
        final Papel comprador = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Comprador.rem.suc", "comprador.rem.suc"),
                this.servletContext, this.session);
        final Papel colaborador = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Colaborador.rem.suc", "colab.rem.suc"),
                this.servletContext, this.session);
        final Papel redator = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Redator.rem.suc", "redator.rem.suc"),
                this.servletContext, this.session);
        final Papel vendedor = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Vendedor.rem.suc", "vendedor.rem.suc"),
                this.servletContext, this.session);

        addHerdeiros(redator, admin);
        addHerdeiros(gestor, admin);
        addHerdeiros(comprador, gestor);
        addHerdeiros(vendedor, gestor);
        addHerdeiros(colaborador, comprador, vendedor);

        removeHerdeiros(colaborador, comprador);

        this.removeSuccess.runTest(admin, this.servletContext, this.session);
        this.removeSuccess.runTest(gestor, this.servletContext, this.session);
        this.removeSuccess.runTest(redator, this.servletContext, this.session);
        this.removeSuccess.runTest(vendedor, this.servletContext, this.session);
        this.removeSuccess.runTest(colaborador, this.servletContext,
                this.session);
        this.removeSuccess
                .runTest(comprador, this.servletContext, this.session);
    }

    private final RunnableTest<Papel> updateFail = new RunnableTest<Papel>(
            PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();

            this.resetInstance(entity.getIdPapel());
            this.setEntityValue(
                    "nome",
                    fillStr(entity.getNome() + ".changed",
                            LengthConstants.NOME_PADRAO + 1));
            Assert.assertEquals("updateNome", false,
                    AbstractAction.UPDATED.equals(this.save()));
            Assert.assertEquals("ends with .changed", false, this.getInstance()
                    .getNome().endsWith(".changed"));

            this.resetInstance(entity.getIdPapel());
            this.setEntityValue(
                    "identificador",
                    fillStr(entity.getIdentificador() + ".changed",
                            LengthConstants.DESCRICAO_PADRAO + 1));
            Assert.assertEquals("updateIdentificador", false,
                    AbstractAction.UPDATED.equals(this.save()));
            Assert.assertEquals("ends with .changed", false, this.getInstance()
                    .getNome().endsWith(".changed"));

            this.resetInstance(entity.getIdPapel());
            this.setEntityValue("identificador", null);
            Assert.assertEquals("updateIdentificador", false,
                    AbstractAction.UPDATED.equals(this.save()));
            Assert.assertEquals("ends with .changed", false, this.getInstance()
                    .getNome().endsWith(".changed"));

            // resetInstance(entity.getIdPapel());
            // setEntityValue("identificador",
            // entity.getIdentificador()+".changed");
            // assertEquals("isManaged", Boolean.TRUE, invokeMethod("isManaged",
            // Boolean.class));
            // assertEquals("updateIdentificador regular", false,
            // UPDATED.equals(save()));
            // assertEquals("ends with .changed", false,
            // getInstance().getNome().endsWith(".changed"));
        }
    };

    //@Test
    public void updateFailTest() throws Exception {
        for (final Papel papel : PapelCrudActionIT.getSuccessFullyPersisted(
                null, "upd.fail", this.servletContext, this.session)) {
            this.updateFail.runTest(papel, this.servletContext, this.session);
        }
    }

    //@Test
    public void updateSuccessTest() throws Exception {
        final Papel admin = PapelCrudActionIT.persistSuccess.runTest(new Papel(
                "Admin.upd.suc", "admin.upd.suc"), this.servletContext,
                this.session);
        final Papel gestor = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Gestor.upd.suc", "gestor.upd.suc"),
                this.servletContext, this.session);
        final Papel comprador = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Comprador.upd.suc", "comprador.upd.suc"),
                this.servletContext, this.session);
        final Papel colaborador = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Colaborador.upd.suc", "colab.upd.suc"),
                this.servletContext, this.session);
        final Papel redator = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Redator.upd.suc", "redator.upd.suc"),
                this.servletContext, this.session);
        final Papel vendedor = PapelCrudActionIT.persistSuccess.runTest(
                new Papel("Vendedor.upd.suc", "vendedor.upd.suc"),
                this.servletContext, this.session);

        this.createRoles.runTest(this.servletContext, this.session);
        this.updateSuccess.runTest(admin, this.servletContext, this.session);
        this.updateSuccess.runTest(gestor, this.servletContext, this.session);
        this.updateSuccess
                .runTest(comprador, this.servletContext, this.session);
        this.updateSuccess.runTest(colaborador, this.servletContext,
                this.session);
        this.updateSuccess.runTest(redator, this.servletContext, this.session);
        this.updateSuccess.runTest(vendedor, this.servletContext, this.session);

        addHerdeiros(redator, admin);
        addHerdeiros(gestor, admin);
        addLicenciadores(colaborador, gestor, comprador, vendedor);
        final ArrayList<String> recursos = new ArrayList<>(0);
        for (int i = 0; i < 25; i++) {
            recursos.add(MessageFormat.format(
                    "/pages/RecursoTeste/recurso{0}.seam", i));
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
            resultList.add(resources.get((int) (Math.random() * size)));
        }

        return resultList.toArray(new String[ammount]);
    }

    //@Test
    public void persistFailTest() throws Exception {
        this.persistFail.runTest(
                new Papel(fillStr("Administrador Admin.fail",
                        LengthConstants.NOME_PADRAO + 1), "adminRole"),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new Papel(null, fillStr("admin",
                        LengthConstants.DESCRICAO_PADRAO + 1)),
                this.servletContext, this.session);
        this.persistFail.runTest(new Papel("Gestor", null),
                this.servletContext, this.session);
    }

}
