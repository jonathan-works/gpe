package br.com.infox.epp.assinador.assinavel;

public class AssinavelGenericoSourceImpl implements AssinavelSource {
	
	private byte[] data;
	
	public AssinavelGenericoSourceImpl(byte[] data) {
		super();
		this.data = data;
	}

	@Override
	public byte[] dataToSign(TipoSignedData tipoHash) {
			return tipoHash.dataToSign(data); 
	}
}