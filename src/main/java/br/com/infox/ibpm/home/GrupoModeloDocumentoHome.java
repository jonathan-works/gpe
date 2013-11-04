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

import org.jboss.seam.annotations.Name;

import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.infox.ibpm.entity.ItemTipoDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.itx.component.AbstractHome;

@Name(GrupoModeloDocumentoHome.NAME)
public class GrupoModeloDocumentoHome extends
		AbstractHome<GrupoModeloDocumento> {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "grupoModeloDocumentoHome";

    @Override
    protected GrupoModeloDocumento createInstance() {
        return new GrupoModeloDocumento();
    }

    @Override
    public String remove(GrupoModeloDocumento obj) {
        setInstance(obj);
        getInstance().setAtivo(Boolean.FALSE);
        String ret = super.update();
        newInstance();
        return ret;
    }

    public List<ItemTipoDocumento> getItemTipoDocumentoList() {
        return getInstance() == null ? null : getInstance()
                .getItemTipoDocumentoList();
    }

    public List<TipoModeloDocumento> getTipoModeloDocumentoList() {
        return getInstance() == null ? null : getInstance()
                .getTipoModeloDocumentoList();
    }

}