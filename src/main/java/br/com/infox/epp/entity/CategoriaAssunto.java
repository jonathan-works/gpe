package br.com.infox.epp.entity;

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

import br.com.infox.epp.query.CategoriaAssuntoQuery;
import br.com.infox.ibpm.entity.Assunto;

@Entity
@Table(name=CategoriaAssunto.TABLE_NAME, schema="public",
	   uniqueConstraints={
			@UniqueConstraint(columnNames={"id_categoria", "id_assunto"})
		})
@NamedQueries(value={
		@NamedQuery(name=CategoriaAssuntoQuery.LIST_BY_CATEGORIA,
				    query=CategoriaAssuntoQuery.LIST_BY_CATEGORIA_QUERY)
	})
public class CategoriaAssunto {

	public static final String TABLE_NAME = "tb_categoria_assunto";
	
	private int idCategoriaAssunto;
	private Categoria categoria;
	private Assunto assunto;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_categoria_assunto")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_categoria_assunto", unique = true, nullable = false)
	public int getIdCategoriaAssunto() {
		return idCategoriaAssunto;
	}
	
	public void setIdCategoriaAssunto(int idCategoriaAssunto) {
		this.idCategoriaAssunto = idCategoriaAssunto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assunto", nullable=false)
	@NotNull
	public Assunto getAssunto() {
		return assunto;
	}
	
	public void setAssunto(Assunto assunto) {
		this.assunto = assunto;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria", nullable=false)
	@NotNull
	public Categoria getCategoria() {
		return categoria;
	}
	
}