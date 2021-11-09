package br.com.infox.epp.tarefaexterna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "uuidTarefaExterna")
public class CadastroTarefaExternaDocumentoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private UUID uuidTarefaExterna;
    @NotNull
    private List<DocumentoBin> bins = new ArrayList<>();
    @NotNull
    private Date dataInclusao;
    @NotNull
    private Integer idClassificacaoDocumento;
    private Integer idPasta;
    @NotBlank
    private String descricao;
    @NotBlank
    private boolean uploadValido;

    public CadastroTarefaExternaDocumentoDTO(UUID uuidTarefaExterna) {
        super();
        this.uuidTarefaExterna = uuidTarefaExterna;
    }

}
