package br.com.infox.epp.painel.caixa;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_caixa")
public class Caixa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "CaixaGenerator", sequenceName = "sq_tb_caixa")
    @GeneratedValue(generator = "CaixaGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_caixa", unique = true, nullable = false)
    private Integer idCaixa;
    
    @Column(name = "nm_caixa", length = LengthConstants.NOME_PADRAO)
    @Size(max = LengthConstants.NOME_PADRAO)
    private String nomeCaixa;
    
    @Column(name = "ds_caixa", nullable = true)
    private String dsCaixa;
    
    @NotNull
    @Column(name = "cd_node_key", nullable = false)
    private String taskKey;
    
    @Column(name = "nm_caixa_idx", length = LengthConstants.NOME_PADRAO, nullable = false)
    @Size(max = LengthConstants.NOME_PADRAO)
    private String nomeIndice;
    
    @Column(name = "id_node_anterior", nullable = true)
    private Long idNodeAnterior;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "caixa")
    private List<Processo> processoList = new ArrayList<Processo>(0);
    
    @PrePersist
    private void prePersist() {
    	normalizarNomeIndiceCaixa();
    }
    
    @PreUpdate
    private void preUpdate() {
    	normalizarNomeIndiceCaixa();
    }
    
    public Caixa() {
    }

    public Caixa(Integer idCaixa) {
        this.idCaixa = idCaixa;
    }

    public Integer getIdCaixa() {
        return idCaixa;
    }

    public void setIdCaixa(Integer idCaixa) {
        this.idCaixa = idCaixa;
    }

    public String getNomeCaixa() {
        return nomeCaixa;
    }

    public void setNomeCaixa(String nomeCaixa) {
        this.nomeCaixa = nomeCaixa;
    }

    public String getDsCaixa() {
        return dsCaixa;
    }

    public void setDsCaixa(String dsCaixa) {
        this.dsCaixa = dsCaixa;
    }
    
    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getNomeIndice() {
        return nomeIndice;
    }

    public void setNomeIndice(String nomeIndice) {
        this.nomeIndice = nomeIndice;
    }

    public Long getIdNodeAnterior() {
        return idNodeAnterior;
    }

    public void setIdNodeAnterior(Long idNodeAnterior) {
        this.idNodeAnterior = idNodeAnterior;
    }

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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Caixa))
            return false;
        Caixa other = (Caixa) obj;
        if (getIdCaixa() == null) {
            if (other.getIdCaixa() != null)
                return false;
        } else if (!getIdCaixa().equals(other.getIdCaixa()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getIdCaixa() == null) ? 0 : getIdCaixa().hashCode());
        return result;
    }
    
    private void normalizarNomeIndiceCaixa() {
    	String nomeIndiceCaixa = format("{0}-{1}", getNomeCaixa(), getTaskKey());
    	String normalized = Normalizer.normalize(nomeIndiceCaixa, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
        setNomeIndice(normalized);
    }

}
