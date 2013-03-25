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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.infox.list.HistoricoModeloDocumentoList;
import br.com.itx.component.AbstractHome;


/**
 * Classe para opera��es com "Assunto(TUA)"
 *
 */
@Name(HistoricoModeloDocumentoHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class HistoricoModeloDocumentoHome extends AbstractHome<HistoricoModeloDocumento> {

    private static final long serialVersionUID = 1L;
    private static final String TEMPLATE = "/ModeloDocumento/historicoModeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "historicosModelosDocumento.xls";
    public static final String NAME = "historicoModeloDocumentoHome";
    
	

	@Override
	public String getTemplate(){
        return HistoricoModeloDocumentoHome.TEMPLATE;
    }
	
	@Override
    public String getDownloadXlsName(){
        return HistoricoModeloDocumentoHome.DOWNLOAD_XLS_NAME;
    }
	
    @Override
    public EntityList<HistoricoModeloDocumento> getBeanList() {
        return HistoricoModeloDocumentoList.instance();
    }
	
}