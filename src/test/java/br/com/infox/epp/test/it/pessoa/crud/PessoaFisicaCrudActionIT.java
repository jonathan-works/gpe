package br.com.infox.epp.test.it.pessoa.crud;

import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.pessoa.crud.PessoaFisicaCrudAction;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.validator.CpfValidator;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class PessoaFisicaCrudActionIT extends AbstractCrudTest<PessoaFisica> {
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(PessoaFisicaCrudAction.class, CpfValidator.class, PessoaFisicaDAO.class, PessoaFisicaManager.class)
        .createDeployment();
    }
    
    @Override
    protected void initEntity(final PessoaFisica entity,final CrudActions<PessoaFisica> crudActions) {
        crudActions.setEntityValue("cpf", entity.getCpf());
        crudActions.setEntityValue("nome", entity.getNome());
        if (entity.getDataNascimento() != null) {
            crudActions.setEntityValue("dataNascimento", new Date(entity.getDataNascimento().getTime()));
        } else {
            crudActions.setEntityValue("dataNascimento", null);
        }
        crudActions.setEntityValue("ativo", entity.getAtivo());
//        id="cpf"
//        required="true" />
//        id="nome"
//        required="true" />  
//        id="dataNascimento"
//        required="true" />
    }
    
    @Override
    protected String getComponentName() {
        return PessoaFisicaCrudAction.NAME;
    }
    
    protected boolean compareEntityValues(final PessoaFisica entity) {
        final CrudActions<PessoaFisica> crudActions = new CrudActionsImpl<>("");
        return compareValues(crudActions.getEntityValue("cpf"), entity.getCpf()) &&
                compareValues(crudActions.getEntityValue("dataNascimento"), entity.getDataNascimento()) &&
                compareValues(crudActions.getEntityValue("nome"), entity.getNome()) &&
                compareValues(crudActions.getEntityValue("certChain"), entity.getCertChain()) &&
                compareValues(crudActions.getEntityValue("tipoPessoa"), entity.getTipoPessoa()) &&
                compareValues(crudActions.getEntityValue("ativo"), entity.getAtivo());
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        persistSuccess.runTest(new PessoaFisica("111111116","",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        persistSuccess.runTest(new PessoaFisica("324789655","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        persistSuccess.runTest(new PessoaFisica("123332123","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
    }

    @Test
    public void persistFailTest() throws Exception {
        persistFail.runTest(new PessoaFisica(null,"Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        persistFail.runTest(new PessoaFisica(fillStr("1", LengthConstants.NUMERO_CPF+1),"Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        
        persistFail.runTest(new PessoaFisica("123.123.131-21",null,new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        persistFail.runTest(new PessoaFisica("123.123.131-22",fillStr("pessoa",LengthConstants.NOME_ATRIBUTO+1),new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        
        persistFail.runTest(new PessoaFisica("012.031.234-33","Pessoa",null,Boolean.TRUE));
    }
    
    @Test
    public void inactivateSuccessTest() throws Exception {
        inactivateSuccess.runTest(new PessoaFisica("1111111111","",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        inactivateSuccess.runTest(new PessoaFisica("32478965x5","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        inactivateSuccess.runTest(new PessoaFisica("qsdsa12313","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
    }
    
    @Test
    public void updateSuccessTest() throws Exception {
        updateSuccess.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("023.123.321-32","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", "000.123.321-32");
                crudActions.setEntityValue("nome", "Nova Pessoa");
            }
        });
        
        updateSuccess.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("1jkjkjkj11","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", "031.123.321-32");
                crudActions.setEntityValue("nome", "xxxxxPessoa");
            }
        });
        
        updateSuccess.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("1klzdjfbm1","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", "578.123.321-32");
                crudActions.setEntityValue("nome", "Novaxxxxxxx");
            }
        });
    }
    
    @Test
    public void updateFailTest() throws Exception {
        updateFail.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("9123993111","Pessoa",new GregorianCalendar(1992,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", fillStr("000.123.321-322",LengthConstants.NUMERO_CPF+1));
            }
        });
        updateFail.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("9332asdds1","Pessoa",new GregorianCalendar(1992,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("cpf", null);
            }
        });
        updateFail.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("asd1236asw","Pessoa",new GregorianCalendar(1992,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("nome", null);
            }
        });
        updateFail.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("asdq23ds41","Pessoa",new GregorianCalendar(1992,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("nome", fillStr("Pessoa",LengthConstants.NOME_ATRIBUTO+1));
            }
        });
        updateFail.runTest(new ActionContainer<PessoaFisica>(new PessoaFisica("sdd00d1000","Pessoa",new GregorianCalendar(1992,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<PessoaFisica> crudActions) {
                crudActions.setEntityValue("dataNascimento", null);
            }
        });
    }
    
}
