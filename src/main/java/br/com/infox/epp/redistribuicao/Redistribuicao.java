package br.com.infox.epp.redistribuicao;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Entity
@Table(name = "tb_redistribuicao")
public class Redistribuicao implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String GENERATOR_NAME = "RedistribuicaoGenerator";
    private static final String SEQUENCE_NAME = "sq_redistribuicao";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_redistribuicao", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_processo")
    private Processo processo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_relator_anterior")
    private PessoaFisica relatorAnterior;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_udm_anterior", nullable = false)
    private UnidadeDecisoraMonocratica udmAnterior;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_relator")
    private PessoaFisica relator;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_udm", nullable = false)
    private UnidadeDecisoraMonocratica udm;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_tipo_redistribuicao")
    private TipoRedistribuicao tipo;

    @NotNull
    @Size(max = 4000)
    @Column(name = "ds_motivo")
    private String motivo;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_redistribuicao")
    private Date data;

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public PessoaFisica getRelatorAnterior() {
        return relatorAnterior;
    }

    public void setRelatorAnterior(PessoaFisica relatorAnterior) {
        this.relatorAnterior = relatorAnterior;
    }

    public UnidadeDecisoraMonocratica getUdmAnterior() {
        return udmAnterior;
    }

    public void setUdmAnterior(UnidadeDecisoraMonocratica udmAnterior) {
        this.udmAnterior = udmAnterior;
    }

    public PessoaFisica getRelator() {
        return relator;
    }

    public void setRelator(PessoaFisica relator) {
        this.relator = relator;
    }

    public UnidadeDecisoraMonocratica getUdm() {
        return udm;
    }

    public void setUdm(UnidadeDecisoraMonocratica udm) {
        this.udm = udm;
    }

    public TipoRedistribuicao getTipo() {
        return tipo;
    }

    public void setTipo(TipoRedistribuicao tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }
}
