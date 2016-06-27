package br.com.infox.epp.assinador.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Hex;

import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.api.AssinaturaResource;
import br.com.infox.epp.assinador.api.Assinavel;
import br.com.infox.epp.assinador.api.AssinavelResource;

public class AssinavelResourceImpl implements AssinavelResource {

	@Inject
	private AssinadorService assinadorService;
	
	private String tokenGrupo;
	
	public AssinavelResourceImpl() {
		
	}

	@Override
	public List<Assinavel> listarAssinaveis() {
		List<UUID> documentos = assinadorService.listarAssinaveis(tokenGrupo);
		List<Assinavel> retorno = new ArrayList<>();
		for(UUID uuid : documentos) {
			Assinavel documento = new Assinavel(uuid);
			retorno.add(documento);
		}
		return retorno;
	}

	@Override
	public String getSHA256Hex(UUID uuid) {
		return Hex.encodeHexString(getSHA256(uuid));
	}

	@Override
	public byte[] getSHA256(UUID uuid) {
		return assinadorService.getSha256(tokenGrupo, uuid);
	}

	@Override
	public AssinaturaResource getAssinaturaResource(UUID uuid) {
		AssinaturaResourceImpl assinaturaResourceImpl = new AssinaturaResourceImpl();
		assinaturaResourceImpl.setTokenGrupo(tokenGrupo);
		assinaturaResourceImpl.setUuidAssinavel(uuid);
		return assinaturaResourceImpl;
	}

	public void setTokenGrupo(String tokenGrupo) {
		this.tokenGrupo = tokenGrupo;
	}

}
