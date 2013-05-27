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

import br.com.infox.ibpm.entity.Parametro;
import br.com.itx.component.AbstractHome;


public abstract class AbstractParametroHome<T>
		extends	AbstractHome<Parametro> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Parametro createInstance() {
		return new Parametro();
	}

	@Override
	public String remove(Parametro obj) {
		setInstance(obj);
		String ret = super.update();
		newInstance();
		return ret;
	}
}