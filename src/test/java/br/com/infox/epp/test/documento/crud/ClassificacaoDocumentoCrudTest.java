package br.com.infox.epp.test.documento.crud;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.itx.util.EntityUtil;

public class ClassificacaoDocumentoCrudTest {

    private ClassificacaoDocumentoCrudAction classificacaoDocumentoCrudAction;
    //private SessionFactory sessionFactory;
    //private Session session;

//    @SuppressWarnings("deprecation")
//    private SessionFactory createSessionFactory() {
//        AnnotationConfiguration configuration = new AnnotationConfiguration();
//        configuration.addAnnotatedClass(TipoProcessoDocumento.class).addAnnotatedClass(ProcessoDocumento.class).addAnnotatedClass(ProcessoDocumentoBin.class).addAnnotatedClass(Localizacao.class).addAnnotatedClass(Papel.class).addAnnotatedClass(UsuarioLogin.class).addAnnotatedClass(Caixa.class).addAnnotatedClass(ItemTipoDocumento.class).addAnnotatedClass(GrupoModeloDocumento.class).addAnnotatedClass(TipoModeloDocumento.class).addAnnotatedClass(ModeloDocumento.class).addAnnotatedClass(VariavelTipoModelo.class).addAnnotatedClass(Variavel.class).addAnnotatedClass(TarefaJbpm.class).addAnnotatedClass(LocalizacaoTurno.class).addAnnotatedClass(EntityLog.class).addAnnotatedClass(BloqueioUsuario.class).addAnnotatedClass(EntityLogDetail.class).addAnnotatedClass(UsuarioLocalizacao.class).addAnnotatedClass(Tarefa.class).addAnnotatedClass(Fluxo.class).addAnnotatedClass(FluxoPapel.class).addAnnotatedClass(PessoaFisica.class).addAnnotatedClass(Processo.class).addAnnotatedClass(Estatistica.class);
//        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
//        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem");
//        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
//        return configuration.buildSessionFactory();
//    }

    @Before
    public void beforeTest() {
//        sessionFactory = createSessionFactory();
//        session = sessionFactory.openSession();
        classificacaoDocumentoCrudAction = new MockClassificacaoDocumentoCrud();
    }

    private String getFilled(String string, int length) {
        StringBuilder sb = new StringBuilder();
        if (string.length() < 1) {
            string = " ";
        }
        for (int i = 0, l = 1 + (length / string.length()); i < l; i++) {
            sb.append(string);
        }

        return sb.substring(0, length);
    }

    @Test
    public void testSave() throws InstantiationException, IllegalAccessException {
        int id=1;
        String codigo = getFilled("codigoDocumento", 15);
        String descricao = getFilled("descricao", 9);
        TipoDocumentoEnum tipoDocumento = TipoDocumentoEnum.T;
        VisibilidadeEnum visibilidade = VisibilidadeEnum.A;
        Boolean numeravel = Boolean.TRUE;
        TipoNumeracaoEnum tipoNumeracao = TipoNumeracaoEnum.S;
        Boolean sistema = Boolean.FALSE;
        Boolean publico = Boolean.FALSE;
        Boolean ativo = Boolean.TRUE;
        String tipoProcessoDocumentoObservacao = getFilled("tipoProcessoDocumentoObservacao", 10);
        
        TipoProcessoDocumento entity = createInstance(id++, codigo, descricao, tipoDocumento, visibilidade, numeravel, tipoNumeracao, sistema, publico, ativo, tipoProcessoDocumentoObservacao);
        TipoProcessoDocumento cloneEntity = EntityUtil.cloneEntity(entity, sistema);
        cloneEntity.setIdTipoProcessoDocumento(entity.getIdTipoProcessoDocumento());
        
        persistEntity(entity);
        Assert.assertNotNull(classificacaoDocumentoCrudAction.getId());
        TipoProcessoDocumento currentInstance = classificacaoDocumentoCrudAction.getInstance();
        Assert.assertNotNull(currentInstance);
        assertEquals(cloneEntity, currentInstance);
    }
    
    @Test
    public void testInactivate() {
        int entityId = 1;
        TipoProcessoDocumento entity = createInstance(entityId, "", "", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, "");
        classificacaoDocumentoCrudAction.newInstance();
        persistEntity(entity);
        
        Assert.assertTrue(classificacaoDocumentoCrudAction.getInstance().getAtivo());
        classificacaoDocumentoCrudAction.inactive(classificacaoDocumentoCrudAction.getInstance());
        Assert.assertFalse(classificacaoDocumentoCrudAction.getInstance().getAtivo());
    }
    
    @Test
    public void testUpdate() {
        int entityId = 1;
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

    private void persistEntity(TipoProcessoDocumento entity) {
        Assert.assertFalse(classificacaoDocumentoCrudAction.isManaged());
        classificacaoDocumentoCrudAction.setInstance(entity);
        classificacaoDocumentoCrudAction.save();
        Assert.assertTrue(classificacaoDocumentoCrudAction.isManaged());
    }
    
    @Test
    public void testRemove() {
        int id=1;
        for (int i = 1; i <= 25; i++) {
            persistEntity(createInstance(id++, i+"", i+"", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+""));
            classificacaoDocumentoCrudAction.newInstance();    
        }
        
        classificacaoDocumentoCrudAction.setId(3);
        TipoProcessoDocumento tipoProcessoDocumento = classificacaoDocumentoCrudAction.getInstance();
        classificacaoDocumentoCrudAction.remove();
        classificacaoDocumentoCrudAction.newInstance();

        persistEntity(tipoProcessoDocumento);
    }
    
    @Test
    public void testMultipleInserts() {
        int id=1;
        for (int i = 0; i < 25; i++) {
            persistEntity(createInstance(id++, i+"", i+"", TipoDocumentoEnum.T, VisibilidadeEnum.A, Boolean.TRUE, TipoNumeracaoEnum.S, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, i+""));
            classificacaoDocumentoCrudAction.newInstance();    
        }
        TipoProcessoDocumento anterior = null;
        for (int i = 1; i < id; i++) {
            classificacaoDocumentoCrudAction.setId(i);
            Assert.assertNotNull(classificacaoDocumentoCrudAction.getInstance());
            Assert.assertFalse(classificacaoDocumentoCrudAction.getInstance().equals(anterior));
            anterior = classificacaoDocumentoCrudAction.getInstance();
        }
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

    @After
    public void afterTest() {
//        session.close();
//        sessionFactory.close();
    }

}
