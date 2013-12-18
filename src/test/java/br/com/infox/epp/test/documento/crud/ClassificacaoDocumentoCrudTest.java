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
import br.com.itx.util.EntityUtil;

public class ClassificacaoDocumentoCrudTest {
    private static int id=1;
    private MockClassificacaoDocumentoCrud classificacaoDocumentoCrudAction;

    @After
    public void afterTest() {
//        session.close();
//        sessionFactory.close();
    }

    @Before
    public void beforeTest() {
        classificacaoDocumentoCrudAction = new MockClassificacaoDocumentoCrud();
    }

    @Test
    public void testInactivate() {
        int entityId = id++;
        TipoProcessoDocumento entity = createInstance(entityId, "", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        classificacaoDocumentoCrudAction.newInstance();
        persistEntity(entity);
        
        Assert.assertTrue(classificacaoDocumentoCrudAction.getInstance().getAtivo());
        classificacaoDocumentoCrudAction.inactive(classificacaoDocumentoCrudAction.getInstance());
        Assert.assertFalse(classificacaoDocumentoCrudAction.getInstance().getAtivo());
    }
    
    @Test
    public void testMultipleInserts() {
        for (int i = 0; i < 25; i++) {
            persistEntity(createInstance(id++, i+"", i+"", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+""));
            classificacaoDocumentoCrudAction.newInstance();
        }
        List<TipoProcessoDocumento> list = classificacaoDocumentoCrudAction.getAll();
        Assert.assertTrue(list.size() >= 25);
    }

    @Test
    public void testRemove() {
        for (int i = 1; i <= 25; i++) {
            persistEntity(createInstance(id++, i+"", i+"", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+""));
            classificacaoDocumentoCrudAction.newInstance();    
        }
        
        final List<TipoProcessoDocumento> list = classificacaoDocumentoCrudAction.getAll();
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
        Assert.assertNotNull(classificacaoDocumentoCrudAction.getId());
        TipoProcessoDocumento currentInstance = classificacaoDocumentoCrudAction.getInstance();
        Assert.assertNotNull(currentInstance);
        assertEquals(cloneEntity, currentInstance);
    }
    
    @Test
    public void testUpdate() {
        int entityId = id++;
        TipoProcessoDocumento entity = createInstance(entityId, "", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        classificacaoDocumentoCrudAction.newInstance();
        persistEntity(entity);
        
        entity = classificacaoDocumentoCrudAction.getInstance();
        Assert.assertEquals("", entity.getCodigoDocumento());
        entity.setCodigoDocumento("novoCodigo");
        classificacaoDocumentoCrudAction.setInstance(entity);
        classificacaoDocumentoCrudAction.save();
        entity = classificacaoDocumentoCrudAction.getInstance();
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
        int size = classificacaoDocumentoCrudAction.getAll().size();
        classificacaoDocumentoCrudAction.remove(entity);
        int afterSize = classificacaoDocumentoCrudAction.getAll().size();
        Assert.assertTrue(afterSize < size);
    }
    
    private void assertRemoveFalse(TipoProcessoDocumento entity) {
        int size = classificacaoDocumentoCrudAction.getAll().size();
        classificacaoDocumentoCrudAction.remove(entity);
        int afterSize = classificacaoDocumentoCrudAction.getAll().size();
        Assert.assertTrue(afterSize == size);
    }
    
    private void assertInsertFalse(TipoProcessoDocumento entity) {
        int size = classificacaoDocumentoCrudAction.getAll().size();
        persistEntity(entity);
        classificacaoDocumentoCrudAction.newInstance();
        int afterSize = classificacaoDocumentoCrudAction.getAll().size();
        Assert.assertTrue(size==afterSize);
    }
    
    private void assertInsertTrue(TipoProcessoDocumento entity) {
        int size = classificacaoDocumentoCrudAction.getAll().size();
        persistEntity(entity);
        classificacaoDocumentoCrudAction.newInstance();
        int afterSize = classificacaoDocumentoCrudAction.getAll().size();
        Assert.assertTrue(size<afterSize);
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
        Assert.assertFalse(classificacaoDocumentoCrudAction.isManaged());
        classificacaoDocumentoCrudAction.setInstance(entity);
        classificacaoDocumentoCrudAction.save();
        Assert.assertTrue(classificacaoDocumentoCrudAction.isManaged());
    }

}
