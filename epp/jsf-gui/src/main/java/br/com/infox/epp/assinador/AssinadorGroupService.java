package br.com.infox.epp.assinador;

import java.util.List;
import java.util.UUID;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;

public interface AssinadorGroupService {

    public String createNewGroupWithAssinavelProvider(AssinavelProvider assinavelProvider);

    public void validarToken(String token);

    public void validarNovoToken(String token);

    public boolean isTokenExpired(String token);

    public StatusToken getStatus(String token);

    public List<UUID> getAssinaveis(String token);

    public byte[] getSha256(String token, UUID uuidAssinavel);

    public void apagarGrupo(String token);

    public void cancelar(String token);

    public void erroProcessamento(String token, UUID uuidAssinavel, String codigoErro, String mensagem);

    public void atualizarAssinaturaTemporaria(String tokenGrupo, UUID uuidAssinavel,
            DadosAssinaturaLegada dadosAssinaturaLegada);

    public List<DadosAssinatura> getDadosAssinatura(String token);

    public DadosAssinatura getDadosAssinatura(String token, UUID uuidAssinavel);

}
