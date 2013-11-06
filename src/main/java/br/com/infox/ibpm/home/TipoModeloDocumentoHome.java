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

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epp.manager.ModeloDocumentoManager;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.manager.TipoModeloDocumentoManager;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(TipoModeloDocumentoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class TipoModeloDocumentoHome 
		extends	AbstractHome<TipoModeloDocumento> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "tipoModeloDocumentoHome";
	
	@In private TipoModeloDocumentoManager tipoModeloDocumentoManager;
	@In private ModeloDocumentoManager modeloDocumentoManager;

	public static final TipoModeloDocumentoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if (!violaConstraintsDeUnicidade()) {
		    return super.beforePersistOrUpdate();
		}
		else{
			return false;
		}
	}
	
	private boolean violaConstraintsDeUnicidade(){
		return (violaUnicidadeDeAbreviacao() || violaUnicidadeDeDescricao());
	}
	
	private boolean violaUnicidadeDeAbreviacao(){
		if (tipoModeloDocumentoManager.violaUnicidadeDeAbreviacao(instance)){
			FacesMessages.instance().add(Severity.ERROR,"Já existe um Tipo de Modelo de Documento com esta abreviação.");
			return true;
		}
		return false;
	}
	
	private boolean violaUnicidadeDeDescricao(){
		if (tipoModeloDocumentoManager.violaUnicidadeDeDescricao(instance)){
			FacesMessages.instance().add(Severity.ERROR,"Já existe um Tipo de Modelo de Documento com esta descrição.");
			return true;
		}
		return false;
	}
	
	public List<ModeloDocumento> getListaDeModeloDocumento(){
		return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(instance.getGrupoModeloDocumento(), instance);
	}
	
	public void setTipoModeloDocumentoIdTipoModeloDocumento(Integer id) {
        setId(id);
    }

    public Integer getTipoModeloDocumentoIdTipoModeloDocumento() {
        return (Integer) getId();
    }

    @Override
    protected TipoModeloDocumento createInstance() {
        TipoModeloDocumento tipoModeloDocumento = new TipoModeloDocumento();
        GrupoModeloDocumentoHome grupoModeloDocumentoHome = (GrupoModeloDocumentoHome) Component
                .getInstance("grupoModeloDocumentoHome", false);
        if (grupoModeloDocumentoHome != null) {
            tipoModeloDocumento
                    .setGrupoModeloDocumento(grupoModeloDocumentoHome
                            .getDefinedInstance());
        }
        return tipoModeloDocumento;
    }

    @Override
    public String remove() {
        GrupoModeloDocumentoHome grupoModeloDocumento = (GrupoModeloDocumentoHome) Component
                .getInstance("grupoModeloDocumentoHome", false);
        if (grupoModeloDocumento != null) {
            grupoModeloDocumento.getInstance().getTipoModeloDocumentoList()
                    .remove(instance);
        }
        return super.remove();
    }

    @Override
    public String remove(TipoModeloDocumento obj) {
        setInstance(obj);
        getInstance().setAtivo(Boolean.FALSE);
        String ret = super.update();
        newInstance();
        return ret;
    }

    @Override
    public String persist() {
        String action = super.persist();
        if (getInstance().getGrupoModeloDocumento() != null) {
            List<TipoModeloDocumento> grupoModeloDocumentoList = getInstance()
                    .getGrupoModeloDocumento().getTipoModeloDocumentoList();
            if (!grupoModeloDocumentoList.contains(instance)) {
                getEntityManager().refresh(
                        getInstance().getGrupoModeloDocumento());
            }
        }
        return action;
    }

    public List<ModeloDocumento> getModeloDocumentoList() {
        return getInstance() == null ? null : getInstance()
                .getModeloDocumentoList();
    }

	
}