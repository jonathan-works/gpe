package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.UICollapsiblePanel;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DestinatarioModeloComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;

@Name(ExpedicaoComunicacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ExpedicaoComunicacaoAction implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "expedicaoComunicacaoAction";
	private static final String PAINEL_COMUNICACAO_ID = ":comunicacaoTabPanel:comunicacaoForm:painelComunicacao";
	
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private DestinatarioModeloComunicacaoList destinatarioModeloComunicacaoList;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	
	private String tab = "list";
	private ModeloComunicacao modeloComunicacao;
	private DestinatarioModeloComunicacao destinatario;
	private String comunicacao;
	
	public String getTab() {
		return tab;
	}
	
	public void setTab(String tab) {
		this.tab = tab;
	}
	
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
		destinatarioModeloComunicacaoList.setModeloComunicacao(modeloComunicacao);
	}
	
	public void setId(Long id) {
		if (id == null) {
			setModeloComunicacao(null);
		} else if (modeloComunicacao == null || !modeloComunicacao.getId().equals(id)) {
			setModeloComunicacao(modeloComunicacaoManager.find(id));
		}
	}
	
	public Long getId() {
		return modeloComunicacao == null ? null : modeloComunicacao.getId();
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}
	
	public String getNomeDestinatario(DestinatarioModeloComunicacao destinatario) {
		if (destinatario == null) {
			return null;
		} else if (destinatario.getDestinatario() != null) {
			return destinatario.getDestinatario().getNome();
		} else {
			return destinatario.getLocalizacaoDestinataria().getCaminhoCompletoFormatado();
		}
	}
	
	public String getNomeDestinatario() {
		return getNomeDestinatario(this.destinatario);
	}
	
	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
		this.comunicacao = null;
		UICollapsiblePanel panel = (UICollapsiblePanel) FacesContext.getCurrentInstance().getViewRoot().findComponent(PAINEL_COMUNICACAO_ID);
		if (destinatario != null) {
			panel.setExpanded(true);
		} else {
			panel.setExpanded(false);
		}
	}
	
	public String getComunicacao() {
		if (comunicacao == null) {
			String modeloDocumento = modeloComunicacao.getComunicacao().getModeloDocumento();
			if (destinatario == null) {
				comunicacao = modeloDocumento;
			} else {
				comunicacao = modeloDocumentoManager.evaluateModeloDocumento(modeloComunicacao.getModeloDocumento(), modeloDocumento, null);
			}
		}
		return comunicacao;
	}
	
	public void setComunicacao(String comunicacao) {
		this.comunicacao = comunicacao;
	}
	
	public boolean podeRenderizarApplet() {
		UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
		UsuarioLogin usuario = usuarioPerfil.getUsuarioLogin();
		Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
		return assinaturaDocumentoService.podeRenderizarApplet(papel, modeloComunicacao.getClassificacaoComunicacao(), modeloComunicacao.getComunicacao(), usuario);
	}
	
	public void expedirComunicacao(DestinatarioModeloComunicacao destinatario) {
		
	}
}
