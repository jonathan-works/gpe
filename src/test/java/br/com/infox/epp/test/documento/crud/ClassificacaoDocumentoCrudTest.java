package br.com.infox.epp.test.documento.crud;

import static br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction.NAME;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

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

    protected void initEntity(final TipoProcessoDocumento entity) {
        setEntityValue("codigoDocumento", entity.getCodigoDocumento());
        setEntityValue("tipoProcessoDocumento", entity.getTipoProcessoDocumento());
        setEntityValue("inTipoDocumento", entity.getInTipoDocumento());
        setEntityValue("visibilidade", entity.getVisibilidade());
        setEntityValue("numera", entity.getNumera());
        setEntityValue("tipoNumeracao", entity.getTipoNumeracao());
        setEntityValue("sistema", entity.getSistema());
        setEntityValue("publico", entity.getPublico());
        setEntityValue("ativo", entity.getAtivo());
        setEntityValue("tipoProcessoDocumentoObservacao", entity.getTipoProcessoDocumentoObservacao());
    }
    
    private boolean areEquals(final Object obj1, final Object obj2) {
        return (obj1==obj2) || (obj1 != null && obj1.equals(obj2)) ;
    }
    
    @Override
    protected boolean compareEntityValues(TipoProcessoDocumento entity) {
        return areEquals(getEntityValue("codigoDocumento"),entity.getCodigoDocumento())
                && areEquals(getEntityValue("tipoProcessoDocumento"),entity.getTipoProcessoDocumento())
                && areEquals(getEntityValue("inTipoDocumento"),entity.getInTipoDocumento())
                && areEquals(getEntityValue("visibilidade"),entity.getVisibilidade())
                && areEquals(getEntityValue("numera"),entity.getNumera())
                && areEquals(getEntityValue("tipoNumeracao"),entity.getTipoNumeracao())
                && areEquals(getEntityValue("sistema"),entity.getSistema())
                && areEquals(getEntityValue("publico"),entity.getPublico())
                && areEquals(getEntityValue("ativo"),entity.getAtivo())
                && areEquals(getEntityValue("tipoProcessoDocumentoObservacao"),entity.getTipoProcessoDocumentoObservacao());
    }
    
    @Override
    protected List<EntityActionContainer<TipoProcessoDocumento>> getUpdateFailList() {
        final ArrayList<EntityActionContainer<TipoProcessoDocumento>> list = new ArrayList<>();
        TipoProcessoDocumento createdEntity = createInstance("", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        list.add(new EntityActionContainer<TipoProcessoDocumento>(createdEntity) {
            @Override
            public void execute() {
                final Object id = getId();
                assert id != null;
                newInstance();
                initEntity(getEntity());
                setEntityValue("idTipoProcessoDocumento", ((int) id) + 1);
            }
        });
        return list;
    }

    @Override
    protected ArrayList<EntityActionContainer<TipoProcessoDocumento>> getUpdateSuccessList() {
        final ArrayList<EntityActionContainer<TipoProcessoDocumento>> list = new ArrayList<>();
        TipoProcessoDocumento createdEntity = createInstance("", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        list.add(new EntityActionContainer<TipoProcessoDocumento>(createdEntity) {
            @Override
            public void execute() {
                final Object id = getId();
                assert id != null;
                newInstance();
                setId(id);
                
                setEntityValue("codigoDocumento", fillStr("updateCodigoDocumento", 25));                
            }
        });
        return list;
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

}