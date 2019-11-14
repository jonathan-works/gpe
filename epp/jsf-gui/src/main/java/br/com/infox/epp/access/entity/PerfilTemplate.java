package br.com.infox.epp.access.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.GET_BY_DESCRICAO;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.GET_BY_DESCRICAO_QUERY;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.GET_BY_LOCALIZACAO_PAI_DESCRICAO;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.GET_BY_LOCALIZACAO_PAI_DESCRICAO_QUERY;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.GET_BY_LOCALIZACAO_PAPEL;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.GET_BY_LOCALIZACAO_PAPEL_QUERY;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.LIST_PERFIS_DENTRO_DE_ESTRUTURA;
import static br.com.infox.epp.access.query.PerfilTemplateQuery.LIST_PERFIS_DENTRO_DE_ESTRUTURA_QUERY;
import static java.text.MessageFormat.format;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table(name = PerfilTemplate.TABLE_NAME)
@NamedQueries({
	@NamedQuery(name=GET_BY_LOCALIZACAO_PAPEL,query=GET_BY_LOCALIZACAO_PAPEL_QUERY),
	@NamedQuery(name=LIST_PERFIS_DENTRO_DE_ESTRUTURA, query=LIST_PERFIS_DENTRO_DE_ESTRUTURA_QUERY),
	@NamedQuery(name=GET_BY_DESCRICAO, query=GET_BY_DESCRICAO_QUERY),
	@NamedQuery(name=GET_BY_LOCALIZACAO_PAI_DESCRICAO, query=GET_BY_LOCALIZACAO_PAI_DESCRICAO_QUERY)
})
public class PerfilTemplate implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_perfil_template";
	
	@Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = "sq_perfil_template")
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_perfil_template", unique = true, nullable = false)
	private Integer id;
	
	@NotNull
    @Column(name="cd_perfil_template", length = 50, nullable = false)
    private String codigo;
	
	@NotNull
	@Column(name="ds_perfil_template", length=DESCRICAO_PADRAO, nullable=false)
    private String descricao;
	
	@ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_localizacao")
    private Localizacao localizacao;
	
	@NotNull
	@ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_papel", nullable = false)
    private Papel papel;
	
	@NotNull
	@Column(name = ATIVO, nullable = false)
    private Boolean ativo;
    
    public PerfilTemplate() {
    }
    
    public PerfilTemplate(Localizacao localizacao, Papel papel) {
        this.localizacao = localizacao;
        this.papel = papel;
        this.descricao = format("{0} / {1}", localizacao, papel);
        this.ativo = Boolean.TRUE;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
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

    public Localizacao getLocalizacao() {
        return localizacao;
    }
    
    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
    
    public Papel getPapel() {
        return papel;
    }
    
    public void setPapel(Papel papel) {
        this.papel = papel;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public String toString() {
        return getDescricao();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAtivo() == null) ? 0 : getAtivo().hashCode());
        result = prime * result
                + ((getDescricao() == null) ? 0 : getDescricao().hashCode());
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result
                + ((getLocalizacao() == null) ? 0 : getLocalizacao().hashCode());
        result = prime * result + ((getPapel() == null) ? 0 : getPapel().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof PerfilTemplate)) return false;
        PerfilTemplate other = (PerfilTemplate) obj;
        if (getAtivo() == null) {
            if (other.getAtivo() != null) return false;
        } else if (!getAtivo().equals(other.getAtivo())) return false;
        if (getDescricao() == null) {
            if (other.getDescricao() != null) return false;
        } else if (!getDescricao().equals(other.getDescricao())) return false;
        if (getId() == null) {
            if (other.getId() != null) return false;
        } else if (!getId().equals(other.getId())) return false;
        if (getLocalizacao() == null) {
            if (other.getLocalizacao() != null) return false;
        } else if (!getLocalizacao().equals(other.getLocalizacao())) return false;
        if (getPapel() == null) {
            if (other.getPapel() != null) return false;
        } else if (!getPapel().equals(other.getPapel())) return false;
        return true;
    }
    
    @Transient
    public String getCaminhoIfLocalizacaoNotNull() {
        //Método utilizado no exportar xls
        if (getLocalizacao() == null) {
            return "";
        } else {
            return getLocalizacao().getCaminhoCompletoFormatado();
        }
    }
}
