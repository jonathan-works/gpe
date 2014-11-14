package br.com.infox.epp.processo.metadado.entity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
	private MetadadoProcessoType nome;
	
	@NotNull
	@Column(name = "vl_metadado_processo", nullable = false)
	private String valor;
	
	@NotNull
	@Column(name = "ds_tipo", nullable = false)
	private Class<?> tipo;
	
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

	public MetadadoProcessoType getNome() {
		return nome;
	}

	public void setNome(MetadadoProcessoType nome) {
		this.nome = nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Class<?> getTipo() {
		return tipo;
	}

	public void setTipo(Class<?> tipo) {
		this.tipo = tipo;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	@SuppressWarnings("unchecked")
	public <E> E getValue() {
		if (value != null) {
			if (EntityUtil.isEntity(getTipo())) {
				EntityManager entityManager = ComponentUtil.getComponent("entityManager");
				value = (E) entityManager.find(getTipo(), getValor());
			} else {
				try {
					Constructor<?> constructor = getTipo().getConstructor(String.class);
					value = constructor.newInstance(getValor());
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
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
