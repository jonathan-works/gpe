package br.com.infox.epp.test.pessoa.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.UPDATED;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.pessoa.crud.PessoaFisicaCrudAction;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.validator.CpfValidator;
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class PessoaFisicaCrudActionIT extends AbstractGenericCrudTest<PessoaFisica> {
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(PessoaFisicaCrudAction.class, CpfValidator.class)
        .createDeployment();
    }
    
    @Override
    protected void initEntity(final PessoaFisica entity) {
        final CrudActions<PessoaFisica> crudActions = getCrudActions();
        crudActions.setEntityValue("cpf", entity.getCpf());
        crudActions.setEntityValue("nome", entity.getNome());
        crudActions.setEntityValue("dataNascimento", entity.getDataNascimento());
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
    
    @Override
    protected List<PessoaFisica> getPersistSuccessList() {
        final ArrayList<PessoaFisica> list = new ArrayList<>();
        list.add(new PessoaFisica("111111111","",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        list.add(new PessoaFisica("324789655","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        list.add(new PessoaFisica("123332123","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        return list;
    }
    
    @Override
    protected List<PessoaFisica> getPersistFailList() {
        final ArrayList<PessoaFisica> list = new ArrayList<>();
        list.add(new PessoaFisica(null,"Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        list.add(new PessoaFisica(fillStr("1", LengthConstants.NUMERO_CPF+1),"Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        
        list.add(new PessoaFisica("123.123.131-21",null,new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        list.add(new PessoaFisica("123.123.131-22",fillStr("pessoa",LengthConstants.NOME_ATRIBUTO+1),new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        
        list.add(new PessoaFisica("012.031.234-33","Pessoa",null,Boolean.TRUE));
        return list;
    }
    
    @Override
    protected List<PessoaFisica> getInactivateSuccessList() {
        final ArrayList<PessoaFisica> list = new ArrayList<>();
        list.add(new PessoaFisica("1111111111","",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        list.add(new PessoaFisica("32478965x5","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        list.add(new PessoaFisica("qsdsa12313","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        return list;
    }
    
    @Override
    protected List<EntityActionContainer<PessoaFisica>> getUpdateSuccessList() {
        final ArrayList<EntityActionContainer<PessoaFisica>> list = new ArrayList<>();
        final CrudActions<PessoaFisica> crudActions = getCrudActions();
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("1jksdlsk11","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("cpf", "000.123.321-32");
                crudActions.setEntityValue("nome", "Nova Pessoa");
            }
        });
        
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("1jkjkjkj11","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("cpf", "031.123.321-32");
                crudActions.setEntityValue("nome", "xxxxxPessoa");
            }
        });
        
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("1klzdjfbm1","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("cpf", "578.123.321-32");
                crudActions.setEntityValue("nome", "Novaxxxxxxx");
            }
        });
        return list;
    }
    
    @Override
    protected List<EntityActionContainer<PessoaFisica>> getUpdateFailList() {
        final ArrayList<EntityActionContainer<PessoaFisica>> list = new ArrayList<>();
        final CrudActions<PessoaFisica> crudActions = getCrudActions();
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("9123993111","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("cpf", fillStr("000.123.321-322",LengthConstants.NUMERO_CPF+1));
            }
        });
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("9332asdds1","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("cpf", null);
            }
        });
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("asd1236asw","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("nome", null);
            }
        });
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("asdq23ds41","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("nome", fillStr("Pessoa",LengthConstants.NOME_ATRIBUTO+1));
            }
        });
        list.add(new EntityActionContainer<PessoaFisica>(new PessoaFisica("sdd00d1000","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE)) {
            @Override
            public void execute() {
                crudActions.setEntityValue("dataNascimento", null);
            }
        });
        
        return list;
    }

    protected void updateFailTest(final EntityActionContainer<PessoaFisica> entityActionContainer) {
        final CrudActions<PessoaFisica> crudActions = getCrudActions();
        crudActions.newInstance();
        PessoaFisica entity = entityActionContainer.getEntity();
        
        initEntity(entity);
        assert PERSISTED.equals(crudActions.save());
        final Integer id = crudActions.getId();
        assert id != null;
        entityActionContainer.execute();
        
        boolean assertion = !UPDATED.equals(crudActions.save());
        if (!assertion) {
            System.out.println("==FLAG "+entity.getCpf());    
        }
        assert assertion;
        crudActions.newInstance();
        crudActions.setId(id);
        assert compareEntityValues(entity);
    };
    
}
