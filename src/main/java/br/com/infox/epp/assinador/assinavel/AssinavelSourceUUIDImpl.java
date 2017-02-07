package br.com.infox.epp.assinador.assinavel;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AssinavelSourceUUIDImpl implements AssinavelSourceUUID{
    private final byte[] data;
    private final UUID uuid;
    
    public AssinavelSourceUUIDImpl(UUID uuid, String texto) {
        this(uuid, texto.getBytes(StandardCharsets.UTF_8));
    }
    
    public AssinavelSourceUUIDImpl(UUID uuid, byte[] data){
        this.uuid = uuid;
        this.data = data;
    }
    
    @Override
    public byte[] dataToSign(TipoSignedData tipoHash) {
        return tipoHash.dataToSign(data);
    }

    @Override
    public UUID getUUIDAssinavel() {
        return uuid;
    }
    
}