package br.com.infox.epp.mail.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.exception.BusinessException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.command.SendmailCommand;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.itx.util.ComponentUtil;

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
    
    private String resolveTipoDeEmail(String parametro) {
        String nomeParam = null;
        if (LOGIN.equals(parametro)) {
            nomeParam = MODELO_SEM_LOGIN;
        } else if (EMAIL.equals(parametro)) {
            nomeParam = MODELO_COM_LOGIN;
        }
        return nomeParam;
    }
    
    public void enviarEmailDeMudancaDeSenha(String parametro, UsuarioLogin usuario, String password) throws BusinessException {
        String nomeParametro = resolveTipoDeEmail(parametro);
        ModeloDocumento modelo = findModelo(nomeParametro);
        if (modelo != null) {
            enviarEmailModelo(modelo, usuario, password);
        } else {
            lancarErroDeParametroInvalido(nomeParametro);
        }
    }

    private void lancarErroDeParametroInvalido(String nomeParametro) throws BusinessException {
        throw new BusinessException("Erro no envio do e-mail. O parâmetro de sistema '"
                + nomeParametro
                + "' não foi definido ou possui um valor inválido");
    }
    
    private ModeloDocumento findModelo(String nomeParametro){
        String nomeModelo = ParametroUtil.getParametroOrFalse(nomeParametro);
        if (nomeModelo == null || "false".equals(nomeModelo)) {
            return null;
        }
        return modeloDocumentoManager.getModeloDocumentoByTitulo(nomeModelo);
    }
    
    private void enviarEmailModelo(ModeloDocumento modelo, UsuarioLogin usuario, String password) {
        
        String conteudo = resolverConteudo(modelo, usuario, password);

        EMailData data = ComponentUtil.getComponent(EMailData.NAME);
        data.setUseHtmlBody(true);
        data.setBody(conteudo);
        data.getRecipientList().clear();
        data.getRecipientList().add(usuario);
        data.setSubject("Senha do Sistema");
        FacesMessages.instance().add("Senha gerada com sucesso.");
        new SendmailCommand().execute("/WEB-INF/email/emailTemplate.xhtml");
    }

    private String resolverConteudo(ModeloDocumento modelo, UsuarioLogin usuario, String password) {
        String conteudo = modeloDocumentoManager.evaluateModeloDocumento(modelo);
        conteudo = substitute(conteudo, CAMPO_USUARIO, usuario.getNome());
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
