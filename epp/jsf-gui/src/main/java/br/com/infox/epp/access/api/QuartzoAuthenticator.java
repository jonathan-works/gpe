package br.com.infox.epp.access.api;

import br.com.infox.core.exception.UnexpectedErrorView;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.util.quartzo.EncryptionQuartzo;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.quartzo.soap.ws.client.WSControleAcessoGPE;
import br.quartzo.soap.ws.client.WSControleAcessoGPESoapPort;
import br.quartzo.soap.ws.client.WSControleAcessoGPEVALIDARLOGINACESSO;
import br.quartzo.soap.ws.client.WSControleAcessoGPEVALIDARLOGINACESSOResponse;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.security.Credentials;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;

@Name(QuartzoAuthenticator.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
@ContextDependency
public class QuartzoAuthenticator implements Serializable {

    private static final Logger LOG = Logger.getLogger(QuartzoAuthenticator.class.getName());

    public static final String NAME = "quartzoAuthenticator";
    private static final int SSID_GPE = 181;

    @Inject
    protected UsuarioLoginManager usuarioLoginManager;

    public boolean login(){
        Credentials credentials = (Credentials) Component.getInstance(Credentials.class);

        WSControleAcessoGPEVALIDARLOGINACESSO validarloginAcesso =  new WSControleAcessoGPEVALIDARLOGINACESSO();

        validarloginAcesso.setSisid(SSID_GPE);
      //  validarloginAcesso.setUsulogin(credentials.getUsername());
        validarloginAcesso.setUsulogin("4909913");
        String chave = EncryptionQuartzo.getNewKey();
        // validarloginAcesso.setUsusenha(EncryptionQuartzo.encrypt64(credentials.getPassword(), chave));
        validarloginAcesso.setUsusenha("VKQGVuRbaVLFXtjbU4CiksK87DCpRh0u6He0rn3Y2ME=");
       // validarloginAcesso.setChave(chave);
        validarloginAcesso.setChave("AFD13A5C5A863B803A2882E8D44F10A9");

        WSControleAcessoGPESoapPort ws = new WSControleAcessoGPE().getWSControleAcessoGPESoapPort();

        WSControleAcessoGPEVALIDARLOGINACESSOResponse validarloginacessoResponse = ws.validarloginacesso(validarloginAcesso);

        boolean loginSuccess = !Objects.isNull(validarloginacessoResponse.getSdtperfilusuariogpe());

        UsuarioLogin usuarioLoginByLogin = usuarioLoginManager.getUsuarioLoginByLogin(credentials.getUsername());
        System.out.println(validarloginAcesso);

        LOG.info("Login with quartzo. Successfully? " + loginSuccess);

        return loginSuccess;
    }

}
