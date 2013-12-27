package br.com.infox.epp.test.documento.crud;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class ClassificacaoDocumentoCrudTest extends AbstractGenericCrudTest<TipoProcessoDocumento> {
    private static final String OVER_PROTOCOL = "Servlet 3.0";
    private final String COMPONENT_NAME = ClassificacaoDocumentoCrudAction.NAME;

    @Deployment
    @OverProtocol(OVER_PROTOCOL)
    public static WebArchive createDeployment() {
        final String[] importPackages = { "br.com.infox.core", "br.com.itx" };
        final Class<?>[] classesToImport = { ClassificacaoDocumentoCrudAction.class };
        final String archiveName = "epp-test.war";
        final String mockWebXMLPath = "src/test/resources/mock-web.xml";
        final String mockComponentsXMLPath = "src/test/resources/mock-components.xml";
        final String mockPersistenceXMLPath = "src/test/resources/mock-persistence.xml";
        final String pomPath = "pom.xml";
        final ArquillianSeamTestSetup arquillianTest = new ArquillianSeamTestSetup().addPackages(importPackages).addClasses(classesToImport).setArchiveName(archiveName).setMockWebXMLPath(mockWebXMLPath).setMockComponentsXMLPath(mockComponentsXMLPath).setMockPersistenceXMLPath(mockPersistenceXMLPath).setPomPath(pomPath);
        final WebArchive deployment = arquillianTest.createDeployment();
        deployment.writeTo(System.out, Formatters.VERBOSE);
        return deployment;
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

    private void setEntityToComponent(final TipoProcessoDocumento entity) {
        setValue(COMPONENT_NAME, "codigoDocumento", entity.getCodigoDocumento());
        setValue(COMPONENT_NAME, "tipoProcessoDocumento", entity.getTipoProcessoDocumento());
        setValue(COMPONENT_NAME, "inTipoDocumento", entity.getInTipoDocumento());
        setValue(COMPONENT_NAME, "visibilidade", entity.getVisibilidade());
        setValue(COMPONENT_NAME, "numera", entity.getNumera());
        setValue(COMPONENT_NAME, "tipoNumeracao", entity.getTipoNumeracao());
        setValue(COMPONENT_NAME, "sistema", entity.getSistema());
        setValue(COMPONENT_NAME, "publico", entity.getPublico());
        setValue(COMPONENT_NAME, "ativo", entity.getAtivo());
        setValue(COMPONENT_NAME, "tipoProcessoDocumentoObservacao", entity.getTipoProcessoDocumentoObservacao());
    }

    @Override
    protected ArrayList<TipoProcessoDocumento> getUpdateFailList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();
        final TipoProcessoDocumento entity = createInstance("", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        list.add(entity);
        return list;
    }

    @Override
    protected Runnable getUpdateFailTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(COMPONENT_NAME, "save"));
                Object id = getValue("#{" + COMPONENT_NAME + ".id}");
                assert id != null;
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                setValue(COMPONENT_NAME, "idTipoProcessoDocumento", ((int) id) + 1);
                assert !AbstractAction.UPDATED.equals(invokeMethod(COMPONENT_NAME, "save"));
            }
        };
    }

    @Override
    protected ArrayList<TipoProcessoDocumento> getUpdateSuccessList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();
        final TipoProcessoDocumento entity = createInstance("", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        list.add(entity);
        return list;
    }

    @Override
    protected Runnable getUpdateSuccessTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(COMPONENT_NAME, "save"));
                Object id = getValue("#{" + COMPONENT_NAME + ".id}");
                assert id != null;
                invokeMethod(COMPONENT_NAME, "newInstance");
                setValue("#{" + COMPONENT_NAME + ".id}", id);
                setValue(COMPONENT_NAME, "codigoDocumento", fillStr("updateCodigoDocumento", 25));
                assert AbstractAction.UPDATED.equals(invokeMethod(COMPONENT_NAME, "save"));
            }
        };
    }

    @Override
    protected ArrayList<TipoProcessoDocumento> getRemoveSuccessList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(createInstance(i + "", i + "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i
                    + ""));
        }
        return list;
    }

    @Override
    protected Runnable getRemoveSuccessTest(final TipoProcessoDocumento entity) {
        return new Runnable() {

            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(COMPONENT_NAME, "save"));
                assert getValue("#{" + COMPONENT_NAME + ".id}") != null;
                assert AbstractAction.REMOVED.equals(invokeMethod(COMPONENT_NAME, "remove"));
            }
        };
    }

    @Override
    protected Runnable getRemoveFailTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                setValue(COMPONENT_NAME, "tipoProcessoDocumentoObservacao", entity.getTipoProcessoDocumentoObservacao());
                assert getValue("#{" + COMPONENT_NAME + ".id}") == null;
                assert AbstractAction.PERSISTED.equals(invokeMethod(COMPONENT_NAME, "save"));
                assert AbstractAction.REMOVED.equals(invokeMethod(COMPONENT_NAME, "remove"));
                assert !AbstractAction.REMOVED.equals(invokeMethod(COMPONENT_NAME, "remove"));
            }
        };
    }

    @Override
    protected Runnable getPersistSuccessTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(COMPONENT_NAME, "save"));
                assert getValue("#{" + COMPONENT_NAME + ".id}") != null;
            }
        };
    }

    @Override
    protected Runnable getPersistFailTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                assert !AbstractAction.PERSISTED.equals(invokeMethod(COMPONENT_NAME, "save"));
                assert getValue("#{" + COMPONENT_NAME + ".id}") == null;
            }
        };
    }

    @Override
    protected ArrayList<TipoProcessoDocumento> getPersistSuccessList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();
        list.add(createInstance(fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        list.add(createInstance(fillStr("codigoDocumento", 12), fillStr("descricao", 0), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));

        list.add(createInstance(fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        list.add(createInstance(fillStr("codigoDocumento", 0), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        for (int i = 0; i < 25; i++) {
            list.add(createInstance(i + "", i + "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i
                    + ""));
        }
        return list;
    }

    @Override
    protected ArrayList<TipoProcessoDocumento> getPersistFailList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();

        list.add(createInstance(fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO + 1), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        list.add(createInstance(fillStr("codigoDocumento", 12), null, TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));

        list.add(createInstance(fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO + 1), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        return list;
    }

    @Override
    protected List<TipoProcessoDocumento> getInactivateSuccessList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(createInstance(i + "", i + "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+ ""));
        }
        return list;
    }

    @Override
    protected List<TipoProcessoDocumento> getInactivateFailList() {
        return new ArrayList<TipoProcessoDocumento>(0);
    }

    @Override
    protected List<TipoProcessoDocumento> getRemoveFailList() {
        return new ArrayList<TipoProcessoDocumento>(0);
    }

    @Override
    protected Runnable getInactivateSuccessTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setEntityToComponent(entity);
                
                assert AbstractAction.PERSISTED.equals(invokeMethod(COMPONENT_NAME, "save"));
                assert getValue("#{" + COMPONENT_NAME + ".id}") != null;
                final Object value = getValue("#{" + COMPONENT_NAME + ".instance}");
                assert value != null;
                assert AbstractAction.UPDATED.equals(invokeMethod("#{"+COMPONENT_NAME+".inactive("+COMPONENT_NAME+".instance)}"));
            }
        };
    }

    @Override
    protected Runnable getInactivateFailTest(final TipoProcessoDocumento entity) {
        return new Runnable() { @Override public void run() {} };
    }

}
