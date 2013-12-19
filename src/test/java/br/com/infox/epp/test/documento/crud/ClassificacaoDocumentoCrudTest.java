package br.com.infox.epp.test.documento.crud;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.test.core.messages.MockMessagesHandler;
import br.com.itx.util.EntityUtil;

public class ClassificacaoDocumentoCrudTest {
    private static int id=1;
    private MockClassificacaoDocumentoCrud mockCrudAction;

    @After
    public void afterTest() {
        MockMessagesHandler.instance().clear();
    }

    @Before
    public void beforeTest() {
        mockCrudAction = new MockClassificacaoDocumentoCrud();
    }

    @Test
    public void testInactivate() {
        int entityId = id++;
        TipoProcessoDocumento entity = createInstance(entityId, "", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        mockCrudAction.newInstance();
        persistEntity(entity);
        
        Assert.assertTrue(mockCrudAction.getInstance().getAtivo());
        mockCrudAction.inactive(mockCrudAction.getInstance());
        Assert.assertFalse(mockCrudAction.getInstance().getAtivo());
    }
    
    @Test
    public void testMultipleInserts() {
        for (int i = 0; i < 25; i++) {
            persistEntity(createInstance(id++, i+"", i+"", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+""));
            mockCrudAction.newInstance();
        }
        List<TipoProcessoDocumento> list = mockCrudAction.getAll();
        Assert.assertTrue(list.size() >= 25);
    }

    @Test
    public void testRemove() {
        for (int i = 1; i <= 25; i++) {
            persistEntity(createInstance(id++, i+"", i+"", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+""));
            mockCrudAction.newInstance();    
        }
        
        final List<TipoProcessoDocumento> list = mockCrudAction.getAll();
        for (TipoProcessoDocumento tipoProcessoDocumento : list) {
            assertRemoveTrue(tipoProcessoDocumento);
        }
    }
    
    @Test
    public void testRestricoesCodigoDocumento() {
        assertInsertTrue(createInstance(id++, fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        
        assertInsertTrue(createInstance(id++, fillStr("codigoDocumento", 0), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        
        assertInsertFalse(createInstance(id++, fillStr("codigoDocumento", LengthConstants.CODIGO_DOCUMENTO+1), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
    }
    
    @Test
    public void testRestricoesTipoProcessoDocumento() {
        assertInsertTrue(createInstance(id++, fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));

        assertInsertTrue(createInstance(id++, fillStr("codigoDocumento", 12), fillStr("descricao", 0), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));

        assertInsertFalse(createInstance(id++, fillStr("codigoDocumento", 12), fillStr("descricao", LengthConstants.DESCRICAO_PADRAO+1), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
        
        assertInsertFalse(createInstance(id++, fillStr("codigoDocumento", 12), null, TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10)));
    }
    
    @Test
    public void testSave() throws InstantiationException, IllegalAccessException {
        TipoProcessoDocumento entity = createInstance(id++, fillStr("codigoDocumento", 15), fillStr("descricao", 9), TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, fillStr("tipoProcessoDocumentoObservacao", 10));
        TipoProcessoDocumento cloneEntity = EntityUtil.cloneEntity(entity, false);
        
        persistEntity(entity);
        
        cloneEntity.setIdTipoProcessoDocumento(entity.getIdTipoProcessoDocumento());
        Assert.assertNotNull(mockCrudAction.getId());
        TipoProcessoDocumento currentInstance = mockCrudAction.getInstance();
        Assert.assertNotNull(currentInstance);
        assertEquals(cloneEntity, currentInstance);
    }
    
    @Test
    public void testUpdate() {
        int entityId = id++;
        TipoProcessoDocumento entity = createInstance(entityId, "", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        mockCrudAction.newInstance();
        persistEntity(entity);
        
        entity = mockCrudAction.getInstance();
        Assert.assertEquals("", entity.getCodigoDocumento());
        entity.setCodigoDocumento("novoCodigo");
        mockCrudAction.setInstance(entity);
        mockCrudAction.save();
        entity = mockCrudAction.getInstance();
        Assert.assertEquals("novoCodigo", entity.getCodigoDocumento());
    }

    private void assertEquals(TipoProcessoDocumento expected,
            TipoProcessoDocumento actual) {
        Assert.assertEquals(expected.getIdTipoProcessoDocumento(), actual.getIdTipoProcessoDocumento());
        Assert.assertEquals(expected.getCodigoDocumento(), actual.getCodigoDocumento());
        Assert.assertEquals(expected.getTipoProcessoDocumento(), actual.getTipoProcessoDocumento());
        Assert.assertEquals(expected.getInTipoDocumento(), actual.getInTipoDocumento());
        Assert.assertEquals(expected.getVisibilidade(), actual.getVisibilidade());
        Assert.assertEquals(expected.getNumera(), actual.getNumera());
        
        if (expected.getNumera()) {
            Assert.assertEquals(expected.getTipoNumeracao(), actual.getTipoNumeracao());   
        } else {
            Assert.assertNull(actual.getTipoNumeracao());
        }
        
        Assert.assertEquals(expected.getSistema(), actual.getSistema());
        Assert.assertEquals(expected.getPublico(), actual.getPublico());
        Assert.assertEquals(expected.getAtivo(), actual.getAtivo());
        Assert.assertEquals(expected.getTipoProcessoDocumentoObservacao(), actual.getTipoProcessoDocumentoObservacao());
    }
    
    private void assertRemoveTrue(TipoProcessoDocumento entity) {
        int size = mockCrudAction.getAll().size();
        mockCrudAction.remove(entity);
        List<TipoProcessoDocumento> list = mockCrudAction.getAll();
        int afterSize = list.size();
        Assert.assertTrue(afterSize < size);
        Assert.assertFalse(list.contains(entity));
    }
    
    private void assertRemoveFalse(TipoProcessoDocumento entity) {
        int size = mockCrudAction.getAll().size();
        mockCrudAction.remove(entity);
        List<TipoProcessoDocumento> list = mockCrudAction.getAll();
        int afterSize = list.size();
        Assert.assertTrue(afterSize == size);
        Assert.assertTrue(list.contains(entity));
    }
    
    private void assertInsertFalse(TipoProcessoDocumento entity) {
        int size = mockCrudAction.getAll().size();
        persistEntity(entity);
        mockCrudAction.newInstance();
        List<TipoProcessoDocumento> list = mockCrudAction.getAll();
        int afterSize = list.size();
        Assert.assertTrue(size==afterSize);
        Assert.assertFalse(list.contains(entity));
    }
    
    private void assertInsertTrue(TipoProcessoDocumento entity) {
        int size = mockCrudAction.getAll().size();
        persistEntity(entity);
        mockCrudAction.newInstance();
        List<TipoProcessoDocumento> list = mockCrudAction.getAll();
        int afterSize = list.size();
        Assert.assertTrue(size<afterSize);
        Assert.assertTrue(list.contains(entity));
    }

    private TipoProcessoDocumento createInstance(int idTipoProcessoDocumento,
            String codigoDocumento, String tipoProcessoDocumento,
            TipoDocumentoEnum tipoDocumento, VisibilidadeEnum visibilidade,
            Boolean numera, TipoNumeracaoEnum tipoNumeracao, Boolean sistema,
            Boolean publico, Boolean ativo,
            String tipoProcessoDocumentoObservacao) {
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
    
    private String fillStr(String string, int topLength) {
        if (string == null || string.length() < 1) {
            string = "-";
        }
        
        StringBuilder sb = new StringBuilder(string);
        int length = string.length();
        if (length < topLength) {
            for (int i = 0, l = topLength-length; i < l; i++) {
                sb.append(" ");
            }
        }
        return sb.substring(0, topLength);
    }

    private void persistEntity(TipoProcessoDocumento entity) {
        Assert.assertFalse(mockCrudAction.isManaged());
        mockCrudAction.setInstance(entity);
        mockCrudAction.save();
        Assert.assertTrue(mockCrudAction.isManaged());
    }

}
