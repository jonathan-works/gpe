package br.com.infox.epp.mail.service;

import static java.text.MessageFormat.format;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.Messages;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.command.SendmailCommand;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.exception.BusinessException;

@Name(AccessMailService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
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

    private String resolveTipoDeEmail(String parametro) {
        String nomeParam = null;
        if (LOGIN.equals(parametro)) {
            nomeParam = MODELO_SEM_LOGIN;
        } else if (EMAIL.equals(parametro)) {
            nomeParam = MODELO_COM_LOGIN;
        }
        return nomeParam;
    }

    /**
     * Envia um e-mail informando a nova senha para o usuário que solicitou a mudança
     * 
     * @param parametro o tipo de modelo a ser utilizado (com/sem login)
     * @param usuario o usuário que solicitou a mudança
     * @param password a senha gerada
     * 
     * @throws BusinessException caso não seja possível enviar o e-mail
     * */
    public void enviarEmailDeMudancaDeSenha(final String parametro,
            final UsuarioLogin usuario, final String password) {
        final String nomeParametro = resolveTipoDeEmail(parametro);
        final ModeloDocumento modelo = findModelo(nomeParametro);
        enviarEmailModelo(modelo, usuario, password);
    }

    private ModeloDocumento findModelo(String nomeParametro) {
        final Parametro parametro = parametroManager.getParametro(nomeParametro);
        ModeloDocumento result = null;
        if (parametro != null) {
            final String nomeModelo = parametro.getValorVariavel();
            if (nomeModelo != null && !"false".equals(nomeModelo)) {
                result = modeloDocumentoManager.getModeloDocumentoByTitulo(nomeModelo);
            }
        }
        if (result == null) {
            result = new ModeloDocumento();
            StringBuilder defaultEmail = new StringBuilder();
            Map<String, String> localeMsgs = Messages.instance();
            defaultEmail.append(format(DEFAULT_EMAIL_LINE, localeMsgs.get(USUARIO_MAIL_DEFAULT_FIELD_NOME), CAMPO_USUARIO));
            defaultEmail.append(format(DEFAULT_EMAIL_LINE, localeMsgs.get(USUARIO_MAIL_DEFAULT_FIELD_SENHA), CAMPO_SENHA));
            result.setModeloDocumento(defaultEmail.toString());
        }
        return result;
    }

    private void enviarEmailModelo(ModeloDocumento modelo,
            UsuarioLogin usuario, String password) {
        String conteudo = resolverConteudo(modelo, usuario, password);

        emailData.setUseHtmlBody(true);
        emailData.setBody(conteudo);
        emailData.getRecipientList().clear();
        emailData.getRecipientList().add(usuario);
        emailData.setSubject(Messages.instance().get("usuario.senha.generated.subject"));
        new SendmailCommand().execute("/WEB-INF/email/emailTemplate.xhtml");
    }

    private String resolverConteudo(ModeloDocumento modelo,
            UsuarioLogin usuario, String password) {
        String modeloDocumento = modelo.getModeloDocumento();

        modeloDocumento = modeloDocumentoManager.evaluateModeloDocumento(modelo);
        modeloDocumento = modeloDocumento.replace(format(EXPRESSION_PATTERN, CAMPO_USUARIO), usuario.getNomeUsuario());
        modeloDocumento = modeloDocumento.replace(format(EXPRESSION_PATTERN, CAMPO_LOGIN), usuario.getLogin());
        modeloDocumento = modeloDocumento.replace(format(EXPRESSION_PATTERN, CAMPO_SENHA), password);
        return modeloDocumento;
    }

}
