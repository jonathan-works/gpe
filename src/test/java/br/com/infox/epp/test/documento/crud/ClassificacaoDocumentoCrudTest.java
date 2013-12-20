package br.com.infox.epp.test.documento.crud;

import java.util.ArrayList;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.crud.MockCrudAction;
import br.com.infox.epp.test.crud.EntityAction;
import br.com.infox.epp.test.crud.EntityActionContainer;

public class ClassificacaoDocumentoCrudTest extends AbstractGenericCrudTest<TipoProcessoDocumento> {

    private static int id = 1;
    final private MockClassificacaoDocumentoCrud mockCrudAction = new MockClassificacaoDocumentoCrud();

    @Override
    protected MockCrudAction<TipoProcessoDocumento> getMockCrudAction() {
        return mockCrudAction;
    }

    private TipoProcessoDocumento createInstance(
            final int idTipoProcessoDocumento, final String codigoDocumento,
            final String tipoProcessoDocumento,
            final TipoDocumentoEnum tipoDocumento,
            final VisibilidadeEnum visibilidade, final Boolean numera,
            final TipoNumeracaoEnum tipoNumeracao, final Boolean sistema,
            final Boolean publico, final Boolean ativo,
            final String tipoProcessoDocumentoObservacao) {
        final TipoProcessoDocumento instance = new TipoProcessoDocumento();
        instance.setIdTipoProcessoDocumento(idTipoProcessoDocumento);
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

    @Override
    protected void initLists() {
        final ArrayList<TipoProcessoDocumento> persistList = initPersistList();
        final ArrayList<TipoProcessoDocumento> persistFailList = initPersistFailList();
        final ArrayList<EntityActionContainer<TipoProcessoDocumento>> updateList = initUpdateList();
        final ArrayList<EntityActionContainer<TipoProcessoDocumento>> updateFailList = initUpdateFailList();
        final ArrayList<TipoProcessoDocumento> removeList = initRemoveList();
        final ArrayList<TipoProcessoDocumento> removeFailList = new ArrayList<>(0);
        
        setPersistList(persistList);
        setPersistFailList(persistFailList);

        setUpdateList(updateList);
        setUpdateFailList(updateFailList);

        setRemoveList(removeList);
        setRemoveFailList(removeFailList);

        setInactivateList(removeList);
        setInactivateFailList(removeFailList);
    }

    private ArrayList<EntityActionContainer<TipoProcessoDocumento>> initUpdateFailList() {
        final ArrayList<EntityActionContainer<TipoProcessoDocumento>> list = new ArrayList<>();
        final TipoProcessoDocumento entity = createInstance(id++, "", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");

        final EntityActionContainer<TipoProcessoDocumento> container = new EntityActionContainer<TipoProcessoDocumento>(entity, new EntityAction<TipoProcessoDocumento>() {
            @Override
            public void run(final TipoProcessoDocumento entity) {
                entity.setIdTipoProcessoDocumento(id++);
                entity.setCodigoDocumento("novoCodigo");
            }
        });
        list.add(container);

        return list;
    }

    private ArrayList<EntityActionContainer<TipoProcessoDocumento>> initUpdateList() {
        final ArrayList<EntityActionContainer<TipoProcessoDocumento>> list = new ArrayList<>();
        final TipoProcessoDocumento entity = createInstance(id++, "", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");

        final EntityActionContainer<TipoProcessoDocumento> container = new EntityActionContainer<TipoProcessoDocumento>(entity, new EntityAction<TipoProcessoDocumento>() {
            @Override
            public void run(final TipoProcessoDocumento entity) {
                entity.setCodigoDocumento("novoCodigo");
            }
        });
        list.add(container);

        return list;
    }

    private ArrayList<TipoProcessoDocumento> initRemoveList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(createInstance(id++, i + "", i + "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i
                    + ""));
        }
        return list;
    }

    private ArrayList<TipoProcessoDocumento> initPersistList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();
        list.add(createInstance(id++, fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        list.add(createInstance(id++, fillStr("codigoDocumento", 12), fillStr("descricao", 0), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));

        list.add(createInstance(id++, fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        list.add(createInstance(id++, fillStr("codigoDocumento", 0), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        for (int i = 0; i < 25; i++) {
            list.add(createInstance(id++, i + "", i + "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i
                    + ""));
        }
        return list;
    }

    private ArrayList<TipoProcessoDocumento> initPersistFailList() {
        final ArrayList<TipoProcessoDocumento> list = new ArrayList<>();

        list.add(createInstance(id++, fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO + 1), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        list.add(createInstance(id++, fillStr("codigoDocumento", 12), null, TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));

        list.add(createInstance(id++, fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO + 1), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        return list;
    }
}
