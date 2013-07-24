/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

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
import br.com.infox.ibpm.manager.VariavelManager;
import br.com.infox.list.ModeloDocumentoList;
import br.com.itx.util.ComponentUtil;

@Name(ModeloDocumentoHome.NAME)
@Scope(ScopeType.PAGE)
public class ModeloDocumentoHome extends AbstractModeloDocumentoHome<ModeloDocumento> {

	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/ModeloDocumento/modeloDocumentoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "ModelosDocumento.xls";
	public static final String NAME = "modeloDocumentoHome";
	
	private GrupoModeloDocumento grupoModeloDocumento;
	private TipoModeloDocumento tipoModeloDocumento;
	
	@In private TipoModeloDocumentoPapelManager tipoModeloDocumentoPapelManager;
	@In private ModeloDocumentoManager modeloDocumentoManager;
	@In private VariavelManager variavelManager;
	
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
	public void setId(final Object id) {
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
	
	public void setTipoModeloDocumento(final TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}
	
	public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}

	public List<Variavel> getVariaveis() {
		if (getInstance().getTipoModeloDocumento() != null) {
			return variavelManager.getVariaveisByTipoModeloDocumento(getInstance().getTipoModeloDocumento());
		}
		return new ArrayList<Variavel>();
	}
	
	public boolean setHistorico(final ModeloDocumento oldEntity)	{
		boolean result = false;
		if ( !instance.hasChanges(oldEntity) ) {
			result = false;
		} else if ( oldEntity== null )	{
			result = true;
		} else {
			HistoricoModeloDocumento historico = new HistoricoModeloDocumento();
			
			historico.setTituloModeloDocumento(oldEntity.getTituloModeloDocumento());
			historico.setDescricaoModeloDocumento(oldEntity.getModeloDocumento());
			historico.setAtivo(oldEntity.getAtivo());
			historico.setDataAlteracao(new Date());
			historico.setModeloDocumento(instance);
			historico.setUsuarioAlteracao((UsuarioLogin) ComponentUtil.getComponent(Authenticator.USUARIO_LOGADO));
			getEntityManager().persist(historico);
			result = true;
			FacesMessages.instance().clear();
		}
		return result;
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		boolean result = true;
		if (!setHistorico(getOldEntity()))	{
				result = false;
		}
		
		return result;
	}
	
	public List<TipoModeloDocumentoPapel> getTiposModeloDocumentoPermitidos() {
		return tipoModeloDocumentoPapelManager.getTiposModeloDocumentoPermitidos();
	}
	
	public List<ModeloDocumento> getModeloDocumentoByGrupoAndTipo(){
		return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(grupoModeloDocumento, tipoModeloDocumento);
	}
	
}