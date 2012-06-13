/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.core.certificado;

import java.math.BigInteger;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.util.Strings;

import br.com.infox.core.certificado.ValidaDocumento.ValidaDocumentoException;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.home.DocumentoBinHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(ValidaDocumentoHome.NAME)
@BypassInterceptors
public class ValidaDocumentoHome {
	
	public static final String NAME = "validaDocumentoHome";
	private ProcessoDocumento documento;
	private ProcessoDocumentoBin processoDocumentoBin;
	private Boolean valido;
	private Certificado dadosCertificado;
	

	@Deprecated
	public void validaDocumento(ProcessoDocumento documento) {
		this.documento = documento;
		ProcessoDocumentoBin bin = documento.getProcessoDocumentoBin();
		validaDocumento(bin, bin.getCertChain(), bin.getSignature());
	}
	
	/**
	 * Valida a assinatura de um ProcessoDocumento. Quando o documento � do tipo
	 * modelo as quebras de linha s�o retiradas.
	 * @param id
	 */
	public void validaDocumento(ProcessoDocumentoBin bin, String certChain, String signature) {
		processoDocumentoBin = bin;
		setValido(false);
		setDadosCertificado(null);
		byte[] data = null;
		
		if (Strings.isEmpty(bin.getCertChain()) || Strings.isEmpty(bin.getSignature())) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, 
					"O documento n�o est� assinado");
			return;
		}
		
		if (!bin.isBinario()) {
			data = ValidaDocumento.removeBR(bin.getModeloDocumento()).getBytes();
		} else {
			try {
				data = DocumentoBinHome.instance().getData(bin.getIdProcessoDocumentoBin()); 
			} catch (Exception e) {
				throw new IllegalArgumentException("Erro ao obter os dados do bin�rio", e);
			}
		}
		if (data == null) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, 
				"Documento inv�lido");
			return;
		}
		try {
			ValidaDocumento validaDocumento = new ValidaDocumento(data, certChain, signature);
			setValido(validaDocumento.verificaAssinaturaDocumento());
			setDadosCertificado(validaDocumento.getDadosCertificado());
		} catch (ValidaDocumentoException e) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, 
				e.getMessage());
		} catch (CertificadoException e) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, 
					e.getMessage());
		}
	}	
	
	public void validaDocumentoId(Integer idDocumento) {
		if (idDocumento == null) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Id do documento n�o informado");
			return;
		}
		ProcessoDocumento processoDocumento = getEntityManager().find(ProcessoDocumento.class, idDocumento);
		if (processoDocumento == null) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Documento n�o encontrado.");
			return;
		}		
		validaDocumento(processoDocumento);
	}

	private EntityManager getEntityManager() {
		return EntityUtil.getEntityManager();
	}

	public ProcessoDocumento getDocumento() {
		return documento;
	}
	
	public void setDocumento(ProcessoDocumento documento) {
		this.documento = documento;
	}

	public void setValido(Boolean valido) {
		this.valido = valido;
	}

	public Boolean getValido() {
		return valido;
	}

	public void setDadosCertificado(Certificado dadosCertificado) {
		this.dadosCertificado = dadosCertificado;
	}

	public Certificado getDadosCertificado() {
		return dadosCertificado;
	}

	public ProcessoDocumentoBin getProcessoDocumentoBin() {
		return processoDocumentoBin;
	}
	
	public String getNomeCertificadora() {
		return dadosCertificado == null ? null : dadosCertificado.getNomeCertificadora();
	}
	
	public String getNome() {
		return dadosCertificado == null ? null : dadosCertificado.getNome();
	}
	
	public BigInteger getSerialNumber() {
		return dadosCertificado == null ? null : dadosCertificado.getSerialNumber();
	}	
	
	public static ValidaDocumentoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
}