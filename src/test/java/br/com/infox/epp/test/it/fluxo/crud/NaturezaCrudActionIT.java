package br.com.infox.epp.test.it.fluxo.crud;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.action.AbstractAction;
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
import org.junit.Assert;

//@RunWith(Arquillian.class)
public class NaturezaCrudActionIT extends AbstractCrudTest<Natureza> {

    private static final String DEFAULT_VALUE = "natureza-p";

    private static final ActionContainer<Natureza> initEntityAction = new ActionContainer<Natureza>() {
        @Override
        public void execute(final CrudActions<Natureza> crudActions) {
            final Natureza entity = getEntity();
            crudActions.setEntityValue("natureza", entity.getNatureza()); // *
            crudActions.setEntityValue("hasPartes", entity.getHasPartes());
            crudActions.setEntityValue("tipoPartes", entity.getTipoPartes());
            crudActions.setEntityValue("numeroPartesFisicas",
                    entity.getNumeroPartesFisicas());
            crudActions.setEntityValue("numeroPartesJuridicas",
                    entity.getNumeroPartesJuridicas());
            crudActions.setEntityValue("ativo", entity.getAtivo());
        }
    };

    private static int i = 0;

    private static final Boolean[] booleans = { Boolean.TRUE, Boolean.FALSE };

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                NaturezaCrudAction.class, NaturezaManager.class,
                NaturezaDAO.class, ParteProcessoEnum.class).createDeployment();
    }

    public static final List<Natureza> getSuccessfullyPersisted(
            final ActionContainer<Natureza> action, final String suffix,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        final PersistSuccessTest<Natureza> persistSuccessTest = new PersistSuccessTest<>(
                NaturezaCrudAction.NAME, NaturezaCrudActionIT.initEntityAction);
        final ArrayList<Natureza> naturezas = new ArrayList<>();
        int i = 0;
        for (final Boolean hasParte : NaturezaCrudActionIT.booleans) {
            for (final Boolean ativo : NaturezaCrudActionIT.booleans) {
                if (hasParte) {
                    for (final ParteProcessoEnum tipo : ParteProcessoEnum
                            .values()) {
                        final Natureza natureza = persistSuccessTest.runTest(
                                action,
                                new Natureza(MessageFormat.format(
                                        "Natureza{0}{1}", suffix, ++i),
                                        hasParte, tipo, 2, ativo),
                                servletContext, session);
                        naturezas.add(natureza);
                    }
                } else {
                    final Natureza natureza = persistSuccessTest.runTest(
                            action,
                            new Natureza(MessageFormat.format("Natureza{0}{1}",
                                    suffix, ++i), hasParte, null, null, ativo),
                            servletContext, session);
                    naturezas.add(natureza);
                }
            }
        }
        return naturezas;
    }

    @Override
    protected boolean compareEntityValues(final Natureza entity,
            final CrudActions<Natureza> crudActions) {
        return compareObjects(entity.getNatureza(),
                crudActions.getEntityValue("natureza"))
                && compareObjects(entity.getHasPartes(),
                        crudActions.getEntityValue("hasPartes"))
                && compareObjects(entity.getAtivo(),
                        crudActions.getEntityValue("ativo"));
    }

    private boolean compareObjects(final Object obj1, final Object obj2) {
        return (obj1 == obj2) || ((obj1 != null) && obj1.equals(obj2));
    }

    @Override
    protected String getComponentName() {
        return NaturezaCrudAction.NAME;
    }

    private String getDescription(final String defaultValue) {
        return MessageFormat.format("{0}{1}", defaultValue,
                ++NaturezaCrudActionIT.i);
    }

    //@Test
    public void inactivateSuccessTest() throws Exception {
        for (final Boolean hasParte : NaturezaCrudActionIT.booleans) {
            for (final Boolean ativo : NaturezaCrudActionIT.booleans) {
                for (final ParteProcessoEnum tipoPartes : ParteProcessoEnum
                        .values()) {
                    this.inactivateSuccess.runTest(new Natureza(
                            getDescription(NaturezaCrudActionIT.DEFAULT_VALUE),
                            hasParte, tipoPartes, 2, ativo),
                            this.servletContext, this.session);
                }
            }
        }
    }

    @Override
    protected ActionContainer<Natureza> getInitEntityAction() {
        return NaturezaCrudActionIT.initEntityAction;
    }

    //@Test
    public void persistFailTest() throws Exception {
        final String[] naturezas = {
                "",
                null,
                fillStr(getDescription(NaturezaCrudActionIT.DEFAULT_VALUE),
                        LengthConstants.DESCRICAO_PEQUENA + 1) };

        for (final String natureza : naturezas) {
            for (final Boolean hasParte : NaturezaCrudActionIT.booleans) {
                for (final Boolean ativo : NaturezaCrudActionIT.booleans) {
                    for (final ParteProcessoEnum tipoPartes : ParteProcessoEnum
                            .values()) {
                        this.persistFail.runTest(new Natureza(natureza,
                                hasParte, tipoPartes, 2, ativo),
                                this.servletContext, this.session);
                    }
                }
            }
        }
        for (final Boolean ativo : NaturezaCrudActionIT.booleans) {
            this.persistFail.runTest(new Natureza(
                    getDescription(NaturezaCrudActionIT.DEFAULT_VALUE), null,
                    null, null, ativo), this.servletContext, this.session);
        }
        for (final ParteProcessoEnum tipoPartes : ParteProcessoEnum.values()) {
            for (final Boolean hasParte : NaturezaCrudActionIT.booleans) {
                this.persistFail.runTest(new Natureza(
                        getDescription(NaturezaCrudActionIT.DEFAULT_VALUE),
                        hasParte, tipoPartes, 2, null), this.servletContext,
                        this.session);
            }
            this.persistFail.runTest(new Natureza(
                    getDescription(NaturezaCrudActionIT.DEFAULT_VALUE),
                    Boolean.TRUE, null, 2, true), this.servletContext,
                    this.session);
            this.persistFail.runTest(new Natureza(
                    getDescription(NaturezaCrudActionIT.DEFAULT_VALUE),
                    Boolean.TRUE, tipoPartes, null, false),
                    this.servletContext, this.session);
        }
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        NaturezaCrudActionIT.getSuccessfullyPersisted(null, "persist-suc",
                this.servletContext, this.session);
    }

    //@Test
    public void updateFailTest() throws Exception {
        final ActionContainer<Natureza> actionContainer = new ActionContainer<Natureza>() {
            @Override
            public void execute(final CrudActions<Natureza> crudActions) {
                final Natureza entity = getEntity();
                final Integer id = crudActions.getId();
                Assert.assertNotNull("id not null", id);

                final String[] naturezas = {
                        "",
                        null,
                        fillStr(getDescription(NaturezaCrudActionIT.DEFAULT_VALUE)
                                + ".changed",
                                LengthConstants.DESCRICAO_PEQUENA + 1) };

                crudActions.resetInstance(id);
                Assert.assertEquals("afterPersisted equals passed instance",
                        true, compareEntityValues(entity, crudActions));
                for (final String natureza : naturezas) {
                    crudActions.setEntityValue("natureza", natureza);
                    Assert.assertEquals(AbstractAction.UPDATED, false,
                            AbstractAction.UPDATED.equals(crudActions.save()));
                    crudActions.resetInstance(id);
                    Assert.assertEquals("attribute changed", true,
                            ((String) crudActions.getEntityValue("natureza"))
                                    .equals(entity.getNatureza()));
                    Assert.assertEquals(
                            "afterPersisted equals passed instance", true,
                            compareEntityValues(entity, crudActions));
                }

                Assert.assertEquals("afterPersisted equals passed instance",
                        true, compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("hasPartes", null);
                Assert.assertEquals(AbstractAction.UPDATED, false,
                        AbstractAction.UPDATED.equals(crudActions.save()));
                crudActions.resetInstance(id);
                Assert.assertEquals(
                        "attribute didn't changed",
                        true,
                        entity.getHasPartes().equals(
                                crudActions.getEntityValue("hasPartes")));
                Assert.assertEquals("afterUpdated equals passed instance",
                        true, compareEntityValues(entity, crudActions));

                crudActions.setEntityValue("ativo", null);
                Assert.assertEquals(AbstractAction.UPDATED, false,
                        AbstractAction.UPDATED.equals(crudActions.save()));
                crudActions.resetInstance(id);
                Assert.assertEquals("attribute changed", true, entity
                        .getAtivo().equals(crudActions.getEntityValue("ativo")));
                Assert.assertEquals("afterUpdated equals passed instance",
                        true, compareEntityValues(entity, crudActions));
            }
        };
        NaturezaCrudActionIT.getSuccessfullyPersisted(actionContainer,
                "update-fail", this.servletContext, this.session);
    }

    //@Test
    public void updateSuccessTest() throws Exception {
        final ActionContainer<Natureza> actionContainer = new ActionContainer<Natureza>() {
            @Override
            public void execute(final CrudActions<Natureza> crudActions) {
                final Natureza entity = getEntity();
                final Integer id = crudActions.getId();

                Assert.assertNotNull("ID WAS NULL", id);

                crudActions.resetInstance(id);

                Assert.assertEquals("entities are not equal", true,
                        compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("natureza", MessageFormat.format(
                        "{0}.{1}", crudActions.getEntityValue("natureza"),
                        "changed"));
                Assert.assertEquals("update fail natureza",
                        AbstractAction.UPDATED, crudActions.save());
                Assert.assertEquals("attribute didn't changed", true,
                        ((String) crudActions.getEntityValue("natureza"))
                                .endsWith(".changed"));

                crudActions.resetInstance(id);

                Assert.assertEquals("entities are equal", false,
                        compareEntityValues(entity, crudActions));
                entity.setNatureza((String) crudActions
                        .getEntityValue("natureza"));

                Assert.assertEquals("entities are not equal", true,
                        compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("hasPartes", !entity.getHasPartes());
                if (!entity.getHasPartes()) {
                    crudActions.setEntityValue("tipoPartes",
                            ParteProcessoEnum.A);
                    crudActions.setEntityValue("numeroPartesFisicas", 2);
                }
                Assert.assertEquals("update fail hasPartes",
                        AbstractAction.UPDATED, crudActions.save());
                Assert.assertEquals(
                        "attribute changed",
                        false,
                        entity.getHasPartes().equals(
                                crudActions.getEntityValue("hasPartes")));

                crudActions.resetInstance(id);

                Assert.assertEquals("entities are equal", false,
                        compareEntityValues(entity, crudActions));
                entity.setHasPartes((Boolean) crudActions
                        .getEntityValue("hasPartes"));

                Assert.assertEquals("entities are not equal", true,
                        compareEntityValues(entity, crudActions));
                crudActions.setEntityValue("ativo", !entity.getAtivo());
                Assert.assertEquals("update fail ativo",
                        AbstractAction.UPDATED, crudActions.save());
                crudActions.resetInstance(id);

                Assert.assertEquals("afterUpdated equals passed instance",
                        false, compareEntityValues(entity, crudActions));
            }
        };

        NaturezaCrudActionIT.getSuccessfullyPersisted(actionContainer,
                "update-suc", this.servletContext, this.session);
    }

}
