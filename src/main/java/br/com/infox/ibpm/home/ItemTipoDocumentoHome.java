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

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.component.suggest.GrupoModeloDocumentoSuggestBean;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.infox.ibpm.entity.ItemTipoDocumento;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.itx.util.EntityUtil;



@Name("itemTipoDocumentoHome")
@BypassInterceptors
public class ItemTipoDocumentoHome
		extends AbstractItemTipoDocumentoHome<ItemTipoDocumento> {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private Localizacao localizacao;	
	
	public void set(ItemTipoDocumento itemTipoDocumento) {
		instance = itemTipoDocumento;
		localizacao = instance.getLocalizacao();
		getGrupoModeloSuggest().setInstance(instance.getGrupoModeloDocumento());
	}

	private GrupoModeloDocumentoSuggestBean getGrupoModeloSuggest() {
		return getComponent("grupoModeloDocumentoSuggest");
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setGrupoModeloDocumento(getGrupoModeloSuggest().getInstance());
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
	public void newInstance() {
		getGrupoModeloSuggest().setInstance(null);
		super.newInstance();
	}
	
	@Override
	public String persist() {
		getInstance().setLocalizacao(LocalizacaoHome.instance().getInstance());
		ItemTipoDocumento itd = getInstance();
		getEntityManager().merge(itd);
		EntityUtil.flush();
		String msg = "persisted";
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
}