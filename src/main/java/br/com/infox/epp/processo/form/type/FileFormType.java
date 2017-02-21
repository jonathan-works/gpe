package br.com.infox.epp.processo.form.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.dao.PastaDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler;
import br.com.infox.seam.exception.BusinessException;

public abstract class FileFormType implements FormType {
    
    protected String name;
    protected String path;
    
    public FileFormType(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.FILE;
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
    	String configuration = (String) formField.getProperties().get("configuration");
    	List<String> codigos = null;
        if (configuration != null && !configuration.isEmpty()) {
            codigos = VariableEditorModeloHandler.fromJson(configuration).getCodigosClassificacaoDocumento(); 
        } 
        List<ClassificacaoDocumento> classificacoes = getClassificacaoDocumentoFacade().getUseableClassificacaoDocumentoVariavel(codigos, false);
        formField.addProperty("classificacoesDocumento", classificacoes);
        if (classificacoes.size() == 1) {
            formField.addProperty("classificacaoDocumento", classificacoes.get(0));
        }
        
        if(configuration != null) {
        	formField.addProperty("pastaPadrao", VariableEditorModeloHandler.fromJson(configuration).getPasta());
        }
        carregarPastas(formField, formData.getProcesso());
    }
    
    private void carregarPastas(FormField formField, Processo processo) {
        List<Pasta> pastas = new ArrayList<Pasta>();
        Pasta pasta = null;
        String pastaPadraoFluxo = (String) formField.getProperties().get("pastaPadrao");
        if (pastaPadraoFluxo != null) {
            pasta = getPastaManager().getByCodigoAndProcesso(pastaPadraoFluxo, processo);
        }
        if (pasta != null) {
            // se existe pasta especifica na configuracao da variavel atribui ao documento
            pastas.add(pasta);
        } else {
            UsuarioPerfil usuario = Authenticator.getUsuarioPerfilAtual();
            Map<Integer, PastaRestricaoBean> restricoes = getPastaRestricaoManager().loadRestricoes(processo, usuario.getUsuarioLogin(),
                    usuario.getLocalizacao(), usuario.getPerfilTemplate().getPapel());
            for (Integer id : restricoes.keySet()) {
                if (Boolean.TRUE.equals(restricoes.get(id).getWrite())) {
                    Pasta pastaComPermissao = getPastaDao().find(id);
                    pastas.add(pastaComPermissao);
                }
            }
        }
        if (pastas.size() == 1) {
        	pasta = pastas.get(0);
        }
        formField.addProperty("podeSelecionarPasta", pasta == null);
        formField.addProperty("pasta", pasta);
        formField.addProperty("pastas", pastas);
    }
    
    @Override
    public boolean validate(FormField formField, FormData formData) throws BusinessException {
        if (formField.isRequired()) {

            Documento documento = formField.getTypedValue(Documento.class);
            if (documento != null && documento.getId() != null) {
                boolean assinaturaVariavelOk = validarAssinaturaDocumento(documento);
                if (!assinaturaVariavelOk) {
                    FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(),
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                                    String.format(
                                            InfoxMessages.getInstance().get("assinaturaDocumento.faltaAssinatura"),
                                            formField.getLabel())));
                    return true;
                }
            }
        }
        return false;
    }
   
    protected boolean validarAssinaturaDocumento(Documento documento) {
        Papel papel = Authenticator.getPapelAtual();
        boolean isValid = getAssinaturaDocumentoService().isDocumentoTotalmenteAssinado(documento)
                || !documento.isAssinaturaObrigatoria(papel) || documento.isDocumentoAssinado(papel);
        return isValid;
    }
    
    public boolean podeAssinar(FormField formField) {
        return false;
//        Documento documento = formField.getTypedValue(Documento.class);
//        if (documento == null){
//            return false;
//        }
//        Papel papelAtual = Authenticator.getPapelAtual();
//        boolean papelPermiteAssinaturaMultipla = documento.papelPermiteAssinaturaMultipla(papelAtual);
//        return documento != null && documento.getId() != null 
//                && (papelPermiteAssinaturaMultipla && !documento.isDocumentoAssinado(Authenticator.getUsuarioLogado())) ||
//                    (!papelPermiteAssinaturaMultipla && documento.isDocumentoAssinavel(papelAtual) && !documento.isDocumentoAssinado(papelAtual)); 
    }
    
    @Override
    public boolean isPersistable() {
        return true;
    }

    protected AssinaturaDocumentoService getAssinaturaDocumentoService() {
        return BeanManager.INSTANCE.getReference(AssinaturaDocumentoService.class);
    }
    
    protected DocumentoManager getDocumentoManager() {
        return BeanManager.INSTANCE.getReference(DocumentoManager.class);
    }
    
    protected DocumentoBinManager getDocumentoBinManager() {
        return BeanManager.INSTANCE.getReference(DocumentoBinManager.class);
    }
    
    protected ClassificacaoDocumentoFacade getClassificacaoDocumentoFacade() {
        return BeanManager.INSTANCE.getReference(ClassificacaoDocumentoFacade.class);
    }
    
    protected PastaManager getPastaManager() {
    	return BeanManager.INSTANCE.getReference(PastaManager.class);
    }
    
    protected PastaRestricaoManager getPastaRestricaoManager() {
    	return BeanManager.INSTANCE.getReference(PastaRestricaoManager.class);
    }
    
    protected PastaDAO getPastaDao() {
    	return BeanManager.INSTANCE.getReference(PastaDAO.class);
    }
    
}
