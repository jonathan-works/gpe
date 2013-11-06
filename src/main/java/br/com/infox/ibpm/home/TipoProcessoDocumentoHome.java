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

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;


@Name(TipoProcessoDocumentoHome.NAME)
public class TipoProcessoDocumentoHome
		extends
			AbstractHome<TipoProcessoDocumento> {

	
	public static final String NAME = "tipoProcessoDocumentoHome";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(TipoProcessoDocumentoHome.class);
	
	@In private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;

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
		setInstance(obj);
        obj.setAtivo(Boolean.FALSE);
        String ret = super.update();
        newInstance();
        return ret;
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
	
	public void setTipoProcessoDocumentoIdTipoProcessoDocumento(Integer id) {
        setId(id);
    }

    public Integer getTipoProcessoDocumentoIdTipoProcessoDocumento() {
        return (Integer) getId();
    }

    @Override
    protected TipoProcessoDocumento createInstance() {
        return new TipoProcessoDocumento();
    }

    public List<ProcessoDocumento> getProcessoDocumentoList() {
        return getInstance() == null ? null : getInstance()
                .getProcessoDocumentoList();
    }
	
}