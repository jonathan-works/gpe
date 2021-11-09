package br.com.infox.epp.tarefaexterna;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = ClassificacaoDocumentoUploadTarefaExterna.TABLE_NAME)
@EqualsAndHashCode(of = "id")
public class ClassificacaoDocumentoUploadTarefaExterna implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_class_doc_upload_tarefa_ext";
    public static final String COLUMN_ID = "id_class_doc_upload_tarefa_ext";

    @Id
    @Column(name = COLUMN_ID, unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_ID, insertable = false, updatable = false)
    private ClassificacaoDocumento classificacaoDocumento;

}
