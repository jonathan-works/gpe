package br.com.infox.epp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

import br.com.infox.epp.query.NaturezaLocalizacaoQuery;
import br.com.infox.ibpm.entity.Localizacao;

@Entity
@Table(name=NaturezaLocalizacao.TABLE_NAME, schema="public",
	   uniqueConstraints={
			@UniqueConstraint(columnNames={"id_natureza", "id_localizacao"})
		})
@NamedQueries(value={
		@NamedQuery(name=NaturezaLocalizacaoQuery.LIST_BY_NATUREZA,
					query=NaturezaLocalizacaoQuery.LIST_BY_NATUREZA_QUERY)
})
public class NaturezaLocalizacao implements Serializable {

	private static final long serialVersionUID = 1332489326033901417L;

	public static final String TABLE_NAME = "tb_natureza_localizacao";
	
	private int idNaturezaLocalizacao;
	private Natureza natureza;
	private Localizacao localizacao;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_natureza_localizacao")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_natureza_localizacao", unique = true, nullable = false)
	public int getIdNaturezaLocalizacao() {
		return idNaturezaLocalizacao;
	}
	
	public void setIdNaturezaLocalizacao(int idNaturezaLocalizacao) {
		this.idNaturezaLocalizacao = idNaturezaLocalizacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao", nullable=false)
	@NotNull
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public void setNatureza(Natureza natureza) {
		this.natureza = natureza;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_natureza", nullable=false)
	@NotNull
	public Natureza getNatureza() {
		return natureza;
	}
	
}