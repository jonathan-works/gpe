package br.com.infox.epp.access;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.core.exception.SystemExceptionFactory;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoaFisica.PessoaFisicaSearch;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.jwt.JWT;
import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.verifiers.Verifiers;
import br.com.infox.security.rsa.RSAErrorCodes;
import br.com.infox.security.rsa.RSAUtil;

@Named
@ViewScoped
public class TermoAdesaoViewController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private TermoAdesaoService termoAdesaoManager;
    @Inject private ParametroManager parametroManager;
    @Inject private PessoaFisicaSearch pessoaFisicaSearch;

    private PessoaFisica pessoaFisica;
    private String urlTermoAdesao;
    private DocumentoBin termoAdesao;

    @PostConstruct
    protected void init() {
        String jwt = getRequestParameter("jwt");
        if (jwt != null){
            this.pessoaFisica = retrievePessoaFisica(jwt, getKey());
            this.termoAdesao = retrieveTermoAdesao(pessoaFisica);
            urlTermoAdesao=termoAdesaoManager.buildUrlDownload(getContextPath(), jwt, this.termoAdesao.getUuid().toString());
        }
    }

    private DocumentoBin retrieveTermoAdesao(PessoaFisica pessoaFisica) {
        if (!isTermoAdesaoAssinado(pessoaFisica)){
            return termoAdesaoManager.createTermoAdesaoFor(pessoaFisica);
        } else {
            return pessoaFisica.getTermoAdesao();
        }
    }
    
    public boolean isTermoAdesaoAssinado(PessoaFisica pessoaFisica){
        return pessoaFisica != null && termoAdesaoManager.isTermoAdesaoAssinado(pessoaFisica.getCpf());
    }

    private String getContextPath() {
        return getHttpServletRequest().getContextPath();
    }

    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }
    
    public String getUrlTermoAdesao(){
        return urlTermoAdesao;
    }

    private String getRequestParameter(String name) {
        return getHttpServletRequest().getParameter(name);
    }

    public PessoaFisica retrievePessoaFisica(String jwt, byte[] secret) {
        Map<String, Object> decodedPayload = JWT.parser().setKey(secret).parse(jwt);

        Verifiers.anyOf(InfoxPrivateClaims.CPF).verify(decodedPayload);

        String cpf = (String) decodedPayload.get(InfoxPrivateClaims.CPF.getClaim());
        if (cpf != null) {
            return pessoaFisicaSearch.getByCpf(cpf);
        }

        return null;
    }

    private byte[] getKey() {
        String base64RsaKey = parametroManager.getValorParametro(Parametros.EPP_API_RSA_PUBLIC_KEY.getLabel());
        if (base64RsaKey == null || base64RsaKey.isEmpty()) {
            throw SystemExceptionFactory.create(RSAErrorCodes.INVALID_PRIVATE_KEY_STRUCTURE)
                    .set(Parametros.EPP_API_RSA_PUBLIC_KEY.getLabel(), base64RsaKey);
        }
        return RSAUtil.getPublicKeyFromBase64(base64RsaKey).getEncoded();
    }

}
