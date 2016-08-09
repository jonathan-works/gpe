package br.com.infox.epp.access.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO_DOBRO;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.LocalizacaoQuery.CAMINHO_COMPLETO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.DESCRICAO_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ESTRUTURA_FILHO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ESTRUTURA_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_LOCALIZACAO_ANCESTOR;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_LOCALIZACAO_ANCESTOR_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LIST_BY_NOME_ESTRUTURA_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LIST_BY_NOME_ESTRUTURA_PAI_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_ATTRIBUTE;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_BY_CODIGO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_BY_CODIGO_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_BY_NOME;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_BY_NOME_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_DENTRO_ESTRUTURA;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_DENTRO_ESTRUTURA_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_FORA_ESTRUTURA_BY_NOME;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_FORA_ESTRUTURA_BY_NOME_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_PAI_ATTRIBUTE;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_ESTRUTURA_FILHO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_ESTRUTURA_FILHO_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_IDS;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_IDS_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.SEQUENCE_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.TABLE_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.TWITTER;
import static br.com.infox.epp.access.query.LocalizacaoQuery.USOS_DA_HIERARQUIA_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.USOS_DA_HIERARQUIA_LOCALIZACAO_QUERY;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
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
import br.com.infox.core.persistence.Recursive;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Entity
@Table(name = TABLE_LOCALIZACAO)
@NamedQueries(value = {
        @NamedQuery(name = LIST_BY_NOME_ESTRUTURA_PAI, query = LIST_BY_NOME_ESTRUTURA_PAI_QUERY),
        @NamedQuery(name = LOCALIZACOES_BY_IDS, query = LOCALIZACOES_BY_IDS_QUERY),
        @NamedQuery(name = IS_LOCALIZACAO_ANCESTOR, query = IS_LOCALIZACAO_ANCESTOR_QUERY),
        @NamedQuery(name = LOCALIZACAO_DENTRO_ESTRUTURA, query = LOCALIZACAO_DENTRO_ESTRUTURA_QUERY),
        @NamedQuery(name = LOCALIZACAO_FORA_ESTRUTURA_BY_NOME, query = LOCALIZACAO_FORA_ESTRUTURA_BY_NOME_QUERY),
        @NamedQuery(name = LOCALIZACAO_BY_CODIGO, query = LOCALIZACAO_BY_CODIGO_QUERY),
        @NamedQuery(name = LOCALIZACOES_BY_ESTRUTURA_FILHO, query = LOCALIZACOES_BY_ESTRUTURA_FILHO_QUERY),
        @NamedQuery(name = LOCALIZACAO_BY_NOME, query = LOCALIZACAO_BY_NOME_QUERY)
})
@NamedNativeQueries({
    @NamedNativeQuery(name = USOS_DA_HIERARQUIA_LOCALIZACAO, query = USOS_DA_HIERARQUIA_LOCALIZACAO_QUERY)
})
public class Localizacao implements Serializable, Recursive<Localizacao> {

    private static final long serialVersionUID = 1L;

    private Integer idLocalizacao;
    private String codigo;
    private String localizacao;
    private Boolean ativo;
    private Localizacao localizacaoPai;
    private Estrutura estruturaFilho;
    private Estrutura estruturaPai;

    private List<LocalizacaoTurno> localizacaoTurnoList = new ArrayList<>(0);
    private List<Localizacao> localizacaoList = new ArrayList<>(0);

    private List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocratica = new ArrayList<>(1);

    private List<UnidadeDecisoraColegiada> unidadeDecisoraColegiada = new ArrayList<>(1);

    private String caminhoCompleto;
    private Boolean temContaTwitter;

    private String caminhoCompletoFormatado;

    public Localizacao() {
        temContaTwitter = Boolean.FALSE;
    }

    public Localizacao(final String localizacao, final Boolean ativo) {
        this();
        this.localizacao = localizacao;
        this.ativo = ativo;
        this.localizacaoPai = null;
        this.estruturaFilho = null;
    }

    public Localizacao(final String localizacao, final Boolean ativo, final Localizacao localizacaoPai) {
        this();
        this.localizacao = localizacao;
        this.ativo = ativo;
        this.localizacaoPai = localizacaoPai;
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_LOCALIZACAO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_LOCALIZACAO, unique = true, nullable = false)
    public Integer getIdLocalizacao() {
        return this.idLocalizacao;
    }

    public void setIdLocalizacao(Integer idLocalizacao) {
        this.idLocalizacao = idLocalizacao;
    }

