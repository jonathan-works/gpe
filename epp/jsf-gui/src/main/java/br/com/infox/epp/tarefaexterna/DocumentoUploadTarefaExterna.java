package br.com.infox.epp.tarefaexterna;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.hibernate.UUIDGenericType;
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
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = "sq_documento_upload_tarefa_ext")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Type(type = UUIDGenericType.TYPE_NAME)
    @Column(name = "ds_uuid_tarefa_externa", unique = true, nullable = false)
    private UUID uuidTarefaExterna;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento_bin", nullable = false)
    private DocumentoBin documentoBin;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_classificacao_documento", nullable = false)
    private ClassificacaoDocumento classificacaoDocumento;

    @NotNull
    @Size(max = 260)
    @Column(name = "ds_documento", nullable = false)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasta")
    private PastaUploadTarefaExterna pasta;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inclusao", nullable = false)
    private Date dataInclusao;

}
