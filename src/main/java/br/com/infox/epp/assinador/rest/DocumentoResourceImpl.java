package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

public class DocumentoResourceImpl implements DocumentoResource {

	@Inject
	private DocumentoBinarioManager documentoBinarioManager;
		
	private String tokenGrupo;

	private DocumentoBin documentoBin;

	public void setDocumentoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
	}	

	public void setTokenGrupo(String tokenGrupo) {
		this.tokenGrupo = tokenGrupo;
	}
	
	public byte[] getBinario() {
		return documentoBinarioManager.getData(documentoBin.getId());
	}

	@Override
	public byte[] getMD5() {
		return DigestUtils.md5(getBinario());
	}

	@Override
	public String getMD5Hex() {
		return DigestUtils.md5Hex(getBinario());
	}
	
	@Override
	public byte[] getSHA1() {
		return DigestUtils.sha1(getBinario());
	}

	@Override
	public String getSHA1Hex() {
		return DigestUtils.sha1Hex(getBinario());
	}

	@Override
	public byte[] getSHA256() {
		return DigestUtils.sha256(getBinario());
	}

	@Override
	public String getSHA256Hex() {
		return DigestUtils.sha256Hex(getBinario());
	}

	@Override
	public AssinaturaRest getAssinaturaRest() {
		AssinaturaRestImpl assinaturaRestImpl = BeanManager.INSTANCE.getReference(AssinaturaRestImpl.class);
		
		assinaturaRestImpl.setDocuemntoBin(documentoBin);
		assinaturaRestImpl.setTokenGrupo(tokenGrupo);
		
		return assinaturaRestImpl;
	}
	

}
