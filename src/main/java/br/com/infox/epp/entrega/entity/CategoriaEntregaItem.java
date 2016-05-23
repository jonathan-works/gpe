package br.com.infox.epp.entrega.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.Localizacao;

@Entity
@Table(name="tb_categoria_entrega_item")
public class CategoriaEntregaItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "GeneratorCategoriaEntregaItem", sequenceName = "sq_categoria_entrega_item")
    @GeneratedValue(generator = "GeneratorCategoriaEntregaItem", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_categoria_entrega_item", unique = true, nullable = false)
    private Integer id;
    
    @NotNull
    @Size(max=LengthConstants.CODIGO_DOCUMENTO)
    @Column(name="cd_categoria_entrega_item")
    private String codigo;
    
    @NotNull
    @Size(max=LengthConstants.DESCRICAO_PADRAO)
    @Column(name="ds_categoria_entrega_item")
    private String descricao;
    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_categoria_entrega")
    private CategoriaEntrega categoriaEntrega;
    
    @OneToMany(mappedBy="itemFilho", fetch=FetchType.LAZY)
    private Set<CategoriaItemRelacionamento> itensPais;
    
    @OneToMany(mappedBy="itemPai", fetch=FetchType.LAZY)
    private Set<CategoriaItemRelacionamento> itensFilhos;
    
    @JoinTable(name="tb_restricao_item_entrega", 
            joinColumns=@JoinColumn(name="id_categoria_entrega_item"), 
            inverseJoinColumns=@JoinColumn(name="id_localizacao"))
    @OneToMany(fetch=FetchType.LAZY)
    private List<Localizacao> restricoes;

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

    public CategoriaEntrega getCategoriaEntrega() {
        return categoriaEntrega;
    }

    public void setCategoriaEntrega(CategoriaEntrega categoriaEntrega) {
        this.categoriaEntrega = categoriaEntrega;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public Set<CategoriaItemRelacionamento> getItensPais() {
        return Collections.unmodifiableSet(itensPais);
    }

    public List<Localizacao> getRestricoes() {
        return restricoes;
    }
    public void setRestricoes(List<Localizacao> restricoes) {
        this.restricoes = restricoes;
    }
    public Set<CategoriaItemRelacionamento> getItensFilhos() {
        return Collections.unmodifiableSet(itensFilhos);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
        CategoriaEntregaItem other = (CategoriaEntregaItem) obj;
        if (codigo == null) {
            if (other.codigo != null)
                return false;
        } else if (!codigo.equals(other.codigo))
            return false;
        return true;
    }
    
    
}
