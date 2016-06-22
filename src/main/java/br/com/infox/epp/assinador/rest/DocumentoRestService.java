package br.com.infox.epp.assinador.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.assinador.AssinadorGroupService;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinaturaLegada;
import br.com.infox.epp.assinador.api.Assinatura;
import br.com.infox.epp.assinador.api.Documento;

@Stateless
public class DocumentoRestService {
	
	@Inject
	private AssinadorGroupService groupService;
	@Inject
	AssinadorService assinadorService;
	
	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public List<Documento> listarDocumentos(String token) {
		List<UUID> documentos = groupService.getDocumentos(token);
		List<Documento> retorno = new ArrayList<>();
		for(UUID uuid : documentos) {
			Documento documento = new Documento(uuid);
			retorno.add(documento);
		}
		return retorno;
	}
	
	private void assinarDocumentoSemFlush(Assinatura assinatura, String tokenGrupo) {
		if(tokenGrupo != null) {
			DadosAssinaturaLegada dadosAssinaturaLegada = assinadorService.getDadosAssinaturaLegada(assinatura.getAssinatura());
			groupService.atualizarAssinaturaTemporaria(tokenGrupo, assinatura.getUuidDocumento(), dadosAssinaturaLegada);
		}
		assinadorService.assinarDocumento(assinatura.getUuidDocumento(), assinatura.getCodigoPerfil(), assinatura.getCodigoLocalizacao(), assinatura.getAssinatura());
	}
	
	public void assinarDocumento(Assinatura assinatura, String tokenGrupo) {
		assinarDocumentoSemFlush(assinatura, tokenGrupo);
		getEntityManager().flush();
	}
	
	public void assinarDocumentos(List<Assinatura> assinaturas, String tokenGrupo) {
		for(Assinatura assinatura : assinaturas) {
			assinarDocumentoSemFlush(assinatura, tokenGrupo);
		}
		getEntityManager().flush();
	}
}
