package br.com.infox.epp.processo.documento.anexos;

import java.io.Serializable;

import br.com.infox.epp.processo.documento.entity.Documento;

public class DocumentoUploadBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Documento documento;
	private byte[] data;
	private boolean valido;
	
	public DocumentoUploadBean() {
		documento = new Documento();
	}
	
	public Documento getDocumento() {
		return documento;
	}
	
	public void setDocumento(Documento documento) {
		this.documento = documento;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}
	
	public void clear() {
		setDocumento(new Documento());
		setData(null);
		setValido(false);
	}

}
