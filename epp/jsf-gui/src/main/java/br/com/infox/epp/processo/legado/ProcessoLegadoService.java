package br.com.infox.epp.processo.legado;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.linkExterno.LinkAplicacaoExternaService;
import br.com.infox.epp.system.Parametros;
import br.com.infox.jwt.JWT;
import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoLegadoService {

    @Inject
    private LinkAplicacaoExternaService linkAplicacaoExternaService;

    public String gerarUrlAcessoSistemaProcessosLegados(UsuarioLogin usuarioLogado) {
        String urlBase = Parametros.URL_ACESSO_PROCESSOS_LEGADOS.getValue();
        try {
            if (!StringUtil.isEmpty(urlBase)) {
                new URI(urlBase);
            } else {
                throw new EppConfigurationException(String.format("O parâmetro '%s' não está configurado", Parametros.URL_ACESSO_PROCESSOS_LEGADOS.getLabel()));
            }
        } catch (URISyntaxException e) {
            throw new EppConfigurationException(String.format("O parâmetro '%s' não está configurado com uma URL válida", Parametros.URL_ACESSO_PROCESSOS_LEGADOS.getLabel()));
        }

        List<Entry<JWTClaim, Object>> claims = new ArrayList<>();
        claims.add(JWT.claim(InfoxPrivateClaims.LOGIN, usuarioLogado.getLogin()));
        claims.add(JWT.claim(InfoxPrivateClaims.NOME_USUARIO, usuarioLogado.getNomeUsuario()));
        String urlComToken = linkAplicacaoExternaService.appendJWTTokenToUrlQuery(urlBase, claims);

        if (urlComToken.equals(urlBase)) {
            throw new BusinessRollbackException("Houve um erro ao gerar a URL de acesso, por favor tente novamente");
        }

        return urlComToken;
    }
}
