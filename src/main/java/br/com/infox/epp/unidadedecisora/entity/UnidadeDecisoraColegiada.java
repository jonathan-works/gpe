package br.com.infox.epp.unidadedecisora.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO_DOBRO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.FIND_ALL_ATIVO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.FIND_ALL_ATIVO_QUERY;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.*;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.FIND_UDC_BY_USUARIO_ID_QUERY;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_EXISTE_UDC_BY_LOCALIZACAO_QUERY;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_UDC_BY_USUARIO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_UDC_BY_USUARIO_QUERY;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.Localizacao;

@Entity
@Table(name = UnidadeDecisoraColegiada.TABLE_NAME)
@NamedQueries(value={
    @NamedQuery(name=SEARCH_UDC_BY_USUARIO, query=SEARCH_UDC_BY_USUARIO_QUERY),
        @NamedQuery(name=FIND_UDC_BY_USUARIO_ID, query=FIND_UDC_BY_USUARIO_ID_QUERY),
    @NamedQuery(name=SEARCH_EXISTE_UDC_BY_LOCALIZACAO, query=SEARCH_EXISTE_UDC_BY_LOCALIZACAO_QUERY),
    @NamedQuery(name=FIND_ALL_ATIVO, query=FIND_ALL_ATIVO_QUERY),
    @NamedQuery(name=FIND_UDC_BY_CODIGO_LOCALIZACAO, query=FIND_UDC_BY_CODIGO_LOCALIZACAO_QUERY)
})
public class UnidadeDecisoraColegiada implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_uni_decisora_colegiada";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name="UnidadeDecisoraColegiadaGenerator", sequenceName="sq_uni_decisora_colegiada")
    @GeneratedValue(generator = "UnidadeDecisoraColegiadaGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name="id_uni_decisora_colegiada", unique = true, nullable = false)
    private Integer idUnidadeDecisoraColegiada;

    @NotNull
    @Size(min = LengthConstants.FLAG, max = DESCRICAO_PADRAO_DOBRO)
    @Column(name = "ds_uni_decisora_colegiada", nullable = false, unique=true)
    private String nome;

    @NotNull
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_localizacao", nullable=false)
    private Localizacao localizacao;

    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH, mappedBy="unidadeDecisoraColegiada")
    @OrderBy("unidadeDecisoraMonocratica ASC")
    private List<UnidadeDecisoraColegiadaMonocratica> unidadeDecisoraColegiadaMonocraticaList = new ArrayList<>();

    public Integer getIdUnidadeDecisoraColegiada() {
        return idUnidadeDecisoraColegiada;
    }

    public void setIdUnidadeDecisoraColegiada(Integer idUnidadeDecisoraColegiada) {
        this.idUnidadeDecisoraColegiada = idUnidadeDecisoraColegiada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<UnidadeDecisoraColegiadaMonocratica> getUnidadeDecisoraColegiadaMonocraticaList() {
        return unidadeDecisoraColegiadaMonocraticaList;
    }

    public void setUnidadeDecisoraColegiadaMonocraticaList(
            List<UnidadeDecisoraColegiadaMonocratica> unidadeDecisoraColegiadaMonocraticaList) {
        this.unidadeDecisoraColegiadaMonocraticaList = unidadeDecisoraColegiadaMonocraticaList;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @Transient
    public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocraticaList(){
        List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaList = new ArrayList<>();
        for (UnidadeDecisoraColegiadaMonocratica colegiadaMonocratica : getUnidadeDecisoraColegiadaMonocraticaList()){
            unidadeDecisoraMonocraticaList.add(colegiadaMonocratica.getUnidadeDecisoraMonocratica());
        }
        return unidadeDecisoraMonocraticaList;
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
                + ((getIdUnidadeDecisoraColegiada() == null) ? 0
                        : getIdUnidadeDecisoraColegiada().hashCode());
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
        if (!(obj instanceof UnidadeDecisoraColegiada)) {
            return false;
        }
        UnidadeDecisoraColegiada other = (UnidadeDecisoraColegiada) obj;
        if (getIdUnidadeDecisoraColegiada() == null) {
            if (other.getIdUnidadeDecisoraColegiada() != null) {
                return false;
            }
        } else if (!getIdUnidadeDecisoraColegiada()
                .equals(other.getIdUnidadeDecisoraColegiada())) {
            return false;
        }
        return true;
    }

}
