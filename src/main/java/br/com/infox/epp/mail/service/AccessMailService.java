package br.com.infox.epp.mail.service;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.mail.Contact;
import br.com.infox.core.mail.EMailBean;
import br.com.infox.core.mail.MailSender;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.exception.BusinessException;

@Stateless
@Name(AccessMailService.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
@Transactional
public class AccessMailService {

    private static final String EXPRESSION_PATTERN = "<{0}>";
    private static final String USUARIO_MAIL_DEFAULT_FIELD_SENHA = "usuario.mail.defaultField.senha";
    private static final String USUARIO_MAIL_DEFAULT_FIELD_NOME = "usuario.mail.defaultField.nome";
    private static final String DEFAULT_EMAIL_LINE = "<div>{0} <span style='color:red'><{1}></span></div>";
    private static final String CAMPO_LOGIN = "loginUsuarioRec";
    private static final String CAMPO_SENHA = "senhaUsuarioRec";
    private static final String CAMPO_USUARIO = "nomeUsuarioRec";
    private static final String MODELO_COM_LOGIN = "tituloModeloEmailMudancaSenhaComLogin";
    private static final String MODELO_SEM_LOGIN = "tituloModeloEmailMudancaSenha";
    private static final String EMAIL = "email";
    private static final String LOGIN = "login";

    public static final String NAME = "accessMailService";

    @In
    private ModeloDocumentoManager modeloDocumentoManager;
    @In
    private ParametroManager parametroManager;
    @In(create = true)
    private EMailData emailData;
    @In
    private String nomeSistema;
    @In
    private String emailSistema;
    @In
    private InfoxMessages infoxMessages;

    private String resolveTipoDeEmail(final String parametro) {
        String nomeParam = null;
        if (AccessMailService.LOGIN.equals(parametro)) {
            nomeParam = AccessMailService.MODELO_SEM_LOGIN;
        } else if (AccessMailService.EMAIL.equals(parametro)) {
            nomeParam = AccessMailService.MODELO_COM_LOGIN;
        }
        return nomeParam;
    }

    /**
     * Envia um e-mail informando a nova senha para o usuário que solicitou a
     * mudança
     *
     * @param parametro
     *            o tipo de modelo a ser utilizado (com/sem login)
     * @param usuario
     *            o usuário que solicitou a mudança
     * @param password
     *            a senha gerada
     * @throws BusinessException
     *             caso não seja possível enviar o e-mail
     * */
    public void enviarEmailDeMudancaDeSenha(final String parametro, final UsuarioLogin usuario, final String password) {
        final String nomeParametro = resolveTipoDeEmail(parametro);
        final ModeloDocumento modelo = findModelo(nomeParametro);
        enviarEmailModelo(modelo, usuario, password);
    }

    private ModeloDocumento findModelo(final String nomeParametro) {
        Parametro parametro = this.parametroManager.getParametro(nomeParametro);
        ModeloDocumento result = null;
        if (parametro != null) {
            final String nomeModelo = parametro.getValorVariavel();
            if ((nomeModelo != null) && !"false".equals(nomeModelo)) {
                result = this.modeloDocumentoManager.getModeloDocumentoByTitulo(nomeModelo);
            }
        }
        if (result == null) {
            result = new ModeloDocumento();
            final StringBuilder defaultEmail = new StringBuilder();
            defaultEmail.append(MessageFormat.format(AccessMailService.DEFAULT_EMAIL_LINE, infoxMessages.get(AccessMailService.USUARIO_MAIL_DEFAULT_FIELD_NOME),
                    AccessMailService.CAMPO_USUARIO));
            defaultEmail.append(MessageFormat.format(AccessMailService.DEFAULT_EMAIL_LINE, infoxMessages.get(AccessMailService.USUARIO_MAIL_DEFAULT_FIELD_SENHA), 
                    AccessMailService.CAMPO_SENHA));
            result.setModeloDocumento(defaultEmail.toString());
        }
        return result;
    }

    private Session getEmailSession() {
        Session session = null;
        try {
            final InitialContext context = new InitialContext();
            session = (Session) context.lookup("java:jboss/mail/epp");
        } catch (final NamingException e) {
            throw new IllegalArgumentException(e);
        }
        return session;
    }

    private void enviarEmailModelo(final ModeloDocumento modelo, final UsuarioLogin usuario, final String password) {
        String conteudo = resolverConteudo(modelo, usuario, password);
        EMailBean mail = new EMailBean();
        mail.setUseHtmlBody(true);
        mail.setBody(conteudo);
        mail.getReceivers().clear();
        mail.getReceivers().add(new Contact(usuario.getNomeUsuario(), usuario.getEmail()));
        mail.setSubject(infoxMessages.get("usuario.senha.generated.subject"));
        mail.setSender(new Contact(this.nomeSistema, this.emailSistema));
        try {
            MailSender.sendMail(mail, getEmailSession());
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new ApplicationException(infoxMessages.get("mail.send.fail"), e);
        }
    }

    private String resolverConteudo(final ModeloDocumento modelo, final UsuarioLogin usuario, final String password) {
        String modeloDocumento = modelo.getModeloDocumento();
        modeloDocumento = this.modeloDocumentoManager.evaluateModeloDocumento(modelo);
        modeloDocumento = modeloDocumento.replace(MessageFormat.format(AccessMailService.EXPRESSION_PATTERN, AccessMailService.CAMPO_USUARIO), usuario.getNomeUsuario());
        modeloDocumento = modeloDocumento.replace(MessageFormat.format(AccessMailService.EXPRESSION_PATTERN, AccessMailService.CAMPO_LOGIN), usuario.getLogin());
        modeloDocumento = modeloDocumento.replace(MessageFormat.format(AccessMailService.EXPRESSION_PATTERN, AccessMailService.CAMPO_SENHA), password);
        return modeloDocumento;
    }

}
