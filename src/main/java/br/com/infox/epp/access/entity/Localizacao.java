package br.com.infox.epp.access.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.LocalizacaoQuery.CAMINHO_COMPLETO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.DESCRICAO_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ESTRUTURA_FILHO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ESTRUTURA_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_LOCALIZACAO_ANCESTOR;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_LOCALIZACAO_ANCESTOR_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_ATTRIBUTE;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_PAI_ATTRIBUTE;
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

import br.com.infox.core.persistence.Recursive;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;

@Entity
@Table(name = TABLE_LOCALIZACAO)
@NamedQueries(value = {
    @NamedQuery(name = LOCALIZACOES_BY_IDS, query = LOCALIZACOES_BY_IDS_QUERY),
    @NamedQuery(name = IS_LOCALIZACAO_ANCESTOR, query = IS_LOCALIZACAO_ANCESTOR_QUERY)
})
@NamedNativeQueries({
    @NamedNativeQuery(name = USOS_DA_HIERARQUIA_LOCALIZACAO, query = USOS_DA_HIERARQUIA_LOCALIZACAO_QUERY)
})
public class Localizacao implements Serializable, Recursive<Localizacao> {

    private static final long serialVersionUID = 1L;

    private Integer idLocalizacao;
    private String localizacao;
    private Boolean ativo;
    private Localizacao localizacaoPai;
    private Estrutura estruturaFilho;
    private Estrutura estruturaPai;
    
    private List<LocalizacaoTurno> localizacaoTurnoList = new ArrayList<>(0);
    private List<Localizacao> localizacaoList = new ArrayList<>(0);

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

    @Column(name = DESCRICAO_LOCALIZACAO, nullable = false, length = DESCRICAO_PADRAO)
    @Size(max = DESCRICAO_PADRAO)
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Localizacao)) {
            return false;
        }
        Localizacao other = (Localizacao) obj;
        if (getIdLocalizacao() != other.getIdLocalizacao()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (this.idLocalizacao == null ? 0 : this.idLocalizacao);
        return result;
    }

    public void setLocalizacaoTurnoList(
            List<LocalizacaoTurno> localizacaoTurnoList) {
        this.localizacaoTurnoList = localizacaoTurnoList;
    }

    @OneToMany(fetch = LAZY, mappedBy = LOCALIZACAO_ATTRIBUTE)
    public List<LocalizacaoTurno> getLocalizacaoTurnoList() {
        return localizacaoTurnoList;
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
			StringBuilder sb = new StringBuilder(this.getCaminhoCompleto());
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
}
