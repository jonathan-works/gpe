package br.com.infox.epp.processo.metadado.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.com.infox.core.util.EntityUtil;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.auditoria.MetadadoProcessoListener;
import br.com.infox.epp.processo.metadado.query.MetadadoProcessoQuery;

@Entity
@Table(name = MetadadoProcesso.TABLE_NAME)
@NamedQueries(value = {
		@NamedQuery(name = MetadadoProcessoQuery.LIST_METADADO_PROCESSO_BY_TYPE, 
				query = MetadadoProcessoQuery.LIST_METADADO_PROCESSO_BY_TYPE_QUERY),
		@NamedQuery(name = MetadadoProcessoQuery.LIST_METADADO_PROCESSO_VISIVEL_BY_PROCESSO, 
					 query = MetadadoProcessoQuery.LIST_METADADO_PROCESSO_VISIVEL_BY_PROCESSO_QUERY),
		@NamedQuery(name = MetadadoProcessoQuery.GET_METADADO, query = MetadadoProcessoQuery.GET_METADADO_QUERY),
		@NamedQuery(name = MetadadoProcessoQuery.REMOVER_METADADO, query = MetadadoProcessoQuery.REMOVER_METADADO_QUERY)})
@EntityListeners(MetadadoProcessoListener.class)
public class MetadadoProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_metadado_processo";

	@Id
	@SequenceGenerator(initialValue = 1, allocationSize = 1, name = "GeneratorMetadadoProcesso", sequenceName = "sq_metadado_processo")
	@GeneratedValue(generator = "GeneratorMetadadoProcesso", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_metadado_processo", unique = true, nullable = false)
	private Long id;

	@NotNull
	@Column(name = "nm_metadado_processo", nullable = false)
	private String metadadoType;

	@NotNull
	@Column(name = "vl_metadado_processo", nullable = false)
	private String valor;

	@NotNull
	@Column(name = "ds_tipo", nullable = false)
	private Class<?> classType;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo", nullable = false)
	private Processo processo;

	@NotNull
	@Column(name = "in_visivel", nullable = true)
	private Boolean visivel = Boolean.TRUE;

	@Transient
	private Object value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMetadadoType() {
		return metadadoType;
	}

	public void setMetadadoType(String metadadoType) {
		this.metadadoType = metadadoType;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		limparValue(valor);
		this.valor = valor;
	}

	public Class<?> getClassType() {
		return classType;
	}

	public void setClassType(Class<?> classType) {
		this.classType = classType;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}

	@SuppressWarnings("unchecked")
	public <E> E getValue() {
		if (value == null) {
			if (EntityUtil.isEntity(getClassType())) {
				EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
				Class<?> idClass = EntityUtil.getId(getClassType()).getPropertyType();
				Object id = ReflectionsUtil.newInstance(idClass, String.class, getValor());
				value = (E) entityManager.find(getClassType(), id);
			} else if (getClassType() == Date.class) {
				try {
					value = new SimpleDateFormat(DATE_PATTERN).parse(getValor());
				} catch (ParseException e) {
					throw new RuntimeException("Erro ao converter data", e);
				}
			} else if (getClassType() != String.class) {
				value = ReflectionsUtil.newInstance(getClassType(), String.class, getValor());
			} else {
				value = getValor();
			}
		}
		return (E) value;
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MetadadoProcesso))
			return false;
		MetadadoProcesso other = (MetadadoProcesso) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	private void limparValue(String novoValor) {
		if (novoValor != null && !novoValor.equals(getValor())) {
			this.value = null;
		}
	}

	public static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";
}
