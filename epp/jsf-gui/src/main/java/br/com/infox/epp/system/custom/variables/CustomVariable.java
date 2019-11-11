package br.com.infox.epp.system.custom.variables;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tb_variavel_custom_sistema")
public class CustomVariable implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String SEQUENCE_GENERATOR = "CustomVariableGenerator";
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	
	@Id
	@SequenceGenerator(name = SEQUENCE_GENERATOR, sequenceName = "sq_variavel_custom_sistema", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = SEQUENCE_GENERATOR, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_variavel_custom_sistema", nullable = false, unique = true)
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 250)
	@Column(name = "nm_codigo", nullable = false, length = 250, unique = true)
	private String codigo;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tp_variavel", nullable = false)
	private TipoCustomVariableEnum tipo;
	
	@NotNull
	@Size(min = 1, max = 250)
	@Column(name = "ds_valor", nullable = false, length = 250)
	private String valor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public TipoCustomVariableEnum getTipo() {
		return tipo;
	}

	public void setTipo(TipoCustomVariableEnum tipo) {
		this.tipo = tipo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public Object getTypedValue() {
		try {
			switch (getTipo()) {
			case BOOLEAN:
				return new Boolean(getValor());
			case DATE:
				return new SimpleDateFormat(DATE_PATTERN).parse(getValor());
			case DOUBLE:
				NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
				return format.parse(getValor()).doubleValue();
			case LONG:
				return new Long(getValor());
			case STRING:
			case EL:
				return getValor();
			default:
				break;
			}
		} catch (Exception e) {
			//logar o erro
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof CustomVariable) {
			CustomVariable other = (CustomVariable) obj;
			if (getId() != null) {
				return getId().equals(other.getId());
			}
		}
		return false;
	}

}
