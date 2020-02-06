package br.com.infox.epp.assinador.view.jsf;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.assinatura.PoliticaAssinatura;
import br.com.infox.assinatura.PoliticaAssinaturaFactory;
import br.com.infox.assinatura.assinador.impl.AssinadorFactory;
import br.com.infox.assinatura.assinador.signable.SimpleSignableIO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.view.AssinadorController;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoService;
import br.com.infox.epp.certificadoeletronico.builder.CertUtil;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronico;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronicoBin;
import br.com.infox.epp.login.LoginService;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

public class AssinadorListenerImpl implements AssinadorListener, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void processEvent(AssinadorEvent assinadorEvent) {
        switch (assinadorEvent.getActionType()) {
        case CLICK:
            clickEvent((AssinadorClickEvent) assinadorEvent);
            break;
        case CLICK_ASSINATURA_ELETRONICA:
            clickAssinaturaEletronicaEvent((AssinadorEletronicoClickEvent) assinadorEvent);
            break;
        case SIGN:
            signEvent((AssinadorSignEvent) assinadorEvent);
            break;
        case UPDATE:
            updateStatusEvent((AssinadorUpdateEvent) assinadorEvent);
            break;
        default:
            ((Assinador)((AssinadorCompleteEvent)assinadorEvent).getComponent()).setCurrentPhase(SignPhase.BEFORE_CLICK);
            break;
        }
    }

    private void updateStatusEvent(AssinadorUpdateEvent evt) {
        Assinador button = (Assinador) evt.getComponent();
        StatusToken status = Beans.getReference(AssinadorService.class).getStatus(button.getToken());
        button.setStatus(status);

        if (SignPhase.AFTER_CLICK.equals(button.getCurrentPhase()))
            button.setCurrentPhase(SignPhase.WAITING_SIGNATURE);
    }

    private void signEvent(AssinadorSignEvent evt) {
        Assinador button = (Assinador) evt.getComponent();

        if (SignPhase.WAITING_SIGNATURE.equals(button.getCurrentPhase())) {
            if (button.getSignAction() != null) {
                button.getSignAction().invoke(FacesContext.getCurrentInstance().getELContext(),
                        new Object[] { evt.getToken() });
            } else {
                button.setTokenField(evt.getToken());
                jndi(AssinadorController.class).assinaturasRecebidas(evt.getToken(), button.getCallbackHandler());
            }
            button.setStatus(null);
            button.setToken(null);
            button.setCurrentPhase(null);
        }
    }

    public void clickAssinaturaEletronicaEvent(AssinadorEletronicoClickEvent evt) {
        Assinador button = (Assinador) evt.getComponent();

        //Idealmente viria do componente, atribuindo o padrão

        PessoaFisica pfComponente = button.getPessoaAssinatura();
        PessoaFisica pfAutenticada = Optional.ofNullable(Authenticator.getUsuarioPerfilAtual()).map(UsuarioPerfil::getUsuarioLogin).map(UsuarioLogin::getPessoaFisica).orElse(null);
        if(pfComponente == null && pfAutenticada == null) {
            String msg = "Pessoa não encontrada.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            return;
        }
        PessoaFisica pessoaAssinante = Optional.ofNullable(pfComponente).orElse(pfAutenticada);
        if (!autenticar(pessoaAssinante, evt.getPassword())) {
            String msg = "Falha de autenticação";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            return;
        }
        PoliticaAssinatura politicaDeAssinatura = PoliticaAssinaturaFactory.getDefault().fromOID(PoliticaAssinatura.AD_RB_CMS_V_2_1);
        CertificadoEletronico certificadoEletronicoUsuarioLogado = pessoaAssinante.getCertificadoEletronico();
        if(certificadoEletronicoUsuarioLogado == null) {
            String msg = "Usuário não possui certificado";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            return;
        }
        CertificadoEletronicoService certificadoEletronicoService = null;
        AssinadorService assinadorService = null;
        try {
            certificadoEletronicoService = Beans.getReference(CertificadoEletronicoService.class);
            CertificadoEletronicoBin certificadoEletronicoBinRaiz = certificadoEletronicoService.getCertificadoEletronicoBinRaiz();
            CertificadoEletronicoBin certificadoEletronicoBinUsuarioLogado = certificadoEletronicoService.getCertificadoEletronicoBin(certificadoEletronicoUsuarioLogado.getId());
            br.com.infox.assinatura.assinador.Assinador assinador = AssinadorFactory.getDefault();
            X509Certificate certificate = CertUtil.getCertificate(certificadoEletronicoBinRaiz.getCrt());
            X509Certificate certificate2 = CertUtil.getCertificate(certificadoEletronicoBinUsuarioLogado.getCrt());
            KeyPair keyPair = CertUtil.getKeyPair(certificadoEletronicoBinUsuarioLogado.getKey(), certificadoEletronicoUsuarioLogado.getSenha());
            Certificate[] certChain = {certificate2, certificate};
            assinador.setCertificateChain(certChain);
            assinador.setPrivateKey(keyPair.getPrivate());
            assinador.setContentHashed(true);
            assinador.setPoliticaAssinatura(politicaDeAssinatura);

            AssinavelProvider assinavelProvider = button.getAssinavelProvider();
            assinadorService = Beans.getReference(AssinadorService.class);
            String token = assinadorService.criarListaAssinaveisAssinaturaEletronica(assinavelProvider);
            for (UUID uuidAssinavel : assinadorService.listarAssinaveis(token)) {
                byte[] dataToSign = assinadorService.getSha256(token, uuidAssinavel);
                SimpleSignableIO dataIO = new SimpleSignableIO(dataToSign, true, false).signWith(assinador);
                assinadorService.setAssinaturaAssinavel(token, uuidAssinavel, dataIO.getSignature());
            }
            Beans.getReference(AssinadorController.class).assinaturasRecebidas(token, button.getCallbackHandler());

            button.setStatus(null);
            button.setToken(null);
            button.setCurrentPhase(null);
        } finally {
            if (certificadoEletronicoService != null) {
                Beans.destroy(certificadoEletronicoService);
            }
            if (assinadorService != null) {
                Beans.destroy(assinadorService);
            }
        }
    }

    private boolean autenticar(PessoaFisica pessoaAssinante, String password) {
        LoginService loginService = null;
        try {
            loginService = Beans.getReference(LoginService.class);
            String usuario = Optional.ofNullable(pessoaAssinante).map(PessoaFisica::getUsuarioLogin).map(UsuarioLogin::getLogin).orElse(null);
            return loginService.autenticar(usuario, password);
        } finally {
            if (loginService != null) {
                Beans.destroy(loginService);
            }
        }
    }

    private void clickEvent(AssinadorClickEvent evt) {
        Assinador button = (Assinador) evt.getComponent();
        String tokenValue = jndi(AssinadorController.class).criarGrupoAssinatura(button.getAssinavelProvider());
        button.setToken(tokenValue);
        button.setCurrentPhase(SignPhase.AFTER_CLICK);
    }

    private <T> T jndi(Class<T> type, Annotation... annotations) {
        return Beans.getReference(type, annotations);
    }

}