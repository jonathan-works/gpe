package br.com.infox.epp.tarefaexterna;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = PastaUploadTarefaExterna.TABLE_NAME)
@EqualsAndHashCode(of = "id")
public class PastaUploadTarefaExterna implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_pasta_upload_tarefa_ext";
    public static final String COLUMN_ID = "id_pasta_upload_tarefa_ext";

    @Id
    @Column(name = COLUMN_ID, unique = true, nullable = false)
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = "sq_pasta_upload_tarefa_ext")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "cd_pasta", nullable = false)
    private String codigo;
    @NotNull
    @Column(name = "nm_pasta", nullable = false)
    private String nome;

}
