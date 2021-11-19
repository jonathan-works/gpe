package br.com.infox.epp.system;

import static br.com.infox.epp.FieldType.SELECT_ONE;
import static br.com.infox.epp.Filter.equal;
import static br.com.infox.epp.Filter.isTrue;

import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.FieldType;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.processo.partes.entity.TipoParte_;
import br.com.infox.epp.system.parametro.ParametroDefinition;

public enum Parametros {

    SINAL_TAREFA_EXTERNA("sinalTarefaExterna"),
    MODELO_DOC_CHAVE_CONSULTA("modeloDocumentoChaveConsulta"),
    MODELO_DOC_DOWNLOAD_PDF("modeloDocumentoDownloadPDF"),
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
    DS_URL_SERVICO_ETURMALINA(
            new ParametroDefinition<String>("eTurmalina", "dsUrlServicoETurmalina", FieldType.STRING)),
    DS_LOGIN_USUARIO_ETURMALINA(
            new ParametroDefinition<String>("eTurmalina", "dsLoginUsuarioETurmalina", FieldType.STRING)),
    DS_SENHA_USUARIO_ETURMALINA(
            new ParametroDefinition<String>("eTurmalina", "dsSenhaUsuarioETurmalina", FieldType.STRING)),
    CODIGO_CLIENTE_ENVIO_LOG("codigoClienteEnvioLog"),
    PASSWORD_CLIENTE_ENVIO_LOG("passwordClienteEnvioLog"),
    IS_ATIVO_ENVIO_LOG_AUTOMATICO("ativarServicoEnvioLogAutomatico"),
    URL_SERVICO_ENVIO_LOG_ERRO("urlServicoEnvioLogErro"),
    HAS_CONSULTA_EXTERNA_PADRAO("ativaConsultaExternaPadrao"),
    CODIGO_UF_SISTEMA("codigoUnidadeFederativaSistema"),
    INTERVALO_ATUALIZACAO_PAINEL("sistema", "intervaloAtualizacaoPainel", false),
    TEXTO_RODAPE_DOCUMENTO(
            new ParametroDefinition<String>("sistema", "textoRodapeDocumento", FieldType.TEXT)),
    INFO_CERT_ELETRONICO_RAIZ(
            new ParametroDefinition<String>("sistema", "infoCertificadoEletronicoRaiz", FieldType.TEXT)),
    CERT_ELETRONICO_RAIZ("sistema", "idCertificadoEletronicoRaiz", true),
    EPP_API_RSA_PRIVATE_KEY("controleAcesso","eppApiPrivateKey", FieldType.TEXT),
    EPP_API_RSA_PUBLIC_KEY("controleAcesso","eppApiPublicKey", FieldType.TEXT),
    ATIVAR_MASSIVE_REINDEX("ativarMassiveReindex"),
    TERMO_ADESAO(new ParametroDefinition<>("controleAcesso", "termoAdesao", SELECT_ONE, ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.tituloModeloDocumento)
            .addFilter(isTrue(ModeloDocumento_.ativo))),
    REST_THREAD_POOL_EXECUTOR_MAXIMUM_POOL_SIZE("restPublicApi", "restThreadPoolExecutorMaximumPoolSize"),
    REST_THREAD_POOL_EXECUTOR_CORE_POOL_SIZE("restPublicApi", "restThreadPoolExecutorCorePoolSize", true),
    REST_THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME("restPublicApi", "restThreadPoolExecutorKeepAliveTime", true),
    PRODUCAO("producao"),
    NAO_EXECUTAR_QUARTZ("naoExecutarQuartz"),
    VALIDA_CPF_ASSINATURA("digital-signature","validaCpfAssinatura", FieldType.BOOLEAN),
    VALIDA_ASSINATURA("digital-signature","validacaoAssinatura", FieldType.BOOLEAN),
    FOLHA_ROSTO_PROCESSO("folhaRostoProcesso"),
    FOLHA_ROSTO_MOVIMENTACOES("folhaRostoMovimentacoes"),
    URL_WS_VIDA_FUNCIONAL_GDPREV("gdprev", "urlWSVidaFuncionalGDPrev", FieldType.STRING),
    TOKEN_WS_VIDA_FUNCIONAL_GDPREV("gdprev", "tokenWSVidaFuncionalGDPrev", FieldType.STRING),
    CLASSIFICACAO_DOC_PDF_GDPREV(new ParametroDefinition<>("gdprev", "codigoClassificacaoDocPdfGDPrev", FieldType.SELECT_ONE,
            ClassificacaoDocumento_.descricao, ClassificacaoDocumento_.codigoDocumento).addFilter(isTrue(ClassificacaoDocumento_.ativo))),
    CLASSIFICACAO_DOC_FUNCIONARIOS_VF_GDPREV(new ParametroDefinition<>("gdprev", "codigoClassificacaoFuncionariosVFGDPrev", FieldType.SELECT_ONE,
            ClassificacaoDocumento_.descricao, ClassificacaoDocumento_.codigoDocumento).addFilter(isTrue(ClassificacaoDocumento_.ativo))),
    TIPO_PARTE_SERVIDOR(new ParametroDefinition<>("gdprev", "codigoTipoParteServidor", FieldType.SELECT_ONE,
            TipoParte_.descricao, TipoParte_.identificador)),
    USUARIO_INCLUSAO_DOC_GDPREV(new ParametroDefinition<>("gdprev", "loginUsuarioInclusaoDocGDPrev", FieldType.SELECT_ONE,
            UsuarioLogin_.nomeUsuario, UsuarioLogin_.login).addFilter(equal(UsuarioLogin_.tipoUsuario, UsuarioEnum.S))),
    EXIBIR_AVISO_INCONSISTENCIA_PARTICIPANTE("gdprev", "exibirAvisoInconsistenciaParticipante", FieldType.BOOLEAN),
    URL_ACESSO_PROCESSOS_LEGADOS("sistema", "urlAcessoProcessosLegados", FieldType.STRING),
    CD_MODELO_DOCUMENTO_FOLHA_ROSTO_RESUMO_PROCESSO(new ParametroDefinition<>("sistema", "cdModDocFolhaRostoResumoProc", FieldType.SELECT_ONE, ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.codigo)
            .addFilter(isTrue(ModeloDocumento_.ativo))),
    CD_MODELO_DOCUMENTO_FOLHA_TRAMITACAO_RESUMO_PROCESSO(new ParametroDefinition<>("sistema", "cdModDocFolhaTramResumoProc", FieldType.SELECT_ONE, ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.codigo)
    .addFilter(isTrue(ModeloDocumento_.ativo))),
    LIMITE_PAGINA_ASSINATURA_ELETRONICA(
            new ParametroDefinition<String>("assinaturaEletronica", "limitePaginaAssinaturaEletronica", FieldType.STRING)),
    LIMITE_DIAS_GERACAO_RELATORIO(
            new ParametroDefinition<String>("sistema", "limiteDiasGeracaoRelatorio", FieldType.STRING));

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

    public <T> T getValue(Class<T> clazz) {
        String valueString = (String) Contexts.getApplicationContext().get(this.label);
        if ( !StringUtil.isEmpty(valueString) ) {
            Object newInstance = ReflectionsUtil.newInstance(clazz, String.class, valueString.trim());
            return clazz.cast(newInstance);
        } else {
            return null;
        }
    }

    public <T> T getValueOrDefault(Class<T> clazz, T defaultValue) {
        T objectT = getValue(clazz);
        return null == objectT ? defaultValue : objectT ;
    }

    public ParametroDefinition<?> getParametroDefinition() {
        return parametroDefinition;
    }
}
