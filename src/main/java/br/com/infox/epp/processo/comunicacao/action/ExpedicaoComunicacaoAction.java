package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.richfaces.component.UICollapsiblePanel;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ArbitraryExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.JbpmExpressionResolver;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DestinatarioModeloComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.ibpm.task.home.VariableTypeResolver;

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
	@In
	private VariableTypeResolver variableTypeResolver;
	@In("org.jboss.seam.bpm.jbpmContext")
	private JbpmContext jbpmContext;
	
	private String tab = "list";
	private ModeloComunicacao modeloComunicacao;
	private DestinatarioModeloComunicacao destinatario;
	private String comunicacao;
	private Map<Long, Map<String, String>> variaveisDestinatarios = new HashMap<>();
	
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
			if (!variaveisDestinatarios.containsKey(destinatario.getId())) {
				variaveisDestinatarios.put(destinatario.getId(), new HashMap<String, String>());
			}
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
				ArbitraryExpressionResolver arbitraryExpressionResolver = new ArbitraryExpressionResolver(variaveisDestinatarios.get(destinatario.getId()));
				
				ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(modeloComunicacao.getProcesso().getIdJbpm());
				variableTypeResolver.setProcessInstance(processInstance);
				JbpmExpressionResolver jbpmExpressionResolver = new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), processInstance.getContextInstance());
				
				SeamExpressionResolver seamExpressionResolver = new SeamExpressionResolver();
				
				ExpressionResolverChain chain = new ExpressionResolverChain(arbitraryExpressionResolver, jbpmExpressionResolver, seamExpressionResolver);
				comunicacao = modeloDocumentoManager.evaluateModeloDocumento(modeloComunicacao.getModeloDocumento(), modeloDocumento, chain);
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
