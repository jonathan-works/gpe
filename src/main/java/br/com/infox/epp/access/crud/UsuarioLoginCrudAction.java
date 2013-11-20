package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.util.RandomStringUtils;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.documento.action.ModeloDocumentoAction;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.command.SendmailCommand;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin> {
    
    public static final String NAME = "usuarioLoginCrudAction";
    private static final int PASSWORD_LENGTH = 8;
    
    private BloqueioUsuario novoBloqueio;
    
    @In private UsuarioLoginManager usuarioLoginManager;
    @In private PessoaManager pessoaManager;
    @In private ModeloDocumentoManager modeloDocumentoManager;

    private boolean pessoaFisicaCadastrada;
    private String password;
    
    @Override
    public void newInstance() {
        newBloqueioUsuario();
        super.newInstance();
        getInstance().setBloqueio(false);
        getInstance().setProvisorio(false);
    }

    private void newBloqueioUsuario() {
        novoBloqueio = new BloqueioUsuario();
    }
    
    @Override
    public void setId(Object id) {
        super.setId(id);
        newBloqueioUsuario();
    }
    
    @Override
    protected boolean beforeSave() {
        // TODO Auto-generated method stub
        return super.beforeSave();
    }
    
    @Override
    public String save() {
        String resultado;
        if (!pessoaFisicaCadastrada){
            resultado = super.save();
        } else{
            PessoaFisica pf = EntityUtil.find(PessoaFisica.class, getInstance().getIdPessoa());
            usuarioLoginManager.inserirUsuarioParaPessoaFisicaCadastrada(getInstance());
            EntityUtil.getEntityManager().detach(pf);
            setInstance(usuarioLoginManager.getUsuarioLogin(getInstance()));
            resultado = "persisted";
            afterSave(resultado);
        }
        return resultado;
    }
    
    @Override
    protected void afterSave(String ret) {
        if (getInstance().getSenha() == null || ParametroUtil.LOGIN_USUARIO_EXTERNO.equals(getInstance().getLogin())) {
            gerarNovaSenha();
        }
        if (getInstance() instanceof PessoaFisica){
            try {
                setInstance(EntityUtil.cloneEntity(getInstance(), false));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            getInstance().loadDataFromPessoaFisica(getInstance());
        };
        super.afterSave(ret);
    }
    
    public void gerarNovaSenha() {
        gerarNovaSenha("email");
    }
    
    //TODO refatorar a relação Password / Senha
    public void gerarNovaSenha(String parametro) {
        if (ParametroUtil.LOGIN_USUARIO_EXTERNO.equals(getInstance().getLogin())) {
            getInstance().setSenha("");
        } else {
            setPassword(RandomStringUtils.randomAlphabetic(PASSWORD_LENGTH));
            getInstance().setSenha(getPassword());
        }
        new RunAsOperation(true) {
            @Override
            public void execute() {
                IdentityManager.instance().changePassword(getInstance().getLogin(), getInstance().getSenha());
            }
        }.run();
        
        EntityUtil.getEntityManager().flush();
        
        iniciarRequisicao(parametro);
    }
    
    /**
     * Inicia o processo de requisição de senha
     * 
     * Requisita nova senha baseada na informação fornecida pelo usuário e tenta
     * enviar com base na informação recuperada
     * {@link #enviarModeloPorNome(String)}.
     * 
     * TODO:melhorar nome do método
     * 
     * @param parametro
     *            Tipo da requisição de senha
     */
    private void iniciarRequisicao(String parametro) {
        String nomeParam = null;
        if ("login".equals(parametro)) {
            nomeParam = "tituloModeloEmailMudancaSenha";
        } else if ("email".equals(parametro)) {
            nomeParam = "tituloModeloEmailMudancaSenhaComLogin";
        }

        String nomeModelo = ParametroUtil.getParametroOrFalse(nomeParam);

        if (!enviarModeloPorNome(nomeModelo)) {
            FacesMessages.instance().add(
                    StatusMessage.Severity.ERROR,
                    "Erro no envio do e-mail. O parâmetro de sistema '"
                            + nomeParam
                            + "' não foi definido ou possui um valor inválido");
        }
    }
    
    /**
     * Método que recupera um modelo de documento pelo seu nome e envia
     * {@link #enviarEmailModelo(ModeloDocumento)}
     * 
     * @param nomeModeloDocumento
     *            Nome do Modelo de Documento a enviar por e-mail
     * @return true se o e-mail for enviado e false se falhar
     */
    private boolean enviarModeloPorNome(String nomeModeloDocumento) {
        if (nomeModeloDocumento == null || "false".equals(nomeModeloDocumento)) {
            return false;
        }
        ModeloDocumento modelo = modeloDocumentoManager.getModeloDocumentoByTitulo(nomeModeloDocumento);
        if (modelo == null) {
            return false;
        } else {
            enviarEmailModelo(modelo);
        }

        return true;
    }
    
    /**
     * Envia e-mail baseado em um Modelo de Documento
     * 
     * @param modelo
     *            Modelo do e-mail a ser enviado
     */
    private void enviarEmailModelo(ModeloDocumento modelo) {
         if (modelo == null) {
            return;
        }
        pessoaFisicaCadastrada = false;
        String conteudo = ModeloDocumentoAction.instance().getConteudo(modelo);

        EMailData data = ComponentUtil.getComponent(EMailData.NAME);
        data.setUseHtmlBody(true);
        data.setBody(conteudo);
        data.getRecipientList().clear();
        data.getRecipientList().add(getInstance());
        data.setSubject("Senha do Sistema");
        FacesMessages.instance().add("Senha gerada com sucesso.");
        new SendmailCommand().execute("/WEB-INF/email/emailTemplate.xhtml");
    }
    
    @Override
    protected void afterSave() {
        newBloqueioUsuario();
        super.afterSave();
    }
    
    public void searchByCpf(String cpf){
        newInstance();
        UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioLoginByCpf(cpf);
        if (usuarioLogin != null){
            setInstance(usuarioLogin);
        } else{
            feedFromPessoaFisica(cpf);
        }
    }

    /**
     * @param cpf
     */
    private void feedFromPessoaFisica(String cpf) {
        PessoaFisica pessoaFisica = pessoaManager.getPessoaFisicaByCpf(cpf);
        if (pessoaFisica != null){
            pessoaFisicaCadastrada = true;
            setInstance(getInstance().loadDataFromPessoaFisica(pessoaFisica));
        }
        else {
            pessoaFisicaCadastrada = false;
        }
    }
    
// ------------------- Getters and Setters ---------------------\\
    
    public BloqueioUsuario getNovoBloqueio() {
        return novoBloqueio;
    }

    public void setNovoBloqueio(BloqueioUsuario novoBloqueio) {
        this.novoBloqueio = novoBloqueio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
