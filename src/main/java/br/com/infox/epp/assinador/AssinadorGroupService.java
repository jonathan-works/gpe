package br.com.infox.epp.assinador;

import java.util.List;
import java.util.UUID;

public interface AssinadorGroupService {
	
	public enum StatusToken {
		ERRO, SUCESSO, DESCONHECIDO, AGUARDANDO_ASSINATURA, EXPIRADO 
	}

	public String createNewGroupWithBinary(List<byte[]> documentos);
	
	public String createNewGroup(List<UUID> documentos);
	
	public void validarToken(String token);
	
	public boolean isTokenExpired(String token);
	
	public StatusToken getStatus(String token);
	
	public List<UUID> getAssinaveis(String token);
	
	public byte[] getSha256(String token, UUID uuidAssinavel); 
	
	public void apagarGrupo(String token);
	
	public void cancelar(String token);
	
	public void erroProcessamento(String token, UUID uuidAssinavel, String codigoErro,  String mensagem);
	
	public void atualizarAssinaturaTemporaria(String tokenGrupo, UUID uuidAssinavel, DadosAssinaturaLegada dadosAssinaturaLegada);
}
