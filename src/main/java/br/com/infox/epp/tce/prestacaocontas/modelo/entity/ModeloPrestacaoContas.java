package br.com.infox.epp.tce.prestacaocontas.modelo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.tce.prestacaocontas.modelo.type.EsferaGovernamental;
import br.com.infox.epp.tce.prestacaocontas.modelo.type.TipoPrestacaoContas;

@Entity
@Table(name = "tb_modelo_prestacao_contas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "tp_prestacao_contas", "id_grupo_prestacao_contas", "tp_esfera_governamental", "nr_ano_exercicio"
    })
})
public class ModeloPrestacaoContas implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ModeloPrestacaoContasGenerator", allocationSize = 1, sequenceName = "sq_modelo_prestacao_contas")
    @GeneratedValue(generator = "ModeloPrestacaoContasGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_modelo_prestacao_contas")
    private Long id;
    
    @NotNull
    @Size(max = 50)
    @Column(name = "nm_modelo_prestacao_contas", length = 50, nullable = false, unique = true)
    private String nome;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_prestacao_contas", nullable = false)
    private TipoPrestacaoContas tipoPrestacaoContas;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_esfera_governamental", nullable = false)
    private EsferaGovernamental esfera;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grupo_prestacao_contas", nullable = false)
    private GrupoPrestacaoContas grupoPrestacaoContas;
    
    @NotNull
    @Column(name = "nr_ano_exercicio", nullable = false)
    private Integer anoExercicio;
    
    @NotNull
    @Column(name = "in_ques_ex_setor_contr_interno", nullable = false)
    private Boolean questionarExistenciaSetoresControleInterno = false;
    
    @NotNull
    @Column(name = "in_ques_ex_setor_contabilidade", nullable = false)
    private Boolean questionarExistenciaSetoresContabilidade = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tb_mod_pres_contas_tp_proc_doc", joinColumns = {@JoinColumn(name = "id_modelo_prestacao_contas", nullable = false)}, 
        inverseJoinColumns = {@JoinColumn(name = "id_tipo_processo_documento", nullable = false)}, 
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id_modelo_prestacao_contas", "id_tipo_processo_documento"})
    })
    private List<TipoProcessoDocumento> classificacoesDocumento = new ArrayList<>(0);
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloPrestacaoContas")
    private List<ResponsavelModeloPrestacaoContas> responsaveis = new ArrayList<>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPrestacaoContas getTipoPrestacaoContas() {
        return tipoPrestacaoContas;
    }

    public void setTipoPrestacaoContas(TipoPrestacaoContas tipoPrestacaoContas) {
        this.tipoPrestacaoContas = tipoPrestacaoContas;
    }

    public EsferaGovernamental getEsfera() {
        return esfera;
    }

    public void setEsfera(EsferaGovernamental esfera) {
        this.esfera = esfera;
    }

    public GrupoPrestacaoContas getGrupoPrestacaoContas() {
        return grupoPrestacaoContas;
    }

    public void setGrupoPrestacaoContas(GrupoPrestacaoContas grupoPrestacaoContas) {
        this.grupoPrestacaoContas = grupoPrestacaoContas;
    }

    public Integer getAnoExercicio() {
        return anoExercicio;
    }

    public void setAnoExercicio(Integer anoExercicio) {
        this.anoExercicio = anoExercicio;
    }

    public Boolean getQuestionarExistenciaSetoresControleInterno() {
        return questionarExistenciaSetoresControleInterno;
    }

    public void setQuestionarExistenciaSetoresControleInterno(Boolean questionarExistenciaSetoresControleInterno) {
        this.questionarExistenciaSetoresControleInterno = questionarExistenciaSetoresControleInterno;
    }

    public Boolean getQuestionarExistenciaSetoresContabilidade() {
        return questionarExistenciaSetoresContabilidade;
    }

    public void setQuestionarExistenciaSetoresContabilidade(Boolean questionarExistenciaSetoresContabilidade) {
        this.questionarExistenciaSetoresContabilidade = questionarExistenciaSetoresContabilidade;
    }

    public List<TipoProcessoDocumento> getClassificacoesDocumento() {
        return classificacoesDocumento;
    }

    public void setClassificacoesDocumento(List<TipoProcessoDocumento> classificacoesDocumento) {
        this.classificacoesDocumento = classificacoesDocumento;
    }

    public List<ResponsavelModeloPrestacaoContas> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(List<ResponsavelModeloPrestacaoContas> responsaveis) {
        this.responsaveis = responsaveis;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getAnoExercicio() == null) ? 0 : getAnoExercicio().hashCode());
        result = prime * result + ((getEsfera() == null) ? 0 : getEsfera().hashCode());
        result = prime
                * result
                + ((getGrupoPrestacaoContas() == null) ? 0 : getGrupoPrestacaoContas()
                        .hashCode());
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime
                * result
                + ((getTipoPrestacaoContas() == null) ? 0 : getTipoPrestacaoContas()
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ModeloPrestacaoContas))
            return false;
        ModeloPrestacaoContas other = (ModeloPrestacaoContas) obj;
        if (getAnoExercicio() == null) {
            if (other.getAnoExercicio() != null)
                return false;
        } else if (!getAnoExercicio().equals(other.getAnoExercicio()))
            return false;
        if (getEsfera() != other.getEsfera())
            return false;
        if (getGrupoPrestacaoContas() == null) {
            if (other.getGrupoPrestacaoContas() != null)
                return false;
        } else if (!getGrupoPrestacaoContas().equals(other.getGrupoPrestacaoContas()))
            return false;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getTipoPrestacaoContas() != other.getTipoPrestacaoContas())
            return false;
        return true;
    }
}
