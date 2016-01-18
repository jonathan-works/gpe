package br.com.infox.epp.system.parametro;

import java.util.Objects;

import javax.persistence.metamodel.SingularAttribute;

public class ParametroDefinition implements Comparable<ParametroDefinition> {

	public static interface Precedencia {
		int DEFAULT = 0;
		int FRAMEWORK = 10;
		int APPLICATION = 20;
		int DEPLOYMENT = 30;
	}

	private final String nome;
	private final Class<?> tipo;
	private final String grupo;
	private final int precedencia;
	private final SingularAttribute<?, ?> keyAttribute;
	private final SingularAttribute<?, ?> labelAttribute;

	public ParametroDefinition(String grupo, String nome, Class<?> tipo) {
		this(grupo, nome, tipo, Precedencia.DEFAULT);
	}

	public ParametroDefinition(String grupo, String nome, Class<?> tipo, int precedencia) {
		this(grupo, nome, tipo, null, null, precedencia);
	}
	
	public ParametroDefinition(String grupo, String nome, Class<?> tipo, SingularAttribute<?, ?> keySingularAttribute,
			SingularAttribute<?, ?> labelSingularAttribute) {
		this(grupo, nome, tipo, keySingularAttribute, labelSingularAttribute, Precedencia.DEFAULT);
	}

	public ParametroDefinition(String grupo, String nome, Class<?> tipo, SingularAttribute<?, ?> code, SingularAttribute<?, ?> label,
			int precedencia) {
		this.nome = Objects.requireNonNull(nome);
		this.tipo = Objects.requireNonNull(tipo);
		this.grupo = Objects.requireNonNull(grupo);
		this.precedencia = Objects.requireNonNull(precedencia);
		this.keyAttribute = code;
		this.labelAttribute = label;
	}

	public SingularAttribute<?, ?> getKeyAttribute() {
		return keyAttribute;
	}

	public SingularAttribute<?, ?> getLabelAttribute() {
		return labelAttribute;
	}

	public String getNome() {
		return nome;
	}

	public Class<?> getTipo() {
		return tipo;
	}

	public String getGrupo() {
		return grupo;
	}

	public int getPrecedencia() {
		return precedencia;
	}

	@Override
	public int compareTo(ParametroDefinition o) {
		int result = nome.compareTo(o.nome);
		if (result == 0) {
			result = precedencia - o.precedencia;
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + precedencia;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParametroDefinition other = (ParametroDefinition) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (precedencia != other.precedencia)
			return false;
		return true;
	}

}
