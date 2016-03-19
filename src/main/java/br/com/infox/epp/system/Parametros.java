package br.com.infox.epp.system;

import org.jboss.seam.contexts.Contexts;

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
	CODIGO_FLUXO_COMUNICACAO_ELETRONICA("codigoFluxoComunicacao"),
	CODIGO_FLUXO_COMUNICACAO_NAO_ELETRONICA("codigoFluxoComunicacaoNaoEletronico"),
	CODIGO_FLUXO_DOCUMENTO("codigoFluxoDocumento"),
	CODIGO_FLUXO_COMUNICACAO_INTERNA("codigoFluxoComunicacaoInterna"),
    RICHFACES_FILE_UPLOAD_MAX_FILES_QUANTITY("richFileUploadMaxFilesQuantity"),
	IS_PRORROGACAO_AUTOMATICA_POR_MODELO_COMUNICACAO("prorrogarPrazoAutomaticamentePorModelo");
	
	private String label;
	
	private Parametros(String label){
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public String getValue(){
		return (String) Contexts.getApplicationContext().get(this.label);
	}

}
