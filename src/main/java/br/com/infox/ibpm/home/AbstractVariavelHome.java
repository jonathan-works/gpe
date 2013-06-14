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

import br.com.infox.ibpm.entity.Variavel;
import br.com.itx.component.AbstractHome;


public abstract class AbstractVariavelHome<T> extends AbstractHome<Variavel> {

	private static final long serialVersionUID = 1L;

	public void setVariavelIdVariavel(Integer id) {
		setId(id);
	}

	public Integer getVariavelIdVariavel() {
		return (Integer) getId();
	}

	@Override
	protected Variavel createInstance() {
		Variavel variavel = new Variavel();
		return variavel;
	}

	@Override
	public String remove(Variavel obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		return action;
	}

}