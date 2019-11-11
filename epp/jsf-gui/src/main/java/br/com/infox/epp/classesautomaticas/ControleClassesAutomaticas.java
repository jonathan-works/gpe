package br.com.infox.epp.classesautomaticas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_controle_classes_automatics")
public class ControleClassesAutomaticas {
	
	@Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "ControleClassesAutomaticasGenerator", sequenceName = "sq_controle_classes_automatics")
    @GeneratedValue(generator = "ControleClassesAutomaticasGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_controle_classes_automatics", unique = true, nullable = false)
    private Long id;
	
	@NotNull
	@Column(name = "nm_classe_automatica", nullable = false)
	private String nomeClasse;
	
	@NotNull
	@Column(name = "in_executar", nullable = false)
	private boolean executar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeClasse() {
		return nomeClasse;
	}

	public void setNomeClasse(String nomeClasse) {
		this.nomeClasse = nomeClasse;
	}

	public boolean isExecutar() {
		return executar;
	}

	public void setExecutar(boolean executar) {
		this.executar = executar;
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
		if (!(obj instanceof ControleClassesAutomaticas))
			return false;
		ControleClassesAutomaticas other = (ControleClassesAutomaticas) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else {
			return getId().equals(other.getId());
		}
		return false;
	}
}
