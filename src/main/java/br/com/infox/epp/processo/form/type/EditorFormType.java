package br.com.infox.epp.processo.form.type;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler.FileConfig;
import br.com.infox.seam.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;

public class EditorFormType extends FileFormType {

	@Getter@Setter
	private ModeloDocumentoValueChange modeloDocumentoValueChange;
	
    public EditorFormType() {
        super("editor", "/Processo/form/editor.xhtml");
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        super.performValue(formField, formData);
        List<ModeloDocumento> modelos = readModelosDocumento(formField, formData);
        formField.addProperty("modelosDocumento", modelos);
        formField.addProperty("modeloDocumento", null);
        Documento documento = formField.getTypedValue(Documento.class);
        if (documento != null && documento.getId() != null) {
            formField.addProperty("classificacaoDocumento", documento.getClassificacaoDocumento());
            formField.addProperty("conteudo", documento.getDocumentoBin().getModeloDocumento());
        }
    }
    
    public void performModeloDocumento(FormField formField, FormData formFata) {
        ModeloDocumento modeloDocumento = formField.getProperty("modeloDocumento", ModeloDocumento.class);
        String evaluatedModelo = "";
        if (modeloDocumento != null) {
            evaluatedModelo = getModeloDocumentoManager().evaluateModeloDocumento(modeloDocumento, formFata.getExpressionResolver());
        }
        formField.addProperty("conteudo", evaluatedModelo);
    }
    
    public void clearModeloDocumentoSelecionado(FormField formField) {
    	formField.addProperty("modeloDocumento",modeloDocumentoValueChange.getOldValue());
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
        try{
            super.performUpdate(formField, formData);
            Documento documento = formField.getTypedValue(Documento.class);
            ClassificacaoDocumento classificacaoDocumento = formField.getProperty("classificacaoDocumento", ClassificacaoDocumento.class);
            String conteudo = formField.getProperty("conteudo", String.class);
            Pasta pasta = formField.getProperty("pasta", Pasta.class);
            
            if(pasta == null){ //nao informou pasta pega a padrao
                pasta = getPastaManager().getDefault(formData.getProcesso());
            }
            
            if ( documento.getId() != null ) {
                documento.getDocumentoBin().setModeloDocumento(conteudo == null ? "" : conteudo);
                documento.setClassificacaoDocumento(classificacaoDocumento);
                documento.setPasta(pasta);
                documento = getDocumentoManager().update(documento);
                formField.setValue(documento);
            } else {
                if ( classificacaoDocumento != null ) {
                    documento.setDescricao(formField.getLabel());
                    documento.getDocumentoBin().setModeloDocumento(conteudo == null ? "" : conteudo);
                    documento.setClassificacaoDocumento(classificacaoDocumento);
                    documento.setPasta(pasta);
                    getDocumentoBinManager().createProcessoDocumentoBin(documento);
                    documento = getDocumentoManager().gravarDocumentoNoProcesso(formData.getProcesso(), documento);
                    formField.setValue(documento);
                }
            }
            formField.addProperty("modeloDocumento", null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public Object convertToFormValue(Object value) {
        if (value == null) {
            Documento documento = createNewDocumento();
            return documento;
        } else {
            if (value instanceof String) {
                value = Integer.valueOf((String) value);
            }
            if (value instanceof Integer) {
                Documento documento = getDocumentoManager().find((Integer) value);
                return documento;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + value + " to Documento");
    }
    
    @Override
    public boolean isInvalid(FormField formField, FormData formData) throws BusinessException {
        if (formField.isRequired()) {
            ClassificacaoDocumento classificacao = formField.getProperty("classificacaoDocumento", ClassificacaoDocumento.class);
            if (classificacao == null) {
                FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(),
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                                InfoxMessages.getInstance().get("beanValidation.classificacaoNaoInformada")));
                return true;
            }

            String conteudo = formField.getProperty("conteudo", String.class);

            if (StringUtil.isEmpty(conteudo)) {
                FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(),
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                                InfoxMessages.getInstance().get("beanValidation.documentoSemConteudo")));
                return true;
            }
        }
        return super.isInvalid(formField, formData);
    }

    private Documento createNewDocumento() {
        DocumentoBin documentoBin = new DocumentoBin();
        documentoBin.setMinuta(false);
        documentoBin.setModeloDocumento("");
        Documento documento = new Documento();
        documento.setAnexo(false);
        documento.setDocumentoBin(documentoBin);
        return documento;
    }
    
    private List<ModeloDocumento> readModelosDocumento(FormField formField, FormData formData) {
    	String configuration = (String) formField.getProperties().get("configuration");
    	List<ModeloDocumento> modelos = new ArrayList<>();
    	if (configuration != null && !configuration.isEmpty()) {
    		FileConfig editorConfiguration = VariableEditorModeloHandler.fromJson(configuration);
    		if (editorConfiguration.getCodigosModeloDocumento() != null && !editorConfiguration.getCodigosModeloDocumento().isEmpty()) {
    			modelos = getModeloDocumentoSearch().getModeloDocumentoListByListCodigos(editorConfiguration.getCodigosModeloDocumento());
    		}
        }
        return modelos;
    }
    
    protected ModeloDocumentoManager getModeloDocumentoManager() {
        return BeanManager.INSTANCE.getReference(ModeloDocumentoManager.class);
    }
    
    protected ModeloDocumentoSearch getModeloDocumentoSearch() {
    	return BeanManager.INSTANCE.getReference(ModeloDocumentoSearch.class);
    }

}
