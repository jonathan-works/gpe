package br.com.infox.epp.assinador.assinavel;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor @Getter
public class AssinavelSourceUUIDImpl implements AssinavelSourceUUID{
    private final UUID UUIDAssinavel;
    private final byte[] data;
    private final TipoMeioAssinaturaEnum tipoMeioAssinatura;

    public AssinavelSourceUUIDImpl(UUID uuid, String texto, TipoMeioAssinaturaEnum tipo) {
        this(uuid, texto.getBytes(StandardCharsets.UTF_8), tipo);
    }

    @Override
    public byte[] dataToSign(TipoSignedData tipoHash) {
        return tipoHash.dataToSign(data);
    }

}