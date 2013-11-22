/*
  IBPM - Ferramenta de produtividade Java
  Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.
 
  Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
  sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
  Free Software Foundation; versão 2 da Licença.
  Este programa é distribuído na expectativa de que seja útil, porém, SEM 
  NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
  ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
  
  Consulte a GNU GPL para mais detalhes.
  Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
  veja em http://www.gnu.org/licenses/  
*/

package br.com.infox.epp.processo.documento.home;

import java.util.Date;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.home.UsuarioHome;
import br.com.infox.epp.documento.home.DocumentoBinHome;
import br.com.infox.epp.processo.documento.api.IProcessoDocumentoBinHome;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.FileHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.Crypto;

@Name(ProcessoDocumentoBinHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoBinHome 
		extends AbstractHome<ProcessoDocumentoBin> 
		implements IProcessoDocumentoBinHome {

	private static final long serialVersionUID = 1L;
	private String certChain;
	private String signature;
	
	public static final String NAME = "processoDocumentoBinHome";
	
	private static final int TAMANHO_MAXIMO_ARQUIVO = 1572864;
    private ProcessoDocumento processoDocumento;
    private boolean isModelo;
    private boolean ignoraConteudoDocumento = Boolean.FALSE;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoBinHome.class);
	
	public static ProcessoDocumentoBinHome instance() {
		return ComponentUtil.getComponent("processoDocumentoBinHome");
	}

	public String getSignature() {
		return signature;
	}

	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getCertChain() {
		return certChain;
	}

	private boolean isValidSignature() {
	    if (signature == null) {
	        return false;
	    }
	    if (certChain == null) {
	        return false;
	    }
	    return !"".equals(signature.trim()) && !"".equals(certChain.trim());
	}
	
	public void assinarDocumento(ProcessoDocumento processoDocumento) {
	    FacesMessages.instance().clear();
	    if (isValidSignature()) {
	        setId(processoDocumento.getProcessoDocumentoBin().getIdProcessoDocumentoBin());
            processoDocumento.setLocalizacao(Authenticator.getLocalizacaoAtual());
            processoDocumento.setPapel(Authenticator.getPapelAtual());
            instance.setUsuarioUltimoAssinar(Authenticator.getUsuarioLogado().getNome());
            instance.setSignature(signature);
            instance.setCertChain(certChain);
            instance.setDataInclusao(new Date());
            processoDocumento.setProcessoDocumentoBin(instance);
            getEntityManager().merge(processoDocumento);
            getEntityManager().flush();
            FacesMessages.instance().add(Messages.instance().get("assinatura.assinadoSucesso"));
	    } else {
            FacesMessages.instance().add(Messages.instance().get("assinatura.falhaAssinatura"));
	    }
	}
	
	public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    public ProcessoDocumento getProcessoDocumento() {
        return processoDocumento;
    }
    
    public boolean isModelo() {
        return isModelo;
    }
    
    public void isModelo(boolean isModelo) {
        this.isModelo = isModelo;
    }
    
    @Override
    public void newInstance() {
        FileHome.instance().clear();
        super.newInstance();
    }
    
    public boolean isModeloVazio() {
        boolean modeloVazio = isModeloVazio(getInstance());
        if (modeloVazio) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O modelo está vazio.");
        }
        return modeloVazio;
    }
    
    public static boolean isModeloVazio(ProcessoDocumentoBin bin) {
        return bin == null || Strings.isEmpty(bin.getModeloDocumento()) || Strings.isEmpty(removeTags(bin.getModeloDocumento()));
    }

    private static String removeTags(String modelo) {
        return modelo.replaceAll("\\<.*?\\>", "")
                    .replaceAll("\n", "").replaceAll("\r", "")
                    .replaceAll("&nbsp;", "");
    }   
    
    @Override
    protected boolean beforePersistOrUpdate() {
        boolean ret = true;
        if (isModelo) {
            if (!ignoraConteudoDocumento && isModeloVazio()) {
                ret = false;
            }
            if(ret){
                getInstance().setUsuario(Authenticator.getUsuarioLogado());
                getInstance().setMd5Documento(Crypto.encodeMD5(getInstance().getModeloDocumento()));
            }
        }
        return ret;
    }
    
    @Override
    public String persist() 
    {
        String ret = null;
        if (isModelo) {
            ret = super.persist();
        } else {
            FileHome file = FileHome.instance();
            if (isDocumentoBinValido(file)) {
                ProcessoDocumentoBin instance = getInstance();
                instance.setUsuario(Authenticator.getUsuarioLogado());
                instance.setExtensao(file.getFileType());
                instance.setMd5Documento(file.getMD5());
                instance.setNomeArquivo(file.getFileName());
                instance.setSize(file.getSize());
                instance.setModeloDocumento(null);
                ret = super.persist();

                DocumentoBinHome.instance().setData(instance.getIdProcessoDocumentoBin(), file.getData());
            }
        }
        if (ret == null) {
            FacesMessages.instance().add(Severity.ERROR, "Erro ao gravar o documento.");
        } else {
            FacesMessages.instance().clear();
            if (getInstance().getUsuario() != null) {
                List<ProcessoDocumentoBin> usuarioList = getInstance().getUsuario().getProcessoDocumentoBinList();
                if (!usuarioList.contains(instance)) {
                    getEntityManager().refresh(getInstance().getUsuario());
                }
            }
        }
        return ret; 
    }
    
    public String persistSemLista(){
        return super.persist();
    }
    
    private boolean isDocumentoBinValido(FileHome file) {
        if (file == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR,
                    "Nenhum documento selecionado.");
            return false;
        }
        if( !file.getFileType().equalsIgnoreCase("PDF") ) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR,
                    "O documento deve ser do tipo PDF.");
            return false;
        }
        if(file.getSize() != null && file.getSize() > TAMANHO_MAXIMO_ARQUIVO){
            FacesMessages.instance().add(StatusMessage.Severity.ERROR,
                    "O documento deve ter o tamanho máximo de 1.5MB!");
            return false;
        }
        return true;
    }

    public void setProcessoDocumentoBinIdProcessoDocumentoBin(Integer id) {
        setId(id);
    }

    public Integer getProcessoDocumentoBinIdProcessoDocumentoBin() {
        return (Integer) getId();
    }

    @Override
    protected ProcessoDocumentoBin createInstance() {
        ProcessoDocumentoBin processoDocumentoBin = new ProcessoDocumentoBin();
        processoDocumentoBin.setUsuario(Authenticator.getUsuarioLogado());
        return processoDocumentoBin;
    }

    @Override
    public String remove() {
        UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        usuarioLogin.getProcessoDocumentoBinList().remove(instance);
        return super.remove();
    }

    @Override
    public String remove(ProcessoDocumentoBin obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        return ret;
    }

    public List<ProcessoDocumento> getProcessoDocumentoList() {
        return getInstance() == null ? null : getInstance()
                .getProcessoDocumentoList();
    }
    
    public String setDownloadInstance() {
        exportData();
        return "/download.xhtml";
    }
    
    public void exportData() {
        FileHome file = FileHome.instance();
        String fileName = "ProcessoDocumentoBin";
        String key = Messages.instance().get("processoDocumentoBin.textColumn");
        if (key != null) {
            String expression = "#{processoDocumentoBinHome.instance." + key + "}";
            fileName = (String) Expressions.instance().createValueExpression(
                    expression).getValue();
        }
        file.setFileName(fileName);
        try {
            file.setData( DocumentoBinHome.instance().getData(getInstance().getIdProcessoDocumentoBin()) );
        } catch (Exception e) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro ao descarregar o documento.");
            LOG.error(".exportData()", e);
        }
        Contexts.getConversationContext().set("fileHome", file);
    }

    public void setIgnoraConteudoDocumento(boolean ignoraConteudoDocumento) {
        this.ignoraConteudoDocumento = ignoraConteudoDocumento;
    }

    public boolean isIgnoraConteudoDocumento() {
        return ignoraConteudoDocumento;
    }
	
}