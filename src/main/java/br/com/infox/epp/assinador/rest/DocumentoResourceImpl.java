package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

public class DocumentoResourceImpl implements DocumentoResource {

	@Inject
	private DocumentoBinarioManager documentoBinarioManager;
	@Inject
	private DocumentoRestService documentoRestService;
		
	private CertificateSignatureGroup group;

	private DocumentoBin documentoBin;

	public void setDocumentoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
	}	

	public void setCertificateSignatureGroup(CertificateSignatureGroup certificateSignatureGroup) {
		this.group = certificateSignatureGroup;
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
	
	private void assinar(byte[] signature) {
		Documento documento = new Documento(documentoBin.getUuid(), signature);
		documentoRestService.criarOuAtualizarAssinatura(documento, group.getToken());
	}
	
	@Override
	public void setAssinaturaCms(byte[] assinatura) {
		assinar(assinatura);
	}

	@Override
	public void setAssinaturaPkcs7(byte[] assinatura) {
		assinar(assinatura);
	}


}
