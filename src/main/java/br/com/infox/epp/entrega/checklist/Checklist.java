package br.com.infox.epp.entrega.checklist;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_checklist")
public class Checklist implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String GENERATOR_NAME = "checklistGenerator";
    private static final String SEQUENCE_NAME = "sq_checklist";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_checklist", nullable = false, updatable = false, unique = true)
    private Integer id;

    @NotNull
    @JoinColumn(name = "id_processo")
    @ManyToOne(fetch = FetchType.LAZY)
    private Processo processo;

    @NotNull
    @JoinColumn(name = "id_pasta")
    @ManyToOne(fetch = FetchType.LAZY)
    private Pasta pasta;

    @NotNull
    @Column(name = "dt_criacao", nullable = false)
    private Date dataCriacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_criacao", nullable = false)
    private UsuarioLogin usuarioCriacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Pasta getPasta() {
        return pasta;
    }

    public void setPasta(Pasta pasta) {
        this.pasta = pasta;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public UsuarioLogin getUsuarioCriacao() {
        return usuarioCriacao;
    }

    public void setUsuarioCriacao(UsuarioLogin usuarioCriacao) {
        this.usuarioCriacao = usuarioCriacao;
    }

}
