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

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.manager.TipoProcessoDocumentoManager;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.type.TipoDocumentoEnum;
import br.com.infox.ibpm.type.TipoNumeracaoEnum;
import br.com.infox.ibpm.type.VisibilidadeEnum;
import br.com.infox.list.TipoProcessoDocumentoList;
import br.com.itx.util.ComponentUtil;


@Name(TipoProcessoDocumentoHome.NAME)
public class TipoProcessoDocumentoHome
		extends
			AbstractTipoProcessoDocumentoHome<TipoProcessoDocumento> {

	
	public static final String NAME = "tipoProcessoDocumentoHome";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(TipoProcessoDocumentoHome.class);
	
	private static final String TEMPLATE = "/ClassificacaoDocumento/tipoProcessoDocumentoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "ClassificacaoDocumento.xls";
	
	@In private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;

	@Override
	public EntityList<TipoProcessoDocumento> getBeanList() {
		return TipoProcessoDocumentoList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	public static TipoProcessoDocumentoHome instance() {
		return ComponentUtil.getComponent("tipoProcessoDocumentoHome");
	}
	
	@Override
	public String persist() {		 
		String ret = null;
		try{
			ret = super.persist();
		}
		catch (Exception e) {
			LOG.error(".persist()", e);
		} 
		return ret;	
	}
	
	@Override
	public String remove(TipoProcessoDocumento obj) {
		obj.setAtivo(Boolean.FALSE);
		return super.remove(obj);
	}
	
	public TipoDocumentoEnum[] getTipoDocumentoEnumValues() {
		return TipoDocumentoEnum.values();
	}
	
	public TipoNumeracaoEnum[] getTipoNumeracaoEnumValues() {
		return TipoNumeracaoEnum.values();
	}
	
	public VisibilidadeEnum[] getVisibilidadeEnumValues(){
		return VisibilidadeEnum.values();
	}
	
	public List<TipoProcessoDocumento> getTipoProcessoDocumentoInterno(boolean isModelo){
		return tipoProcessoDocumentoManager.getTipoProcessoDocumentoInterno(isModelo);
	}
	
}