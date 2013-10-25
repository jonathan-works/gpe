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
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(ModeloDocumentoHome.NAME)
@Scope(ScopeType.PAGE)
public class ModeloDocumentoHome extends AbstractHome<ModeloDocumento> {

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
	
	@Override
    protected ModeloDocumento createInstance() {
        ModeloDocumento modeloDocumento = new ModeloDocumento();
        TipoModeloDocumentoHome tipoModeloDocumentoHome = (TipoModeloDocumentoHome) Component
                .getInstance("tipoModeloDocumentoHome", false);
        if (tipoModeloDocumentoHome != null) {
            modeloDocumento.setTipoModeloDocumento(tipoModeloDocumentoHome
                    .getDefinedInstance());
        }
        return modeloDocumento;
    }

    @Override
    public String remove() {
        TipoModeloDocumentoHome tipoModeloDocumento = (TipoModeloDocumentoHome) Component
                .getInstance("tipoModeloDocumentoHome", false);
        if (tipoModeloDocumento != null) {
            tipoModeloDocumento.getInstance().getModeloDocumentoList().remove(
                    instance);
        }
        return super.remove();
    }

    @Override
    public String remove(ModeloDocumento obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        return ret;
    }

    @Override
    public String persist() {
        String action = super.persist();
        if (getInstance().getTipoModeloDocumento() != null) {
            List<ModeloDocumento> tipoModeloDocumentoList = getInstance()
                    .getTipoModeloDocumento().getModeloDocumentoList();
            if (!tipoModeloDocumentoList.contains(instance)) {
                getEntityManager().refresh(
                        getInstance().getTipoModeloDocumento());
            }
        }
        return action;
    }

	
}