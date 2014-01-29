package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;

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
    protected void initEntity(Fluxo entity, ICrudActions<Fluxo> crudActions) {
        crudActions.setEntityValue("codFluxo", entity.getCodFluxo());//* validator
        crudActions.setEntityValue("fluxo", entity.getFluxo()); //*
        crudActions.setEntityValue("qtPrazo", entity.getQtPrazo()); //*
        crudActions.setEntityValue("dataInicioPublicacao", entity.getDataInicioPublicacao());//*
        crudActions.setEntityValue("dataFimPublicacao", entity.getDataFimPublicacao());
        crudActions.setEntityValue("publicado", entity.getPublicado());//*
        crudActions.setEntityValue("ativo", entity.getAtivo());//*
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
        for(Date dataFim : datasFim) {
            for(Boolean publicado: allBooleans) {
                for (Boolean ativo:booleans) {
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
        for(Date dataFim : datasFim) {
            for(Boolean publicado : allBooleans) {
                final String codigo = generateName("persistFailFluxo");
                persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 20, dataInicio, dataFim, publicado, null));
            }
        }
    }

    private void persistFailDataFimTest() throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        for(Boolean publicado : allBooleans) {
            for (Boolean ativo : booleans) {
                final String codigo = generateName("persistFailFluxo");
                persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 20, dataInicio, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,-20), publicado, ativo));
            }
        }
    }

    private void persistFailQtPrazoTest() throws Exception {
        final GregorianCalendar currentDate = new GregorianCalendar();
        final Date dataInicio = currentDate.getTime();
        final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR,20)};
        for(Date dataFim : datasFim) {
            for(Boolean publicado : allBooleans) {
                for (Boolean ativo : booleans) {
                    final String codigo = generateName("persistFailFluxo");
                    persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), null, dataInicio, dataFim, publicado, ativo));
                }
            }
        }
    }

    private void persistFailDataInicioTest() throws Exception {
        for(Date dataFim : new Date[]{null, getIncrementedDate(new GregorianCalendar(), GregorianCalendar.DAY_OF_YEAR, 20)}) {
            for(Boolean publicado: allBooleans) {
                for (Boolean ativo : booleans) {
                    final String codigo = generateName("persistFailFluxo");
                    persistFail.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 5, null, dataFim, publicado, ativo));
                }
            }
        }
    }

    private void persistFailFluxoTest() throws Exception {
        for(String fluxo : new String[]{null, "", fillStr("codigo",DESCRICAO_PADRAO+1)}) {
            final GregorianCalendar currentDate = new GregorianCalendar();
            final Date dataInicio = currentDate.getTime();
            final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR, 20)};
            for(Date dataFim : datasFim) {
                for(Boolean publicado: allBooleans) {
                    for (Boolean ativo:booleans) {
                        persistFail.runTest(new Fluxo(generateName("persistFailFluxo"), fluxo, 5, dataInicio, dataFim, publicado, ativo));
                    }
                }
            } 
        }
    }

    private void persistFailCodigoTest() throws Exception {
        for(String codigo : new String[]{null, "", fillStr(generateName("persistFailFluxo"), DESCRICAO_PEQUENA+1)}) {
            final GregorianCalendar currentDate = new GregorianCalendar();
            final Date dataInicio = currentDate.getTime();
            final Date[] datasFim = {null, getIncrementedDate(currentDate,GregorianCalendar.DAY_OF_YEAR, 20)};
            for(Date dataFim : datasFim) {
                for(Boolean publicado: allBooleans) {
                    for (Boolean ativo:booleans) {
                        persistFail.runTest(new Fluxo(codigo,generateName("persistFailFluxo"), 5, dataInicio, dataFim, publicado, ativo));
                    }
                }
            }            
        }
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
        for(Date dataFim : datasFim) {
            for(Boolean publicado: allBooleans) {
                for (Boolean ativo:booleans) {
                    final String codigo = generateName("persistSuccessFluxo");
                    inactivateSuccess.runTest(new Fluxo(codigo,codigo.replace('.', ' '), 5, dataInicio, dataFim, publicado, ativo));
                }
            }
        }
    }
    
}
