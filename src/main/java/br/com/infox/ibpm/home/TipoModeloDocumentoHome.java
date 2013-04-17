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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epa.list.TipoModeloDocumentoList;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.manager.TipoModeloDocumentoManager;
import br.com.itx.util.ComponentUtil;

@Name(TipoModeloDocumentoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class TipoModeloDocumentoHome 
		extends	AbstractTipoModeloDocumentoHome<TipoModeloDocumento> {
	
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/TipoModeloDocumento/tipoModeloDocumentoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "TiposModeloDocumento.xls";
	
	public static final String NAME = "tipoModeloDocumentoHome";
	
	@In private TipoModeloDocumentoManager tipoModeloDocumentoManager;

	public static final TipoModeloDocumentoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	public EntityList<TipoModeloDocumento> getBeanList() {
		return TipoModeloDocumentoList.instance();
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
	protected boolean beforePersistOrUpdate() {
		if (!violaConstraintsDeUnicidade())
			return super.beforePersistOrUpdate();
		else{
			return false;
		}
	}
	
	private boolean violaConstraintsDeUnicidade(){
		return (violaUnicidadeDeAbreviacao() || violaUnicidadeDeDescricao());
	}
	
	private boolean violaUnicidadeDeAbreviacao(){
		if (tipoModeloDocumentoManager.violaUnicidadeDeAbreviacao(instance)){
			FacesMessages.instance().add(Severity.ERROR,"J� existe um Tipo de Modelo de Documento com esta abrevia��o.");
			return true;
		}
		return false;
	}
	
	
	
	private boolean violaUnicidadeDeDescricao(){
		if (tipoModeloDocumentoManager.violaUnicidadeDeDescricao(instance)){
			FacesMessages.instance().add(Severity.ERROR,"J� existe um Tipo de Modelo de Documento com esta descri��o.");
			return true;
		}
		return false;
	}
	
}