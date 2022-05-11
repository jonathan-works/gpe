package br.com.infox.epp.documento;

import br.com.infox.epp.processo.documento.entity.Documento;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentoAssinavelDTO implements Serializable {

	private Integer iddocumento;
	private Integer idClassificacao;
	private Integer idDocumentoBin;
	private String idTaskIntance;
}
