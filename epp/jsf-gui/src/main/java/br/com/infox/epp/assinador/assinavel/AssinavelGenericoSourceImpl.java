package br.com.infox.epp.assinador.assinavel;

import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class AssinavelGenericoSourceImpl implements AssinavelSource {

	private byte[] data;
	private final TipoMeioAssinaturaEnum tipoMeioAssinatura;

	@Override
	public byte[] dataToSign(TipoSignedData tipoHash) {
			return tipoHash.dataToSign(data);
	}
}