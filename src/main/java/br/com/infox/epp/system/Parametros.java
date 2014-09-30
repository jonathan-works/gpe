package br.com.infox.epp.system;

import org.jboss.seam.contexts.Contexts;

public enum Parametros {
	
	IS_USUARIO_EXTERNO_VER_DOC_EXCLUIDO("usuarioExternoPodeVerDocExcluido"),
	SOMENTE_USUARIO_INTERNO_PODE_VER_HISTORICO("somenteUsuarioInternoVerMotivoExclusaoDoc");
	
	private String label;
	
	private Parametros(String label){
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public Object getValue(){
		return Contexts.getApplicationContext().get(this.label);
	}

}
