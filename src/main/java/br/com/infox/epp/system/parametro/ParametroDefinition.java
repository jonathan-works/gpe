package br.com.infox.epp.system.parametro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.metamodel.SingularAttribute;

import br.com.infox.epp.FieldType;
import br.com.infox.epp.Filter;

public class ParametroDefinition<T> implements Comparable<ParametroDefinition<T>> {

	public static interface Precedencia {
		int DEFAULT = 0;
		int FRAMEWORK = 10;
		int APPLICATION = 20;
		int DEPLOYMENT = 30;
	}

	private final String nome;
	private final FieldType tipo;
	private final String grupo;
	private final int precedencia;
	private final SingularAttribute<T, ?> keyAttribute;
	private final SingularAttribute<T, ?> labelAttribute;
	private final List<Filter<T,?>> filters;

	public ParametroDefinition(String grupo, String nome, FieldType tipo) {
		this(grupo, nome, tipo, Precedencia.DEFAULT);
	}

	public ParametroDefinition(String grupo, String nome, FieldType tipo, int precedencia) {
		this(grupo, nome, tipo, null, null, precedencia);
	}
	
	public ParametroDefinition(String grupo, String nome, FieldType tipo, SingularAttribute<T, ?> keySingularAttribute,
			SingularAttribute<T, ?> labelSingularAttribute) {
		this(grupo, nome, tipo, keySingularAttribute, labelSingularAttribute, Precedencia.DEFAULT);
	}

	public ParametroDefinition(String grupo, String nome, FieldType tipo, SingularAttribute<T, ?> keyAttribute, SingularAttribute<T, ?> labelAttribute, int precedencia) {
		this.nome = Objects.requireNonNull(nome);
		this.tipo = Objects.requireNonNull(tipo);
		this.grupo = Objects.requireNonNull(grupo);
		this.precedencia = Objects.requireNonNull(precedencia);
		this.keyAttribute = keyAttribute;
		this.labelAttribute = labelAttribute;
		this.filters = new ArrayList<>();
	}

	public SingularAttribute<T, ?> getKeyAttribute() {
		return keyAttribute;
	}

	public SingularAttribute<T, ?> getLabelAttribute() {
		return labelAttribute;
	}

	public String getNome() {
		return nome;
	}

	public FieldType getTipo() {
		return tipo;
	}

	public String getGrupo() {
		return grupo;
	}

	public int getPrecedencia() {
		return precedencia;
	}

	@Override
	public int compareTo(ParametroDefinition<T> o) {
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParametroDefinition<T> other = (ParametroDefinition<T>) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (precedencia != other.precedencia)
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public ParametroDefinition<T> addFilters(Filter<T,?>... filters){
		for (int i = 0; i < filters.length; i++) {
			Filter<T, ?> filter = filters[i];
			this.filters.add(filter);
		}
		return this;
	}
	
	public ParametroDefinition<T> addFilter(Filter<T,?> filter){
		filters.add(filter);
		return this;
	}
	
	public List<Filter<T,?>> getFilters() {
		return Collections.unmodifiableList(filters);
	}

}
