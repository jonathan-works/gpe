package br.com.infox.epp.tarefaexterna;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = DocumentoUploadTarefaExterna.TABLE_NAME)
@EqualsAndHashCode(of = "id")
public class DocumentoUploadTarefaExterna implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_documento_upload_tarefa_ext";
    public static final String COLUMN_ID = "id_documento_upload_tarefa_ext";

    @Id
    @Column(name = COLUMN_ID, unique = true, nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento_bin", nullable = false, insertable = false, updatable = false)
    private DocumentoBin documentoBin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_ID, nullable = false, insertable = false, updatable = false)
    private ClassificacaoDocumento classificacaoDocumento;

    @NotNull
    @Column(name = "cd_pasta", nullable = false)
    @Size(max = 250)
    private String codigo;

    @NotNull
    @Column(name = "ds_documento", nullable = false)
    @Size(max = 260)
    private String descricao;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inclusao", nullable = false)
    @Size(max = 260)
    private Date dataInclusao;

}
