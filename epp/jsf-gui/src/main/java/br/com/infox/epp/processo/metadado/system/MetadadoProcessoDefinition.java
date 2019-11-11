package br.com.infox.epp.processo.metadado.system;

import java.io.Serializable;

public final class MetadadoProcessoDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	private String metadadoType;
	private Class<?> classType;
	private String label;
	private int prioridade = BUILT_IN;
	
	public MetadadoProcessoDefinition(String metadadoType, Class<?> classType) {
		this(metadadoType, null, BUILT_IN, classType);
	}
	
	public MetadadoProcessoDefinition(String metadadoType, String label, Class<?> classType) {
		this(metadadoType, label, BUILT_IN, classType);
	}
	
	public MetadadoProcessoDefinition(String metadadoType, String label, int prioridade, Class<?> classType) {
		if (prioridade != BUILT_IN && prioridade != APPLICATION && prioridade != FRAMEWORK && prioridade != DEPLOYMENT) {
			throw new IllegalArgumentException("Prioridade inválida");
		}
		this.prioridade = prioridade;
		this.metadadoType = metadadoType;
		this.label = label;
		this.classType = classType;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int getPrioridade() {
		return prioridade;
	}
	
	public String getMetadadoType() {
		return metadadoType;
	}
	
	public Class<?> getClassType() {
		return classType;
	}
	
	public static final int BUILT_IN = 0;
	public static final int FRAMEWORK = 10;
	public static final int APPLICATION = 20;
	public static final int DEPLOYMENT = 30;
}
