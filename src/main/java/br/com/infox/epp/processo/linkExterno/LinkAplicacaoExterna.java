package br.com.infox.epp.processo.linkExterno;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_link_applicacao")
public class LinkAplicacaoExterna implements Serializable {

    private static final String GENERATOR = "LinkApplicacaoExternaGenerator";

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR, sequenceName = "sq_link_aplicacao")
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_link_aplicacao", unique = true, nullable = false) 
    private Integer id;

    @NotNull 
    @Column(name = "ds_link_aplicacao", nullable = false, unique = false) 
    private String descricao;

    @NotNull 
    @Column(name = "ds_url", nullable = false, unique = false) 
    private String url;

    @NotNull 
    @Column(name = "cd_link_aplicacao", nullable = false, unique = true) 
    private String codigo;

    @NotNull 
    @Column(name = "in_ativo", nullable = false, unique = false) 
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUrl() {
        return url;
    }

    public String getCodigo() {
        return codigo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
        if (getClass() != obj.getClass())
            return false;
        LinkAplicacaoExterna other = (LinkAplicacaoExterna) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LinkAplicacaoExterna [descricao=" + descricao + ", url=" + url + ", codigo=" + codigo + "]";
    }

    
    
}
