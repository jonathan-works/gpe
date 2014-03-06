package br.com.infox.epp.mail.service;

import static java.text.MessageFormat.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

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
    
    private static final String CAMPO_LOGIN = "Seu login &eacute;:";
    private static final String CAMPO_SENHA = "Sua senha nova &eacute;:";
    private static final String CAMPO_USUARIO = "Caro,";
    private static final String MODELO_COM_LOGIN = "tituloModeloEmailMudancaSenhaComLogin";
    private static final String MODELO_SEM_LOGIN = "tituloModeloEmailMudancaSenha";
    private static final String EMAIL = "email";
    private static final String LOGIN = "login";

    public static final String NAME = "accessMailService";
    
    @In private ModeloDocumentoManager modeloDocumentoManager;
    @In private ParametroManager parametroManager;
    @In(create=true) private EMailData emailData;
    
    private String resolveTipoDeEmail(String parametro) {
        String nomeParam = null;
        if (LOGIN.equals(parametro)) {
            nomeParam = MODELO_SEM_LOGIN;
        } else if (EMAIL.equals(parametro)) {
            nomeParam = MODELO_COM_LOGIN;
        }
        return nomeParam;
    }
    
    public void enviarEmailDeMudancaDeSenha(final String parametro, final UsuarioLogin usuario, final String password) throws BusinessException {
        final String nomeParametro = resolveTipoDeEmail(parametro);
        final ModeloDocumento modelo = findModelo(nomeParametro);
        if (modelo != null) {
            enviarEmailModelo(modelo, usuario, password);
        } else {
            final String errorMessage = format("Erro no envio do e-mail. O parâmetro de sistema '{0}' não foi definido ou possui um valor inválido", nomeParametro);
            throw new BusinessException(errorMessage);
        }
    }

    private ModeloDocumento findModelo(String nomeParametro){
        final Parametro parametro = parametroManager.getParametro(nomeParametro);
        ModeloDocumento result = null;
        if (parametro != null) {
            final String nomeModelo = parametro.getValorVariavel();
            if (nomeModelo != null && !"false".equals(nomeModelo)) {
                result = modeloDocumentoManager.getModeloDocumentoByTitulo(nomeModelo);
            }
        }
        return result;
    }
    
    private void enviarEmailModelo(ModeloDocumento modelo, UsuarioLogin usuario, String password) {
        String conteudo = resolverConteudo(modelo, usuario, password);

        emailData.setUseHtmlBody(true);
        emailData.setBody(conteudo);
        emailData.getRecipientList().clear();
        emailData.getRecipientList().add(usuario);
        emailData.setSubject("Senha do Sistema");
        new SendmailCommand().execute("/WEB-INF/email/emailTemplate.xhtml");
    }

    private String resolverConteudo(ModeloDocumento modelo, UsuarioLogin usuario, String password) {
        String conteudo = modeloDocumentoManager.evaluateModeloDocumento(modelo);
        conteudo = substitute(conteudo, CAMPO_USUARIO, usuario.getNomeUsuario());
        conteudo = substitute(conteudo, CAMPO_LOGIN, usuario.getLogin());
        conteudo = substitute(conteudo, CAMPO_SENHA, password);
        return conteudo;
    }

    private String substitute(String conteudoDocumento, String campo, String valor){
        Pattern pattern = Pattern.compile(campo);
        Matcher matcher = pattern.matcher(conteudoDocumento);
        return matcher.replaceFirst(campo + " " + valor);
    }

}
