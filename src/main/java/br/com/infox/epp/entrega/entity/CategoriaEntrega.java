package br.com.infox.epp.entrega.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name="tb_categoria_entrega")
public class CategoriaEntrega implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "GeneratorCategoriaEntrega", sequenceName = "sq_categoria_entrega")
    @GeneratedValue(generator = "GeneratorCategoriaEntrega", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_categoria_entrega", unique = true, nullable = false)
    private Long id;
    
    @NotNull
    @Size(max=LengthConstants.CODIGO_DOCUMENTO, min=1)
    @Column(name="cd_categoria_entrega")
    private String codigo;
    
    @NotNull
    @Size(max=LengthConstants.DESCRICAO_PADRAO, min=1)
    @Column(name="ds_categoria_entrega")
    private String descricao;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_categoria_entrega_pai")
    private CategoriaEntrega categoriaEntregaPai;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="categoriaEntrega")
    private Set<CategoriaEntregaItem> itemsFilhos;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="categoriaEntregaPai")
    private Set<CategoriaEntrega> categoriasFilhas;
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public CategoriaEntrega getCategoriaEntregaPai() {
        return categoriaEntregaPai;
    }

    public void setCategoriaEntregaPai(CategoriaEntrega categoriaEntregaPai) {
        this.categoriaEntregaPai = categoriaEntregaPai;
    }

    public Long getId() {
        return id;
    }

    public Set<CategoriaEntregaItem> getItemsFilhos() {
        return Collections.unmodifiableSet(itemsFilhos);
    }
    

    @Override
	public String toString() {
		return descricao;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
        return result;
    }

    public Set<CategoriaEntrega> getCategoriasFilhas() {
        return Collections.unmodifiableSet(categoriasFilhas);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoriaEntrega other = (CategoriaEntrega) obj;
        if (codigo == null) {
            if (other.codigo != null)
                return false;
        } else if (!codigo.equals(other.codigo))
            return false;
        return true;
    }

}
