package br.com.infox.epp.system;

import static br.com.infox.epp.FieldType.SELECT_ONE;
import static br.com.infox.epp.Filter.isTrue;

import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.FieldType;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.system.parametro.ParametroDefinition;

public enum Parametros {
    
    IS_USUARIO_EXTERNO_VER_DOC_EXCLUIDO("usuarioExternoPodeVerDocExcluido"),
    SOMENTE_USUARIO_INTERNO_PODE_VER_HISTORICO("somenteUsuarioInternoVerMotivoExclusaoDoc"),
    ID_USUARIO_PROCESSO_SISTEMA("idUsuarioProcessoSistema"),
    ID_USUARIO_SISTEMA("idUsuarioSistema"),
    PAPEL_USUARIO_INTERNO("usuarioInterno"),
    PAPEL_USUARIO_EXTERNO("usuarioExterno"),
    PASTA_DOCUMENTO_GERADO("pastaDocumentoGerado"),
    RAIZ_LOCALIZACOES_COMUNICACAO("raizLocalizacoesComunicacao"),
    RAIZ_LOCALIZACOES_COMUNICACAO_INTERNA("raizLocalizacoesComunicacaoInterna"),
    RAIZ_LOCALIZACOES_ASSINATURA_COMUNICACAO("codigoRaizResponsavelAssinaturaLocalizacao"),
    CODIGO_FLUXO_COMUNICACAO_ELETRONICA("codigoFluxoComunicacao"),
    CODIGO_FLUXO_COMUNICACAO_NAO_ELETRONICA("codigoFluxoComunicacaoNaoEletronico"),
    CODIGO_FLUXO_DOCUMENTO("codigoFluxoDocumento"),
    CODIGO_FLUXO_COMUNICACAO_INTERNA("codigoFluxoComunicacaoInterna"),
    IS_PRORROGACAO_AUTOMATICA_POR_MODELO_COMUNICACAO("prorrogarPrazoAutomaticamentePorModelo"),
    RICHFACES_FILE_UPLOAD_MAX_FILES_QUANTITY("richFileUploadMaxFilesQuantity"),
    WEB_SERVICE_TOKEN("webserviceToken"),
    CODIGO_CLIENTE_ENVIO_LOG("codigoClienteEnvioLog"),
    PASSWORD_CLIENTE_ENVIO_LOG("passwordClienteEnvioLog"),
    IS_ATIVO_ENVIO_LOG_AUTOMATICO("ativarServicoEnvioLogAutomatico"),
    URL_SERVICO_ENVIO_LOG_ERRO("urlServicoEnvioLogErro"),
    HAS_CONSULTA_EXTERNA_PADRAO("ativaConsultaExternaPadrao"),
    CODIGO_UF_SISTEMA("codigoUnidadeFederativaSistema"),
    EPP_API_RSA_PRIVATE_KEY("controleAcesso","eppApiPrivateKey"),
    EPP_API_RSA_PUBLIC_KEY("controleAcesso","eppApiPublicKey"),
    ATIVAR_MASSIVE_REINDEX("ativarMassiveReindex"),
    TERMO_ADESAO(new ParametroDefinition<>("controleAcesso", "termoAdesao", SELECT_ONE, ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.tituloModeloDocumento)
            .addFilter(isTrue(ModeloDocumento_.ativo))),
    REST_THREAD_POOL_EXECUTOR_MAXIMUM_POOL_SIZE("restPublicApi", "restThreadPoolExecutorMaximumPoolSize"),
    REST_THREAD_POOL_EXECUTOR_CORE_POOL_SIZE("restPublicApi", "restThreadPoolExecutorCorePoolSize", true),
    REST_THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME("restPublicApi", "restThreadPoolExecutorKeepAliveTime", true),
    PRODUCAO("producao"),
    VALIDA_CPF_ASSINATURA("digital-signature","validaCpfAssinatura", FieldType.BOOLEAN),
    VALIDA_ASSINATURA("digital-signature","validacaoAssinatura", FieldType.BOOLEAN),
    ;

    private final String label;
    private final ParametroDefinition<?> parametroDefinition;

    private Parametros(String grupo, String nome, boolean sistema){
        this(new ParametroDefinition<>(grupo, nome, FieldType.STRING, sistema, false));
        
    }
    private Parametros(String grupo, String nome){
        this(new ParametroDefinition<>(grupo, nome, FieldType.STRING));
    }
    private Parametros(String grupo, String nome, FieldType fieldType){
        this(new ParametroDefinition<>(grupo, nome, fieldType));
    }
    
    private Parametros(ParametroDefinition<?> parametroDefinition) {
        this.label = parametroDefinition.getNome();
        this.parametroDefinition = parametroDefinition;
    }

    private Parametros(String label) {
        this.label = label;
        parametroDefinition = null;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return (String) Contexts.getApplicationContext().get(this.label);
    }

    public ParametroDefinition<?> getParametroDefinition() {
        return parametroDefinition;
    }
}
