package br.com.infox.epp.entrega.checklist;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;

@Entity
@Table(name = "tb_checklist_doc")
public class ChecklistDoc implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String GENERATOR_NAME = "ChecklistDocGenerator";
    private static final String SEQUENCE_NAME = "sq_checklist_doc";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_checklist_doc", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_checklist", nullable = false)
    private Checklist checklist;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento", nullable = false)
    private Documento documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_situacao")
    private ChecklistSituacao situacao;

    @Column(name = "ds_comentario")
    @Size(max = 4096)
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_alteracao")
    private UsuarioLogin usuarioAlteracao;

    @Column(name = "dt_alteracao")
    private Date dataAlteracao;

    @Version
    @Column(name = "nr_version", nullable = false)
    private Long version = 0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public ChecklistSituacao getSituacao() {
        return situacao;
    }

    public void setSituacao(ChecklistSituacao situacao) {
        this.situacao = situacao;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public UsuarioLogin getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
