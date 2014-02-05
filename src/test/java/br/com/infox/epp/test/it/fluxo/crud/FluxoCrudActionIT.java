package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class FluxoCrudActionIT extends AbstractCrudTest<Fluxo> {
    
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
        .addClasses(FluxoCrudAction.class, FluxoManager.class, FluxoDAO.class)
        .createDeployment();
    }

    private static final ActionContainer<Fluxo> initEntityAction = new ActionContainer<Fluxo>() {
        @Override
        public void execute(final CrudActions<Fluxo> crudActions) {
            final Fluxo entity = getEntity();
            crudActions.setEntityValue("codFluxo", entity.getCodFluxo());//* validator
            crudActions.setEntityValue("fluxo", entity.getFluxo()); //*
            crudActions.setEntityValue("qtPrazo", entity.getQtPrazo()); //*
            crudActions.setEntityValue("dataInicioPublicacao", entity.getDataInicioPublicacao());//*
            crudActions.setEntityValue("dataFimPublicacao", entity.getDataFimPublicacao());
            crudActions.setEntityValue("publicado", entity.getPublicado());//*
            crudActions.setEntityValue("ativo", entity.getAtivo());//*
        }
    };
    
    public static ActionContainer<Fluxo> getInitEntityAction() {
        return initEntityAction;
    }
    
    public static List<Fluxo> getSuccessfullyPersisted(final ActionContainer<Fluxo> action, final String suffix, ServletContext servletContext, HttpSession session) throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        currentDate.add(GregorianCalendar.DAY_OF_YEAR, 20);
        final Date[] datasFim = { null, currentDate.getTime() };
        int id = 0;
        final List<Fluxo> fluxos = new ArrayList<Fluxo>();
        final ActionContainer<Fluxo> initEntityAction = FluxoCrudActionIT.getInitEntityAction();
        final PersistSuccessTest<Fluxo> persistSuccessTest = new PersistSuccessTest<Fluxo>(FluxoCrudAction.NAME, initEntityAction);
        for (final Date dataFim : datasFim) {
            for (final Boolean publicado : allBooleans) {
                for (final Boolean ativo : booleans) {
                    final String nome = format("Fluxo {0} {1}", suffix, ++id);
                    final Fluxo prePersisted = new Fluxo(nome.replace(' ', '.'), nome, 5, dataInicio, dataFim, publicado, ativo);
                    final Fluxo afterPersisted = persistSuccessTest.runTest(prePersisted, servletContext, session);
                    fluxos.add(afterPersisted);
                }
            }
        }
        return fluxos;
    }
    
    @Override
    protected String getComponentName() {
        return FluxoCrudAction.NAME;
    }

    @Override
    protected void initEntity(final Fluxo entity, final CrudActions<Fluxo> crudActions) {
        final ActionContainer<Fluxo> initEntityAction = getInitEntityAction();
        initEntityAction.setEntity(entity);
        initEntityAction.execute(crudActions);
    }
    
    private static int id=0;
    
    private String generateName(final String baseString) {
        return format("{0}.{1}", baseString, ++id);
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        getSuccessfullyPersisted(null, "persist-success", servletContext, session);
    }
    
    @Test
    public void persistFailTest() throws Exception {
        persistFailCodigoTest();
        persistFailFluxoTest();
        persistFailQtPrazoTest();
        persistFailDataInicioTest();
        persistFailDataFimTest();
        persistFailAtivoTest();
    }
    
    private void persistFailAtivoTest() throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        for(final Date dataFim : datasFim) {
            for(final Boolean publicado : allBooleans) {
                final String codigo = generateName("persistFailFluxo");
                persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 20, dataInicio, dataFim, publicado, null));
            }
        }
    }

    private void persistFailDataFimTest() throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        for(final Boolean publicado : allBooleans) {
            for (final Boolean ativo : booleans) {
                final String codigo = generateName("persistFailFluxo");
                persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 20, dataInicio, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,-20), publicado, ativo));
            }
        }
    }

    private void persistFailQtPrazoTest() throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        for(final Date dataFim : datasFim) {
            for(final Boolean publicado : allBooleans) {
                for (final Boolean ativo : booleans) {
                    final String codigo = generateName("persistFailFluxo");
                    persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), null, dataInicio, dataFim, publicado, ativo));
                }
            }
        }
    }

    private void persistFailDataInicioTest() throws Exception {
        for(final Date dataFim : new Date[]{null, getIncrementedDate(new GregorianCalendar(), GregorianCalendar.DAY_OF_YEAR, 20)}) {
            for(final Boolean publicado: allBooleans) {
                for (final Boolean ativo : booleans) {
                    final String codigo = generateName("persistFailFluxo");
                    persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 5, null, dataFim, publicado, ativo));
                }
            }
        }
    }

    private void persistFailFluxoTest() throws Exception {
        for(final String fluxo : new String[]{null, "", fillStr("codigo",DESCRICAO_PADRAO+1)}) {
            final GregorianCalendar currentDate = new GregorianCalendar();
            final Date dataInicio = currentDate.getTime();
            final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR, 20)};
            for(final Date dataFim : datasFim) {
                for(final Boolean publicado: allBooleans) {
                    for (final Boolean ativo:booleans) {
                        persistFail.runTest(new Fluxo(generateName("persistFailFluxo"), fluxo, 5, dataInicio, dataFim, publicado, ativo));
                    }
                }
            } 
        }
    }

    private void persistFailCodigoTest() throws Exception {
        for(final String codigo : new String[]{null, "", fillStr(generateName("persistFailFluxo"), DESCRICAO_PEQUENA+1)}) {
            final GregorianCalendar currentDate = new GregorianCalendar();
            final Date dataInicio = currentDate.getTime();
            final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR, 20)};
            for(final Date dataFim : datasFim) {
                for(final Boolean publicado: allBooleans) {
                    for (final Boolean ativo:booleans) {
                        persistFail.runTest(new Fluxo(codigo,generateName("persistFailFluxo"), 5, dataInicio, dataFim, publicado, ativo));
                    }
                }
            }            
        }
    }

    private Date getIncrementedDate(final Date currentDate, final int field, final int ammount) {
        final GregorianCalendar calendar = new GregorianCalendar();
        if (currentDate == null) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(currentDate);
        }
        return getIncrementedDate(calendar, field, ammount);
    }
    
    private Date getIncrementedDate(final GregorianCalendar currentDate, final int field, final int ammount) {
        currentDate.add(field, ammount);
        final Date dataFim = currentDate.getTime();
        return dataFim;
    }
    
    @Test
    public void inactivateSuccessTest() throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        for(final Date dataFim : datasFim) {
            for(final Boolean publicado: allBooleans) {
                for (final Boolean ativo:booleans) {
                    final String codigo = generateName("persistSuccessFluxo");
                    inactivateSuccess.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 5, dataInicio, dataFim, publicado, ativo));
                }
            }
        }
    }
    
    /*
     * TODO: TODO: construir teste de falha para inativação de fluxo
     * após construir testes envolvendo naturezacategoriafluxo
     * e inicialização de processos
     */
    public void inactivateFailTest() throws Exception {}
    
    private boolean compareObjects(final Object obj1, final Object obj2) {
        return (obj1 == obj2) || ((obj1!=null) && obj1.equals(obj2));
    }
    
    @Override
    protected boolean compareEntityValues(final Fluxo entity,final CrudActions<Fluxo> crudActions) {
        final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        final String dataHoje = formato.format(new Date());
        final Date dataInicio = crudActions.getEntityValue(FIELD_DT_INICIO);
        if (dataInicio != null && dataHoje.equals(formato.format(dataInicio))){
            entity.setPublicado(TRUE);
        }
        
        return compareObjects(entity.getCodFluxo(), crudActions.getEntityValue(FIELD_CODIGO))
                && compareObjects(entity.getFluxo(), crudActions.getEntityValue(FIELD_DESC))
                && compareObjects(entity.getQtPrazo(), crudActions.getEntityValue(FIELD_PRAZO))
                && compareObjects(entity.getDataInicioPublicacao(), crudActions.getEntityValue(FIELD_DT_INICIO))
                && compareObjects(entity.getDataFimPublicacao(), crudActions.getEntityValue(FIELD_DT_FIM))
                && compareObjects(entity.getPublicado(), crudActions.getEntityValue(FIELD_PUBLICADO))
                && compareObjects(entity.getAtivo(), crudActions.getEntityValue(FIELD_ATIVO));
    }
    
    @Test
    public void updateSuccessTest() throws Exception {
        final ActionContainer<Fluxo> actionContainer = new ActionContainer<Fluxo>() {
            @Override
            public void execute(final CrudActions<Fluxo> crudActions) {
                final Fluxo entity = getEntity();
                final Integer id = crudActions.getId();
                crudActions.resetInstance(id);
                entity.setCodFluxo(updateField(crudActions, entity, id, FIELD_CODIGO, entity.getCodFluxo()+".changed"));
                entity.setFluxo(updateField(crudActions, entity, id, FIELD_DESC, entity.getFluxo()+".changed"));
                entity.setQtPrazo(updateField(crudActions, entity, id, FIELD_PRAZO, entity.getQtPrazo()+5));
                entity.setDataInicioPublicacao(updateField(crudActions, entity, id, FIELD_DT_INICIO, getIncrementedDate(entity.getDataInicioPublicacao(), Calendar.DAY_OF_YEAR, -5)));
                entity.setDataFimPublicacao(updateField(crudActions, entity, id, FIELD_DT_FIM, getIncrementedDate(entity.getDataFimPublicacao(), Calendar.DAY_OF_YEAR, 5)));
                entity.setPublicado(updateField(crudActions, entity, id, FIELD_PUBLICADO, !entity.getPublicado()));
                entity.setPublicado(updateField(crudActions, entity, id, FIELD_PUBLICADO, !entity.getPublicado()));
                entity.setAtivo(updateField(crudActions, entity, id, FIELD_ATIVO, !entity.getAtivo()));
                entity.setAtivo(updateField(crudActions, entity, id, FIELD_ATIVO, !entity.getAtivo()));
            }
            
            private <F> F updateField(final CrudActions<Fluxo> crudActions, final Fluxo baseEntity, final Integer id, final String fieldName, final F newValue) {
                assertTrue("entidade igual", compareEntityValues(baseEntity, crudActions));
                crudActions.setEntityValue(fieldName, newValue);
                assertEquals(UPDATED, UPDATED, crudActions.save());
                crudActions.resetInstance(id);
                assertFalse("entidade diferente", compareEntityValues(baseEntity, crudActions));
                final F entityValue = crudActions.getEntityValue(fieldName);
                assertEquals("aren't equal", newValue, entityValue);
                return entityValue;
            }
        };
        
        executeUpdate(actionContainer, persistSuccess, "updateSuccessFluxo");
    }
    
    private void executeUpdate(final ActionContainer<Fluxo> actionContainer,
            final InternalRunnableTest<Fluxo> runnable, final String defaultCodigo) throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        for(final Date dataFim : datasFim) {
            for(final Boolean publicado: allBooleans) {
                for (final Boolean ativo:booleans) {
                    final String codigo = generateName(defaultCodigo);
                    runnable.runTest(actionContainer,new Fluxo(codigo,codigo.replace('.', ' '), 5, dataInicio, dataFim, publicado, ativo));
                }
            }
        }
    }
    
    @Test
    public void updateFailTest() throws Exception {
        final ActionContainer<Fluxo> actionContainer = new ActionContainer<Fluxo>() {
            @Override
            public void execute(final CrudActions<Fluxo> crudActions) {
                final Fluxo baseEntity = getEntity();
                final Integer id = crudActions.getId();
                crudActions.resetInstance(id);
                
                for (final String codigo : new String[]{null, "", fillStr(generateName("persistFailFluxo"), DESCRICAO_PEQUENA+1)}) {
                    updateField(crudActions, baseEntity, id, FIELD_CODIGO, codigo);                    
                }
                for (final String fluxo : new String[]{null, "", fillStr("codigo",DESCRICAO_PADRAO+1)}) {
                    updateField(crudActions, baseEntity, id, FIELD_CODIGO, fluxo);
                }
                updateField(crudActions, baseEntity, id, FIELD_PRAZO, null);
                updateField(crudActions, baseEntity, id, FIELD_DT_INICIO, null);
                updateField(crudActions, baseEntity, id, FIELD_DT_FIM, getIncrementedDate(baseEntity.getDataInicioPublicacao(), Calendar.DAY_OF_YEAR, -1));
                updateField(crudActions, baseEntity, id, FIELD_ATIVO, null);
            }
            
            private <F> F updateField(final CrudActions<Fluxo> crudActions, final Fluxo baseEntity, final Integer id, final String fieldName, final F newValue) {
                assertTrue("entidade não é igual", compareEntityValues(baseEntity, crudActions));
                crudActions.setEntityValue(fieldName, newValue);
                assertFalse(UPDATED, UPDATED.equals(crudActions.save()));
                crudActions.resetInstance(id);
                assertTrue("entidade não é igual", compareEntityValues(baseEntity, crudActions));
                final F entityValue = crudActions.getEntityValue(fieldName);
                
                assertFalse("valores não são iguais", compareObjects(newValue, entityValue));
                return entityValue;
            }
        };
        
        executeUpdate(actionContainer, persistSuccess, "updateFailFluxo");
    }
    
}