    @Column(name = "cd_localizacao", nullable=false, length=LengthConstants.DESCRICAO_ENTIDADE)
    @Size(max=LengthConstants.DESCRICAO_ENTIDADE)
    @NotNull
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Column(name = DESCRICAO_LOCALIZACAO, nullable = false, length = DESCRICAO_PADRAO_DOBRO)
    @Size(min = LengthConstants.FLAG, max = DESCRICAO_PADRAO_DOBRO)
    @NotNull
    public String getLocalizacao() {
        return this.localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Column(name = ATIVO, nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = LOCALIZACAO_PAI)
    public Localizacao getLocalizacaoPai() {
        return this.localizacaoPai;
    }

    public void setLocalizacaoPai(Localizacao localizacaoPai) {
        this.localizacaoPai = localizacaoPai;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = LOCALIZACAO_PAI_ATTRIBUTE)
    @OrderBy(LOCALIZACAO_ATTRIBUTE)
    public List<Localizacao> getLocalizacaoList() {
        return this.localizacaoList;
    }

    public void setLocalizacaoList(List<Localizacao> localizacaoList) {
        this.localizacaoList = localizacaoList;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ESTRUTURA_FILHO)
    public Estrutura getEstruturaFilho() {
        return estruturaFilho;
    }

    public void setEstruturaFilho(Estrutura estruturaFilho) {
        this.estruturaFilho = estruturaFilho;
        this.caminhoCompletoFormatado = null;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ESTRUTURA_PAI)
    public Estrutura getEstruturaPai() {
        return estruturaPai;
    }

    public void setEstruturaPai(Estrutura estruturaPai) {
        this.estruturaPai = estruturaPai;
    }

    @Column(name = TWITTER, nullable = false)
    public Boolean getTemContaTwitter() {
        return temContaTwitter;
    }

    public void setTemContaTwitter(Boolean temContaTwitter) {
        this.temContaTwitter = temContaTwitter;
    }

    @Column(name = CAMINHO_COMPLETO)
    public String getCaminhoCompleto() {
        return caminhoCompleto;
    }

    public void setCaminhoCompleto(String caminhoCompleto) {
        this.caminhoCompleto = caminhoCompleto;
        this.caminhoCompletoFormatado = null;
    }

    @Override
    public String toString() {
        return localizacao;
    }

    public void setLocalizacaoTurnoList(
            List<LocalizacaoTurno> localizacaoTurnoList) {
        this.localizacaoTurnoList = localizacaoTurnoList;
    }

    @OneToMany(fetch = LAZY, mappedBy = LOCALIZACAO_ATTRIBUTE)
    public List<LocalizacaoTurno> getLocalizacaoTurnoList() {
        return localizacaoTurnoList;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="localizacao")
    public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocratica() {
        return unidadeDecisoraMonocratica;
    }

    public void setUnidadeDecisoraMonocratica(List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocratica) {
        this.unidadeDecisoraMonocratica = unidadeDecisoraMonocratica;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="localizacao")
    public List<UnidadeDecisoraColegiada> getUnidadeDecisoraColegiada() {
        return unidadeDecisoraColegiada;
    }

    public void setUnidadeDecisoraColegiada(List<UnidadeDecisoraColegiada> unidadeDecisoraColegiada) {
        this.unidadeDecisoraColegiada = unidadeDecisoraColegiada;
    }

    @Override
    @Transient
    public Localizacao getParent() {
        return this.getLocalizacaoPai();
    }

    @Override
    public void setParent(Localizacao parent) {
        this.setLocalizacaoPai(parent);
    }

    @Override
    @Transient
    public String getHierarchicalPath() {
        return this.getCaminhoCompleto();
    }

    @Override
    public void setHierarchicalPath(String path) {
        this.setCaminhoCompleto(path);
    }

    @Override
    @Transient
    public String getPathDescriptor() {
        return this.getLocalizacao();
    }

    @Override
    public void setPathDescriptor(String pathDescriptor) {
        this.setLocalizacao(pathDescriptor);
    }

    @Override
    @Transient
    public List<Localizacao> getChildList() {
        return this.getLocalizacaoList();
    }

    @Override
    public void setChildList(List<Localizacao> childList) {
        this.setLocalizacaoList(childList);
    }

    @Transient
    public String getCaminhoCompletoFormatado() {
        if (caminhoCompletoFormatado == null) {
            StringBuilder sb = new StringBuilder();
            if (this.getEstruturaPai() != null) {
                sb.append(this.getEstruturaPai().getNome());
                sb.append('|');
            }
            sb.append(this.getCaminhoCompleto());
            if (sb.charAt(sb.length() -1) == '|') {
                sb.deleteCharAt(sb.length() - 1);
            }
            int index = sb.indexOf("|", 0);
            while (index != -1) {
                sb.replace(index, index + 1, " / ");
                index = sb.indexOf("|", index);
            }
            if (this.getEstruturaFilho() != null) {
                sb.append(": ");
                sb.append(this.getEstruturaFilho().getNome());
            }
            caminhoCompletoFormatado = sb.toString();
        }
        return caminhoCompletoFormatado;
    }

    public void setCaminhoCompletoFormatado(String caminhoCompletoFormatado) {
        this.caminhoCompletoFormatado = caminhoCompletoFormatado;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCaminhoCompleto() == null) ? 0 : getCaminhoCompleto().hashCode());
        result = prime * result + ((getIdLocalizacao() == null) ? 0 : getIdLocalizacao().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Localizacao))
            return false;
        Localizacao other = (Localizacao) obj;
        if (getCaminhoCompleto() == null) {
            if (other.getCaminhoCompleto() != null)
                return false;
        } else if (!getCaminhoCompleto().equals(other.getCaminhoCompleto()))
            return false;
        if (getIdLocalizacao() == null) {
            if (other.getIdLocalizacao() != null)
                return false;
        } else if (!getIdLocalizacao().equals(other.getIdLocalizacao()))
            return false;
        return true;
    }

    @Transient
    public boolean isDecisoraMonocratica() {
        return !getUnidadeDecisoraMonocratica().isEmpty() && getUnidadeDecisoraMonocratica().get(0).getAtivo();
    }

    @Transient
    public List<UnidadeDecisoraColegiada> getColegiadaDaMonocraticaList() {
        if (!getUnidadeDecisoraMonocratica().isEmpty()) {
            return getUnidadeDecisoraMonocratica().get(0).getUnidadeDecisoraColegiadaList();
        } else {
            return new ArrayList<>();
        }
    }

    @Transient
    public boolean isDecisoraColegiada() {
        return !getUnidadeDecisoraColegiada().isEmpty() && getUnidadeDecisoraColegiada().get(0).getAtivo();
    }

}
