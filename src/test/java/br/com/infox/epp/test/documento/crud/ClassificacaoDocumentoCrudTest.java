package br.com.infox.epp.test.documento.crud;

import static br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction.NAME;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
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

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
                .addPackages("br.com.infox.core", "br.com.itx")
                .addClasses(ClassificacaoDocumentoCrudAction.class)
                .setArchiveName("epp-test.war")
                .setMockWebXMLPath("src/test/resources/mock-web.xml")
                .setMockComponentsXMLPath("src/test/resources/mock-components.xml")
                .setMockPersistenceXMLPath("src/test/resources/mock-persistence.xml")
                .setPomPath("pom.xml")
                .createDeployment();
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

    protected void setPersistData(final TipoProcessoDocumento entity) {
        setValue(NAME, "codigoDocumento", entity.getCodigoDocumento());
        setValue(NAME, "tipoProcessoDocumento", entity.getTipoProcessoDocumento());
        setValue(NAME, "inTipoDocumento", entity.getInTipoDocumento());
        setValue(NAME, "visibilidade", entity.getVisibilidade());
        setValue(NAME, "numera", entity.getNumera());
        setValue(NAME, "tipoNumeracao", entity.getTipoNumeracao());
        setValue(NAME, "sistema", entity.getSistema());
        setValue(NAME, "publico", entity.getPublico());
        setValue(NAME, "ativo", entity.getAtivo());
        setValue(NAME, "tipoProcessoDocumentoObservacao", entity.getTipoProcessoDocumentoObservacao());
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
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(NAME, "save"));
                Object id = getValue("#{" + NAME + ".id}");
                assert id != null;
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                setValue(NAME, "idTipoProcessoDocumento", ((int) id) + 1);
                assert !AbstractAction.UPDATED.equals(invokeMethod(NAME, "save"));
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
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(NAME, "save"));
                Object id = getValue("#{" + NAME + ".id}");
                assert id != null;
                invokeMethod(NAME, "newInstance");
                setValue("#{" + NAME + ".id}", id);
                setValue(NAME, "codigoDocumento", fillStr("updateCodigoDocumento", 25));
                assert AbstractAction.UPDATED.equals(invokeMethod(NAME, "save"));
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
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(NAME, "save"));
                assert getValue("#{" + NAME + ".id}") != null;
                assert AbstractAction.REMOVED.equals(invokeMethod(NAME, "remove"));
            }
        };
    }

    @Override
    protected Runnable getRemoveFailTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                setValue(NAME, "tipoProcessoDocumentoObservacao", entity.getTipoProcessoDocumentoObservacao());
                assert getValue("#{" + NAME + ".id}") == null;
                assert AbstractAction.PERSISTED.equals(invokeMethod(NAME, "save"));
                assert AbstractAction.REMOVED.equals(invokeMethod(NAME, "remove"));
                assert !AbstractAction.REMOVED.equals(invokeMethod(NAME, "remove"));
            }
        };
    }

    @Override
    protected Runnable getPersistSuccessTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                assert AbstractAction.PERSISTED.equals(invokeMethod(NAME, "save"));
                assert getValue("#{" + NAME + ".id}") != null;
            }
        };
    }

    @Override
    protected Runnable getPersistFailTest(final TipoProcessoDocumento entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                assert !AbstractAction.PERSISTED.equals(invokeMethod(NAME, "save"));
                assert getValue("#{" + NAME + ".id}") == null;
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
                invokeMethod(NAME, "newInstance");
                setPersistData(entity);
                
                assert AbstractAction.PERSISTED.equals(invokeMethod(NAME, "save"));
                assert getValue("#{" + NAME + ".id}") != null;
                final Object value = getValue("#{" + NAME + ".instance}");
                assert value != null;
                assert AbstractAction.UPDATED.equals(invokeMethod("#{"+NAME+".inactive("+NAME+".instance)}"));
            }
        };
    }

    @Override
    protected Runnable getInactivateFailTest(final TipoProcessoDocumento entity) {
        return new Runnable() { @Override public void run() {} };
    }

}
