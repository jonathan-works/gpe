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
package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.manager.ModeloDocumentoManager;
import br.com.infox.epp.manager.TipoModeloDocumentoPapelManager;
import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumentoPapel;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.Variavel;
import br.com.infox.list.ModeloDocumentoList;
import br.com.itx.util.ComponentUtil;

@Name(ModeloDocumentoHome.NAME)
@Scope(ScopeType.PAGE)
public class ModeloDocumentoHome extends AbstractModeloDocumentoHome<ModeloDocumento> {

	private static final String	RAWTYPES	= "rawtypes";
	private static final String	UNCHECKED	= "unchecked";
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/ModeloDocumento/modeloDocumentoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "ModelosDocumento.xls";
	public static final String NAME = "modeloDocumentoHome";
	
	private GrupoModeloDocumento grupoModeloDocumento;
	private TipoModeloDocumento tipoModeloDocumento;
	
	@In private TipoModeloDocumentoPapelManager tipoModeloDocumentoPapelManager;
	@In private ModeloDocumentoManager modeloDocumentoManager;
	
	@Override
	public EntityList<ModeloDocumento> getBeanList() {
		return ModeloDocumentoList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if (changed) {
			tipoModeloDocumento = getInstance().getTipoModeloDocumento();
			grupoModeloDocumento = tipoModeloDocumento.getGrupoModeloDocumento();
		}
		if (id == null) {
			tipoModeloDocumento = null;
			grupoModeloDocumento = null;
		} 
	}
	
	public GrupoModeloDocumento getGrupoModeloDocumento() {
		return grupoModeloDocumento;
	}
	
	public void setGrupoModeloDocumento(
			GrupoModeloDocumento grupoModeloDocumento) {
		this.grupoModeloDocumento = grupoModeloDocumento;
	}
	
	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}
	
	public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}

	@SuppressWarnings({ UNCHECKED, RAWTYPES })
	public List<Variavel> getVariaveis() {
		List list = new ArrayList<Variavel>();
		if (getInstance().getTipoModeloDocumento() != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("select o from Variavel o ");
			sb.append("join o.variavelTipoModeloList tipos ");
			sb.append("where tipos.tipoModeloDocumento = :tipo");
			list = getEntityManager().createQuery(sb.toString()).setParameter("tipo", getInstance().getTipoModeloDocumento()).getResultList();
		}
		return list;
	}
	
	public boolean setHistorico(ModeloDocumento oldEntity)	{
		if ( !instance.hasChanges(oldEntity) ) {
			return false;
		}
		if ( oldEntity== null )	{
			return true;
		}
			
		
		HistoricoModeloDocumentoHome home = (HistoricoModeloDocumentoHome)Component.getInstance(HistoricoModeloDocumentoHome.NAME);
		home.newInstance();
		HistoricoModeloDocumento historico = home.getInstance();
		
		historico.setTituloModeloDocumento(oldEntity.getTituloModeloDocumento());
		historico.setDescricaoModeloDocumento(oldEntity.getModeloDocumento());
		historico.setAtivo(oldEntity.getAtivo());
		historico.setDataAlteracao(new Date());
		historico.setModeloDocumento(instance);
		historico.setUsuarioAlteracao((UsuarioLogin) ComponentUtil.getComponent(Authenticator.USUARIO_LOGADO));
		
		home.persist();
		return true;
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if (!setHistorico(getOldEntity()))	{
				return false;
		}
		
		return super.beforePersistOrUpdate();
	}
	
	public List<TipoModeloDocumentoPapel> getTiposModeloDocumentoPermitidos() {
		return tipoModeloDocumentoPapelManager.getTiposModeloDocumentoPermitidos();
	}
	
	public List<ModeloDocumento> getModeloDocumentoByGrupoAndTipo(){
		return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(grupoModeloDocumento, tipoModeloDocumento);
	}
	
}