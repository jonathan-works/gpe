package br.com.infox.epp.unidadedecisora.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO_DOBRO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.FIND_UDM_BY_CODIGO_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.FIND_UDM_BY_CODIGO_LOCALIZACAO_QUERY;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_BY_UNIDADE_DECISORA_COLEGIADA;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_BY_UNIDADE_DECISORA_COLEGIADA_QUERY;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_EXISTE_UDM_BY_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_EXISTE_UDM_BY_LOCALIZACAO_QUERY;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_UDM_BY_USUARIO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_UDM_BY_USUARIO_QUERY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Entity
@Table(name = UnidadeDecisoraMonocratica.TABLE_NAME)
@NamedQueries(value={ @NamedQuery(name=SEARCH_BY_UNIDADE_DECISORA_COLEGIADA, query=SEARCH_BY_UNIDADE_DECISORA_COLEGIADA_QUERY),
        @NamedQuery(name=SEARCH_UDM_BY_USUARIO, query=SEARCH_UDM_BY_USUARIO_QUERY),
        @NamedQuery(name=SEARCH_EXISTE_UDM_BY_LOCALIZACAO, query=SEARCH_EXISTE_UDM_BY_LOCALIZACAO_QUERY),
        @NamedQuery(name=FIND_UDM_BY_CODIGO_LOCALIZACAO, query=FIND_UDM_BY_CODIGO_LOCALIZACAO_QUERY)})
public class UnidadeDecisoraMonocratica implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_uni_decisora_monocratica";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "UnidadeDecisoraMonocraticaGenerator", sequenceName="sq_uni_decisora_monocratica")
    @GeneratedValue(generator="UnidadeDecisoraMonocraticaGenerator", strategy=GenerationType.SEQUENCE)
    @Column(name = "id_uni_decisora_monocratica", unique=true, nullable=false)
    private Integer idUnidadeDecisoraMonocratica;

    @NotNull
    @Size(min = LengthConstants.FLAG, max = DESCRICAO_PADRAO_DOBRO)
    @Column(name="ds_uni_decisora_monocratica", nullable=false, unique=true)
    private String nome;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_localizacao", nullable=false)
    private Localizacao localizacao;

    @NotNull
    @Column(name="in_ativo", nullable = false)
    private Boolean ativo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_pessoa_fisica", nullable = true)
    private PessoaFisica chefeGabinete;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH, mappedBy="unidadeDecisoraMonocratica")
    @OrderBy("unidadeDecisoraColegiada ASC")
    private List<UnidadeDecisoraColegiadaMonocratica> unidadeDecisoraColegiadaMonocraticaList = new ArrayList<>();

    public Integer getIdUnidadeDecisoraMonocratica() {
        return idUnidadeDecisoraMonocratica;
    }

    public void setIdUnidadeDecisoraMonocratica(Integer idUnidadeDecisoraMonocratica) {
        this.idUnidadeDecisoraMonocratica = idUnidadeDecisoraMonocratica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public PessoaFisica getChefeGabinete() {
        return chefeGabinete;
    }

    public void setChefeGabinete(PessoaFisica chefeGabinete) {
        this.chefeGabinete = chefeGabinete;
    }

    public List<UnidadeDecisoraColegiadaMonocratica> getUnidadeDecisoraColegiadaMonocraticaList() {
        return unidadeDecisoraColegiadaMonocraticaList;
    }

    public void setUnidadeDecisoraColegiadaMonocraticaList(
            List<UnidadeDecisoraColegiadaMonocratica> unidadeDecisoraColegiadaMonocraticaList) {
        this.unidadeDecisoraColegiadaMonocraticaList = unidadeDecisoraColegiadaMonocraticaList;
    }

    @Transient
    public List<UnidadeDecisoraColegiada> getUnidadeDecisoraColegiadaList(){
        List<UnidadeDecisoraColegiada> unidadeDecisoraColegiadaList = new ArrayList<>();
        for (UnidadeDecisoraColegiadaMonocratica colegiadaMonocratica : getUnidadeDecisoraColegiadaMonocraticaList()){
            unidadeDecisoraColegiadaList.add(colegiadaMonocratica.getUnidadeDecisoraColegiada());
        }
        return unidadeDecisoraColegiadaList;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime
                * result)
                + ((getIdUnidadeDecisoraMonocratica() == null) ? 0
                        : getIdUnidadeDecisoraMonocratica().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UnidadeDecisoraMonocratica)) {
            return false;
        }
        UnidadeDecisoraMonocratica other = (UnidadeDecisoraMonocratica) obj;
        if (getIdUnidadeDecisoraMonocratica() == null) {
            if (other.getIdUnidadeDecisoraMonocratica() != null) {
                return false;
            }
        } else if (!getIdUnidadeDecisoraMonocratica()
                .equals(other.getIdUnidadeDecisoraMonocratica())) {
            return false;
        }
        return true;
    }

}
