package br.com.infox.epp.processo.documento.entity;

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_documento_compartilhamento")
public class DocumentoCompartilhamento implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String GENERATOR_NAME = "DocumentoCompartilhamentoGenerator";
    private static final String SEQUENCE_NAME = "sq_documento_compartilhamento";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_documento_compartilhamento")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento")
    private Documento documento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo_alvo")
    private Processo processoAlvo;

    @NotNull
    @Column(name = "in_ativo")
    private Boolean ativo;

    @OneToMany(mappedBy = "documentoCompartilhamento", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<DocumentoCompartilhamentoHistorico> historicoList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public Processo getProcessoAlvo() {
        return processoAlvo;
    }

    public void setProcessoAlvo(Processo processoAlvo) {
        this.processoAlvo = processoAlvo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
