package br.com.infox.epp.painel.caixa;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.painel.caixa.CaixaQuery.CAIXA_ATTRIBUTE;
import static br.com.infox.epp.painel.caixa.CaixaQuery.DESCRICAO_CAIXA;
import static br.com.infox.epp.painel.caixa.CaixaQuery.ID_CAIXA;
import static br.com.infox.epp.painel.caixa.CaixaQuery.ID_TAREFA;
import static br.com.infox.epp.painel.caixa.CaixaQuery.NODE_ANTERIOR;
import static br.com.infox.epp.painel.caixa.CaixaQuery.NOME_CAIXA;
import static br.com.infox.epp.painel.caixa.CaixaQuery.NOME_INDICE;
import static br.com.infox.epp.painel.caixa.CaixaQuery.REMOVE_BY_ID;
import static br.com.infox.epp.painel.caixa.CaixaQuery.REMOVE_BY_ID_QUERY;
import static br.com.infox.epp.painel.caixa.CaixaQuery.SEQUENCE_TABLE_CAIXA;
import static br.com.infox.epp.painel.caixa.CaixaQuery.TABLE_CAIXA;

import java.text.Normalizer;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.entity.Tarefa;

@Entity
@Table(name = TABLE_CAIXA)
@NamedQueries(value = { @NamedQuery(name = REMOVE_BY_ID, query = REMOVE_BY_ID_QUERY) })
public class Caixa implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idCaixa;
    private String nomeCaixa;
    private String dsCaixa;
    private String nomeIndice;
    private Tarefa tarefa;
    private Integer idNodeAnterior;
    private List<Processo> processoList = new ArrayList<Processo>(0);

    public Caixa() {

    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_TABLE_CAIXA)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_CAIXA, unique = true, nullable = false)
    public int getIdCaixa() {
        return idCaixa;
    }

    public void setIdCaixa(int idCaixa) {
        this.idCaixa = idCaixa;
    }

    @Column(name = NOME_CAIXA, length = LengthConstants.NOME_PADRAO)
    @Size(max = LengthConstants.NOME_PADRAO)
    public String getNomeCaixa() {
        return nomeCaixa;
    }

    public void setNomeCaixa(String nomeCaixa) {
        this.nomeCaixa = nomeCaixa;
    }

    @Column(name = DESCRICAO_CAIXA)
    public String getDsCaixa() {
        return dsCaixa;
    }

    public void setDsCaixa(String dsCaixa) {
        this.dsCaixa = dsCaixa;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_TAREFA)
    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    @Column(name = NOME_INDICE, length = LengthConstants.NOME_PADRAO, nullable = false)
    @Size(max = LengthConstants.NOME_PADRAO)
    public String getNomeIndice() {
        return nomeIndice;
    }

    public void setNomeIndice(String nomeIndice) {
        this.nomeIndice = nomeIndice;
    }

    @Column(name = NODE_ANTERIOR)
    public Integer getIdNodeAnterior() {
        return idNodeAnterior;
    }

    public void setIdNodeAnterior(Integer idNodeAnterior) {
        this.idNodeAnterior = idNodeAnterior;
    }

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
        CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = CAIXA_ATTRIBUTE)
    public List<Processo> getProcessoList() {
        return processoList;
    }

    public void setProcessoList(List<Processo> processoList) {
        this.processoList = processoList;
    }

    @Override
    public String toString() {
        return nomeCaixa;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Caixa)) {
            return false;
        }
        Caixa other = (Caixa) obj;
        if (getIdCaixa() != other.getIdCaixa()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdCaixa();
        return result;
    }

    @PreUpdate
    @PrePersist
    public void normalize() {
        String normalized = Normalizer.normalize(getNomeCaixa(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
        setNomeIndice(normalized);
    }
}
