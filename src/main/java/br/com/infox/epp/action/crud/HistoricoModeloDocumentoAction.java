package br.com.infox.epp.action.crud;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.epp.query.HistoricoModeloDocumentoQuery;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.infox.ibpm.entity.ModeloDocumento;
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
    private List<UsuarioLogin> usuarioAlteracaoList;
    private HistoricoModeloDocumento selecionado;
	
    public HistoricoModeloDocumento getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(HistoricoModeloDocumento selecionado) {
		this.selecionado = selecionado;
	}
	
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

	@SuppressWarnings("unchecked")
	@Override
	public void create() {
		super.create();
		javax.persistence.Query query = EntityUtil.createQuery(HistoricoModeloDocumentoQuery.LIST_MODELO_QUERY);
		setModeloDocumentoList(query.getResultList());
		query = EntityUtil.createQuery(HistoricoModeloDocumentoQuery.LIST_USUARIO_QUERY);
		query.setParameter(HistoricoModeloDocumentoQuery.LIST_USUARIO_PARAM_MODELO, ((ModeloDocumentoHome)Component.getInstance(ModeloDocumentoHome.NAME)).getInstance());
		setUsuarioAlteracaoList(query.getResultList());
	}
	
	public List<UsuarioLogin> getUsuarioAlteracaoList() {
		return usuarioAlteracaoList;
	}

	public void setUsuarioAlteracaoList(List<UsuarioLogin> usuarioAlteracaoList) {
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
