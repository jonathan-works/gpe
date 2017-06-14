package br.com.infox.epp.test.it.pessoa.crud;

import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.pessoa.crud.PessoaFisicaCrudAction;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.jsf.validator.CpfValidator;

//@RunWith(Arquillian.class)
public class PessoaFisicaCrudActionIT extends AbstractCrudTest<PessoaFisica> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                PessoaFisicaCrudAction.class, CpfValidator.class,
                PessoaFisicaDAO.class, PessoaFisicaManager.class)
                .createDeployment();
    }

    private static final ActionContainer<PessoaFisica> initEntityAction = new ActionContainer<PessoaFisica>() {
        @Override
        public void execute(final CrudActions<PessoaFisica> crudActions) {
            final PessoaFisica entity = getEntity();
            crudActions.setEntityValue("cpf", entity.getCpf());
            crudActions.setEntityValue("nome", entity.getNome());
            if (entity.getDataNascimento() != null) {
                crudActions.setEntityValue("dataNascimento", new Date(entity
                        .getDataNascimento().getTime()));
            } else {
                crudActions.setEntityValue("dataNascimento", null);
            }
            crudActions.setEntityValue("ativo", entity.getAtivo());
            // id="cpf"
            // required="true" />
            // id="nome"
            // required="true" />
            // id="dataNascimento"
            // required="true" />
        }
    };

    @Override
    protected ActionContainer<PessoaFisica> getInitEntityAction() {
        return PessoaFisicaCrudActionIT.initEntityAction;
    }

    @Override
    protected String getComponentName() {
        return null;
    }

    protected boolean compareEntityValues(final PessoaFisica entity) {
        final CrudActions<PessoaFisica> crudActions = new CrudActionsImpl<>("");
        return compareValues(crudActions.getEntityValue("cpf"), entity.getCpf())
                && compareValues(crudActions.getEntityValue("dataNascimento"),
                        entity.getDataNascimento())
                && compareValues(crudActions.getEntityValue("nome"),
                        entity.getNome())
                && compareValues(crudActions.getEntityValue("certChain"),
                        entity.getCertChain())
                && compareValues(crudActions.getEntityValue("tipoPessoa"),
                        entity.getTipoPessoa())
                && compareValues(crudActions.getEntityValue("ativo"),
                        entity.getAtivo());
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        this.persistSuccess.runTest(new PessoaFisica("111111116", "",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.persistSuccess.runTest(new PessoaFisica("324789655", "Pessoa",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.persistSuccess.runTest(new PessoaFisica("123332123", "Pessoa",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
    }

    //@Test
    public void persistFailTest() throws Exception {
        this.persistFail.runTest(new PessoaFisica(null, "Pessoa",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new PessoaFisica(fillStr("1", LengthConstants.NUMERO_CPF + 1),
                        "Pessoa", new GregorianCalendar(1955, 11, 9).getTime(),
                        Boolean.TRUE), this.servletContext, this.session);

        this.persistFail.runTest(new PessoaFisica("123.123.131-21", null,
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new PessoaFisica("123.123.131-22", fillStr("pessoa",
                        LengthConstants.NOME_ATRIBUTO + 1),
                        new GregorianCalendar(1955, 11, 9).getTime(),
                        Boolean.TRUE), this.servletContext, this.session);

        this.persistFail.runTest(new PessoaFisica("012.031.234-33", "Pessoa",
                null, Boolean.TRUE), this.servletContext, this.session);
    }

    //@Test
    public void inactivateSuccessTest() throws Exception {
        this.inactivateSuccess.runTest(new PessoaFisica("1111111111", "",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.inactivateSuccess.runTest(new PessoaFisica("32478965x5", "Pessoa",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.inactivateSuccess.runTest(new PessoaFisica("qsdsa12313", "Pessoa",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
    }

    //@Test
    public void updateSuccessTest() throws Exception {
        this.updateSuccess.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("023.123.321-32", "Pessoa",
                        new GregorianCalendar(1955, 11, 9).getTime(),
                        Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", "000.123.321-32");
                crudActions.setEntityValue("nome", "Nova Pessoa");
            }
        }, this.servletContext, this.session);

        this.updateSuccess.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("1jkjkjkj11", "Pessoa", new GregorianCalendar(
                        1955, 11, 9).getTime(), Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", "031.123.321-32");
                crudActions.setEntityValue("nome", "xxxxxPessoa");
            }
        }, this.servletContext, this.session);

        this.updateSuccess.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("1klzdjfbm1", "Pessoa", new GregorianCalendar(
                        1955, 11, 9).getTime(), Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", "578.123.321-32");
                crudActions.setEntityValue("nome", "Novaxxxxxxx");
            }
        }, this.servletContext, this.session);
    }

    //@Test
    public void updateFailTest() throws Exception {
        this.updateFail.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("9123993111", "Pessoa", new GregorianCalendar(
                        1992, 11, 9).getTime(), Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue(
                        "cpf",
                        fillStr("000.123.321-322",
                                LengthConstants.NUMERO_CPF + 1));
            }
        }, this.servletContext, this.session);
        this.updateFail.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("9332asdds1", "Pessoa", new GregorianCalendar(
                        1992, 11, 9).getTime(), Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", null);
            }
        }, this.servletContext, this.session);
        this.updateFail.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("asd1236asw", "Pessoa", new GregorianCalendar(
                        1992, 11, 9).getTime(), Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("nome", null);
            }
        }, this.servletContext, this.session);
        this.updateFail.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("asdq23ds41", "Pessoa", new GregorianCalendar(
                        1992, 11, 9).getTime(), Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("nome",
                        fillStr("Pessoa", LengthConstants.NOME_ATRIBUTO + 1));
            }
        }, this.servletContext, this.session);
        this.updateFail.runTest(new ActionContainer<PessoaFisica>(
                new PessoaFisica("sdd00d1000", "Pessoa", new GregorianCalendar(
                        1992, 11, 9).getTime(), Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("dataNascimento", null);
            }
        }, this.servletContext, this.session);
    }

}
