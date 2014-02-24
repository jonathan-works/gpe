package br.com.infox.epp.test.it.documento.crud;

import static br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction.NAME;
import static java.text.MessageFormat.format;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction;
import br.com.infox.epp.documento.dao.TipoProcessoDocumentoDAO;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class ClassificacaoDocumentoCrudIT extends AbstractCrudTest<TipoProcessoDocumento> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
                .addClasses(ClassificacaoDocumentoCrudAction.class)
                .addClasses(TipoProcessoDocumentoManager.class)
                .addClass(TipoProcessoDocumentoDAO.class)
                .createDeployment();
    }

    @Override
    protected String getComponentName() {
        return NAME;
    }
    
    private TipoProcessoDocumento createInstance(final String codigoDocumento,
            final String tipoProcessoDocumento,
            final TipoDocumentoEnum tipoDocumento,
            final VisibilidadeEnum visibilidade, final Boolean numera,
            final TipoNumeracaoEnum tipoNumeracao, final Boolean sistema,
            final Boolean publico, final Boolean ativo,
            final String tipoProcessoDocumentoObservacao) {
        final TipoProcessoDocumento instance = new TipoProcessoDocumento();
        instance.setCodigoDocumento(codigoDocumento);
        instance.setTipoProcessoDocumento(tipoProcessoDocumento);
        instance.setInTipoDocumento(tipoDocumento);
        instance.setVisibilidade(visibilidade);
        instance.setNumera(numera);
        instance.setTipoNumeracao(tipoNumeracao);
        instance.setSistema(sistema);
        instance.setPublico(publico);
        instance.setAtivo(ativo);
        instance.setTipoProcessoDocumentoObservacao(tipoProcessoDocumentoObservacao);
        return instance;
    }

    private final ActionContainer<TipoProcessoDocumento> initEntityAction = new ActionContainer<TipoProcessoDocumento>() {
        @Override
        public void execute(CrudActions<TipoProcessoDocumento> crudActions) {
            final TipoProcessoDocumento entity = getEntity();
            crudActions.setEntityValue("codigoDocumento", entity.getCodigoDocumento());
            crudActions.setEntityValue("tipoProcessoDocumento", entity.getTipoProcessoDocumento());
            crudActions.setEntityValue("inTipoDocumento", entity.getInTipoDocumento());
            crudActions.setEntityValue("visibilidade", entity.getVisibilidade());
            crudActions.setEntityValue("numera", entity.getNumera());
            crudActions.setEntityValue("tipoNumeracao", entity.getTipoNumeracao());
            crudActions.setEntityValue("sistema", entity.getSistema());
            crudActions.setEntityValue("publico", entity.getPublico());
            crudActions.setEntityValue("ativo", entity.getAtivo());
            crudActions.setEntityValue("tipoProcessoDocumentoObservacao", entity.getTipoProcessoDocumentoObservacao());
        }
    };
    
    protected ActionContainer<TipoProcessoDocumento> getInitEntityAction() {
        return initEntityAction;
    };
    
    private boolean areEquals(final Object obj1, final Object obj2) {
        return (obj1==obj2) || (obj1 != null && obj1.equals(obj2)) ;
    }
    
    @Override
    protected boolean compareEntityValues(TipoProcessoDocumento entity, CrudActions<TipoProcessoDocumento> crudActions) {
        return areEquals(crudActions.getEntityValue("codigoDocumento"),entity.getCodigoDocumento())
                && areEquals(crudActions.getEntityValue("tipoProcessoDocumento"),entity.getTipoProcessoDocumento())
                && areEquals(crudActions.getEntityValue("inTipoDocumento"),entity.getInTipoDocumento())
                && areEquals(crudActions.getEntityValue("visibilidade"),entity.getVisibilidade())
                && areEquals(crudActions.getEntityValue("numera"),entity.getNumera())
                && areEquals(crudActions.getEntityValue("tipoNumeracao"),entity.getTipoNumeracao())
                && areEquals(crudActions.getEntityValue("sistema"),entity.getSistema())
                && areEquals(crudActions.getEntityValue("publico"),entity.getPublico())
                && areEquals(crudActions.getEntityValue("ativo"),entity.getAtivo())
                && areEquals(crudActions.getEntityValue("tipoProcessoDocumentoObservacao"),entity.getTipoProcessoDocumentoObservacao());
    }

    @Test
    public void updateFailTest() throws Exception {
        TipoProcessoDocumento createdEntity = createInstance("", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        updateFail.runTest(new ActionContainer<TipoProcessoDocumento>(createdEntity) {
            @Override
            public void execute(final CrudActions<TipoProcessoDocumento> crudActions) {
                final Object id = crudActions.getId();
                assert id != null;
                crudActions.newInstance();
                initEntityAction.execute(getEntity(), crudActions);
                crudActions.setEntityValue("idTipoProcessoDocumento", ((int) id) + 1);
            }
        }, servletContext, session);
    }

    @Test
    public void updateSuccessTest() throws Exception {
        TipoProcessoDocumento createdEntity = createInstance("", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        TipoProcessoDocumento processoDocumento = persistSuccess.runTest(createdEntity, servletContext, session);
        System.out.println(format("ENTITY = {0}", processoDocumento.getIdTipoProcessoDocumento()));
//        final CrudActions<TipoProcessoDocumento> crudActions = getCrudActions();
//        updateSuccess.runTest(new EntityActionContainer<TipoProcessoDocumento>(createdEntity) {
//            @Override
//            public void execute() {
//                crudActions.setEntityValue("codigoDocumento", fillStr("updateCodigoDocumento", 25));                
//            }
//        });
    }

    @Test
    public void persistSuccessTest() throws Exception {
        persistSuccess.runTest(createInstance(fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)), servletContext, session);
        persistSuccess.runTest(createInstance(fillStr("codigoDocumento", 12), fillStr("descricao", 0), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)), servletContext, session);

        persistSuccess.runTest(createInstance(fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)), servletContext, session);
        persistSuccess.runTest(createInstance(fillStr("codigoDocumento", 0), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)), servletContext, session);
        for (int i = 0; i < 25; i++) {
            persistSuccess.runTest(createInstance(i + "", i + "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i
                    + ""), servletContext, session);
        }
    }

    @Test
    public void persistFailTest() throws Exception {
        persistFail.runTest(createInstance(fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO + 1), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)), servletContext, session);
        persistFail.runTest(createInstance(fillStr("codigoDocumento", 12), null, TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)), servletContext, session);

        persistFail.runTest(createInstance(fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO + 1), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)), servletContext, session);
    }

    @Test
    public void inactivateSuccessTest() throws Exception {
        for (int i = 0; i < 25; i++) {
            inactivateSuccess.runTest(createInstance(i + "", i + "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+ ""), servletContext, session);
        }
    }

}