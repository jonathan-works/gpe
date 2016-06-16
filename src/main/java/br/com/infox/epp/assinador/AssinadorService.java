package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.infox.epp.assinador.rest.Assinatura;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class AssinadorService implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String NOME_CACHE_DOCUMENTOS="AssinadorService.listaDocumentos";
	
	@Inject
	private CertificateSignatureGroupService certificateSignatureGroupService;
	@Inject
	private CMSAdapter cmsAdapter;

	public CertificateSignatureGroup criarListaDocumentos(List<DocumentoBin> listaDocumentos) {
		return certificateSignatureGroupService.createNewGroup(listaDocumentos);
	}
	
	public DadosAssinaturaLegada getDadosAssinaturaLegada(byte[] signature) {
		return cmsAdapter.convert(signature);
	}
	
}
