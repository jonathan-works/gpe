package br.com.infox.epp.documento;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.epp.processo.documento.entity.Documento;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskInstanceListagemDocumentoDTO {

	private List<Documento> listaDocumentoAssinavel = new ArrayList<Documento>();
	private List<Documento> listaDocumentoNaoAssinavel = new ArrayList<Documento>();
	private List<Documento> listaDocumentoMinuta = new ArrayList<Documento>();

	private List<DocumentoAssinavelDTO> listaDocumentoAssinavelDTO = new ArrayList<DocumentoAssinavelDTO>();
	private List<DocumentoAssinavelDTO> listaDocumentoNaoAssinavelDTO = new ArrayList<DocumentoAssinavelDTO>();
	private List<DocumentoAssinavelDTO> listaDocumentoMinutaDTO = new ArrayList<DocumentoAssinavelDTO>();
}
