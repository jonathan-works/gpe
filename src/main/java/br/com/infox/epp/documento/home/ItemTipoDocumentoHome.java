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
package br.com.infox.epp.documento.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.documento.entity.ItemTipoDocumento;
import br.com.infox.ibpm.home.LocalizacaoHome;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

@Name(ItemTipoDocumentoHome.NAME)
@Deprecated
public class ItemTipoDocumentoHome
		extends AbstractHome<ItemTipoDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "itemTipoDocumentoHome";
	
	public void set(ItemTipoDocumento itemTipoDocumento) {
		instance = itemTipoDocumento;
	}

	@Override
	protected boolean beforePersistOrUpdate() {
		if (getInstance().getGrupoModeloDocumento() == null) {
			FacesMessages.instance().add(Severity.ERROR, "É obrigatório selecionar um Grupo de Modelo");
			return false;
		}
		return true;
	}

	public LocalizacaoTreeHandler getLocalizacaoTree(){
		return getComponent("localizacaoItemTipoDocumentoFormTree");
	}
	
	@Override
	public String persist() {
		getInstance().setLocalizacao(LocalizacaoHome.instance().getInstance());
		ItemTipoDocumento itd = getInstance();
		getEntityManager().merge(itd);
		EntityUtil.flush();
		String msg = PERSISTED;
		instance.setGrupoModeloDocumento(null);
		newInstance();
		FacesMessages.instance().add(StatusMessage.Severity.INFO, "Registro inserido com sucesso.");
		return msg;
	}
	
	@Override
	public String remove(ItemTipoDocumento obj) {
    	getEntityManager().remove(obj);
    	EntityUtil.flush();
        newInstance();
        return "removido";
    }
	
	public void setItemTipoDocumentoIdItemTipoDocumento(Integer id) {
        setId(id);
    }

    public Integer getItemTipoDocumentoIdItemTipoDocumento() {
        return (Integer) getId();
    }

    @Override
    protected ItemTipoDocumento createInstance() {
        return new ItemTipoDocumento();
    }
    
}