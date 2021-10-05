package br.com.infox.epp.documento.entity;

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.documento.entity.Documento;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = TaskInstancePermitidaAssinarDocumento.TABLE_NAME)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class TaskInstancePermitidaAssinarDocumento implements Serializable {
	
	public static final String TABLE_NAME = "tb_taski_permitida_assinar_doc";

    private static final long serialVersionUID = 1L;
    private static final String GENERATOR = "TaskInstancePermitidaAssinarDocumentoGenerator";
    
    @Id
    @SequenceGenerator(name = GENERATOR, sequenceName = "sq_taski_permitida_assinar_doc", allocationSize = 1, initialValue=1)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_taski_permitida_assinar_doc", nullable = false, unique = true)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id_documento", nullable = false)
    private Documento documento;
    
    @NotNull
    @Column(name = "id_taskinstance", nullable = false)
    private Long idTaskInstance;

}
