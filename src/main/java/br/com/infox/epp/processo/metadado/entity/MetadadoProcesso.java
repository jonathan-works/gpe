package br.com.infox.epp.processo.metadado.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.com.infox.core.util.EntityUtil;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.type.MetadadoProcessoType;
import br.com.infox.seam.util.ComponentUtil;

@Entity
@Table(name = MetadadoProcesso.TABLE_NAME)
public class MetadadoProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_metadado_processo";
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="GeneratorMetadadoProcesso", sequenceName="sq_metadado_processo")
	@GeneratedValue(generator = "GeneratorMetadadoProcesso", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_metadado_processo", unique = true, nullable = false)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "nm_metadado_processo", nullable = false)
	private MetadadoProcessoType metadadoType;
	
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
	
	@Transient
	private Object value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MetadadoProcessoType getMetadadoType() {
		return metadadoType;
	}

	public void setMetadadoType(MetadadoProcessoType metadadoType) {
		this.metadadoType = metadadoType;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
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
	
	@SuppressWarnings("unchecked")
	public <E> E getValue() {
		if (value == null) {
			if (EntityUtil.isEntity(getClassType())) {
				EntityManager entityManager = ComponentUtil.getComponent("entityManager");
				Class<?> idClass = EntityUtil.getId(getClassType()).getPropertyType();
				Object id = ReflectionsUtil.newInstance(idClass, String.class, getValor());
				value = (E) entityManager.find(getClassType(), id);
			} else if (getClassType() != String.class) {
				value = ReflectionsUtil.newInstance(getClassType(), String.class, getValor());
			} else {
				value = getValor();
			}
		}
		return (E) value;
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
	
}
