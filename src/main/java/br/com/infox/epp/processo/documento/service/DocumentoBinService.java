package br.com.infox.epp.processo.documento.service;

import java.util.List;

import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;

public interface DocumentoBinService {
	public List<AssinaturaDocumento> carregarAssinaturas(Integer idDocumentoBin);
	public DocumentoBinario carregarDocumentoBinario(Integer idDocumentoBin);
	public Integer getSize(Integer idDocumentoBin);
	public boolean existeBinario(Integer idDocumentoBin);
	public String getNomeArquivo(Integer idDocumentoBin);
	public String getExtensao(Integer idDocumentoBin);
}
