package br.com.infox.epa.action.crud;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.epa.query.HistoricoModeloDocumentoQuery;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.home.ModeloDocumentoHome;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

@Name(HistoricoModeloDocumentoAction.NAME)
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
public class HistoricoModeloDocumentoAction extends AbstractHome<HistoricoModeloDocumento> {

	public static final String NAME = "historicoModeloDocumentoAction";
	private static final long serialVersionUID = 1L;

    private List<ModeloDocumento> modeloDocumentoList;
    private List<Usuario> usuarioAlteracaoList;
    private HistoricoModeloDocumento selecionado;
	
    public HistoricoModeloDocumento getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(HistoricoModeloDocumento selecionado) {
		this.selecionado = selecionado;
	}
	
	///TODO: Encontrar meio melhor de realizar este processo, funciona, mas joga uma NullPointerException vinda do Controller.debug
	public void restaurarSelecionado()	{
		if (selecionado==null)
			return;
		
		ModeloDocumento modelo = selecionado.getModeloDocumento();
		ModeloDocumentoHome modeloHome = (ModeloDocumentoHome)Component.getInstance(ModeloDocumentoHome.NAME);
		
		ModeloDocumento oldEntity = new ModeloDocumento();
		oldEntity.setAtivo(modelo.getAtivo());
		oldEntity.setIdModeloDocumento(modelo.getIdModeloDocumento());
		oldEntity.setModeloDocumento(modelo.getModeloDocumento());
		oldEntity.setTipoModeloDocumento(modelo.getTipoModeloDocumento());
		oldEntity.setTituloModeloDocumento(modelo.getTituloModeloDocumento());
		
		modeloHome.newInstance();
		modeloHome.setInstance(modelo);
		modeloHome.setOldEntity(oldEntity);
		modelo.setTituloModeloDocumento(selecionado.getTituloModeloDocumento());
		modelo.setModeloDocumento(selecionado.getDescricaoModeloDocumento());
		modelo.setAtivo(selecionado.getAtivo());
		
		modeloHome.update();
	}

	@Override
	public void create() {
		super.create();
		javax.persistence.Query query = EntityUtil.createQuery(HistoricoModeloDocumentoQuery.LIST_MODELO_QUERY);
		setModeloDocumentoList(query.getResultList());
		query = EntityUtil.createQuery(HistoricoModeloDocumentoQuery.LIST_USUARIO_QUERY);
		setUsuarioAlteracaoList(query.getResultList());
	}
	
	public List<Usuario> getUsuarioAlteracaoList() {
		return usuarioAlteracaoList;
	}

	public void setUsuarioAlteracaoList(List<Usuario> usuarioAlteracaoList) {
		this.usuarioAlteracaoList = usuarioAlteracaoList;
	}
	
	public List<ModeloDocumento> getModeloDocumentoList() {
		return modeloDocumentoList;
	}

	public void setModeloDocumentoList(
			List<ModeloDocumento> historicoModeloDocumentoList) {
		this.modeloDocumentoList = historicoModeloDocumentoList;
	}

	
}
