package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.junit.Assert.*;
import static br.com.infox.core.action.AbstractAction.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class FluxoCrudActionIT extends AbstractGenericCrudTest<Fluxo> {
    
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

    @Override
    protected String getComponentName() {
        return FluxoCrudAction.NAME;
    }

    @Override
    protected void initEntity(final Fluxo entity, final ICrudActions<Fluxo> crudActions) {
        crudActions.setEntityValue(FIELD_CODIGO, entity.getCodFluxo());//* validator
        crudActions.setEntityValue(FIELD_DESC, entity.getFluxo()); //*
        crudActions.setEntityValue(FIELD_PRAZO, entity.getQtPrazo()); //*
        crudActions.setEntityValue(FIELD_DT_INICIO, entity.getDataInicioPublicacao());//*
        crudActions.setEntityValue(FIELD_DT_FIM, entity.getDataFimPublicacao());
        crudActions.setEntityValue(FIELD_PUBLICADO, entity.getPublicado());//*
        crudActions.setEntityValue(FIELD_ATIVO, entity.getAtivo());//*
    }
    
    private static int id=0;
    
    private String generateName(final String baseString) {
        return format("{0}.{1}", baseString, ++id);
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        for(final Date dataFim : datasFim) {
            for(final Boolean publicado: allBooleans) {
                for (final Boolean ativo:booleans) {
                    final String codigo = generateName("persistSuccessFluxo");
                    persistSuccess.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 5, dataInicio, dataFim, publicado, ativo));
                }
            }
        }
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
    protected boolean compareEntityValues(final Fluxo entity,final ICrudActions<Fluxo> crudActions) {
        final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        final String dataHoje = formato.format(new Date());
        final String dataInicio = formato.format(crudActions.getEntityValue(FIELD_DT_INICIO));
        if (dataHoje.equals(dataInicio)){
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
        final EntityActionContainer<Fluxo> actionContainer = new EntityActionContainer<Fluxo>() {
            @Override
            public void execute(final ICrudActions<Fluxo> crudActions) {
                final Fluxo baseEntity = getEntity();
                final Integer id = crudActions.getId();
                crudActions.resetInstance(id);
                baseEntity.setCodFluxo(updateStringField(crudActions, baseEntity, id, FIELD_CODIGO));
                baseEntity.setFluxo(updateStringField(crudActions, baseEntity, id, FIELD_DESC));
                baseEntity.setQtPrazo(updateIntegerField(crudActions, baseEntity, id, FIELD_PRAZO));
                baseEntity.setDataInicioPublicacao(updateDateField(crudActions, baseEntity, id, FIELD_DT_INICIO, Calendar.DAY_OF_YEAR, -5));
                baseEntity.setDataFimPublicacao(updateDateField(crudActions, baseEntity, id, FIELD_DT_FIM, Calendar.DAY_OF_YEAR, 5));
                baseEntity.setPublicado(updateBooleanField(crudActions, baseEntity, id, FIELD_PUBLICADO));
                baseEntity.setAtivo(updateBooleanField(crudActions, baseEntity, id, FIELD_ATIVO));
            }

            private Boolean updateBooleanField(final ICrudActions<Fluxo> crudActions,
                    final Fluxo baseEntity, final Integer id, final String fieldName) {
                assertTrue("entidade igual", compareEntityValues(baseEntity, crudActions));
                final Boolean newValue = !((Boolean)crudActions.getEntityValue(fieldName));
                crudActions.setEntityValue(fieldName, newValue);
                assertEquals(UPDATED, UPDATED, crudActions.save());
                crudActions.resetInstance(id);
                assertFalse("entidade diferente", compareEntityValues(baseEntity, crudActions));
                final Boolean entityValue = crudActions.getEntityValue(fieldName);
                assertEquals("boolean are equal", entityValue, newValue);
                return newValue;
            }
            
            private Date updateDateField(final ICrudActions<Fluxo> crudActions,
                    final Fluxo baseEntity, final Integer id, final String fieldName,
                    final int dateField, final int increment) {
                assertTrue("entidade igual", compareEntityValues(baseEntity, crudActions));
                final Date entityValue = crudActions.getEntityValue(fieldName);
                final Date newEntityValue = getIncrementedDate(entityValue, dateField, increment);
                crudActions.setEntityValue(fieldName, newEntityValue);
                assertEquals(UPDATED, UPDATED, crudActions.save());
                crudActions.resetInstance(id);
                assertFalse("entidade igual", compareEntityValues(baseEntity, crudActions));
                assertEquals("valor de data correto", crudActions.getEntityValue(fieldName), newEntityValue);
                return newEntityValue;
            }
            
            private Integer updateIntegerField(final ICrudActions<Fluxo> crudActions,
                    final Fluxo baseEntity, final Integer id, final String fieldName) {
                assertTrue("entidade igual", compareEntityValues(baseEntity, crudActions));
                final Integer novoPrazo = ((Integer)crudActions.getEntityValue(fieldName))+5;
                crudActions.setEntityValue(fieldName, novoPrazo);
                assertEquals(UPDATED, UPDATED, crudActions.save());
                crudActions.resetInstance(id);
                assertFalse("entidade diferente", compareEntityValues(baseEntity, crudActions));
                assertEquals("valor de prazo correto", crudActions.getEntityValue(fieldName), novoPrazo);
                return novoPrazo;
            }

            private String updateStringField(final ICrudActions<Fluxo> crudActions,
                    final Fluxo baseEntity, final Integer id,
                    final String fieldName) {
                assertTrue("entidade igual", compareEntityValues(baseEntity, crudActions));
                final String suffix = ".changed";
                crudActions.setEntityValue(fieldName, crudActions.getEntityValue(fieldName)+suffix);
                assertEquals(UPDATED, UPDATED, crudActions.save());
                crudActions.resetInstance(id);
                assertFalse("entidade diferente", compareEntityValues(baseEntity, crudActions));
                final String entityValue = crudActions.getEntityValue(fieldName);
                assertTrue("entityValue ends with "+ suffix, entityValue.endsWith(suffix));
                return entityValue;
            }
        };
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        for(final Date dataFim : datasFim) {
            for(final Boolean publicado: allBooleans) {
                for (final Boolean ativo:booleans) {
                    final String codigo = generateName("updateSuccessFluxo");
                    persistSuccess.runTest(actionContainer,new Fluxo(codigo,codigo.replace('.', ' '), 5, dataInicio, dataFim, publicado, ativo));
                }
            }
        }
    }
    
    @Test
    public void updateFailTest() throws Exception {
        final EntityActionContainer<Fluxo> actionContainer = new EntityActionContainer<Fluxo>() {
            @Override
            public void execute(final ICrudActions<Fluxo> crudActions) {
                // TODO Auto-generated method stub
                
            }
        };
    }
    
}
