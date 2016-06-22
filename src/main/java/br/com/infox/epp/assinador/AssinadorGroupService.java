package br.com.infox.epp.assinador;

import java.util.List;
import java.util.UUID;

public interface AssinadorGroupService {
	
	public enum StatusToken {
		ERRO, SUCESSO, DESCONHECIDO, AGUARDANDO_ASSINATURA, EXPIRADO 
	}

	public String createNewGroup(List<UUID> documentos);
	
	public void validarToken(String token);
	
	public boolean isTokenExpired(String token);
	
	public StatusToken getStatus(String token);
	
	public List<UUID> getDocumentos(String token);
	
	public void apagarGrupo(String token);
	
	public void cancelar(String token);
	
	public void erroProcessamento(String token, String mensagem);
	
	public void processamentoFinalizado(String token);
	
	public void atualizarAssinaturaTemporaria(String tokenGrupo, UUID uuid, DadosAssinaturaLegada dadosAssinaturaLegada);
}
