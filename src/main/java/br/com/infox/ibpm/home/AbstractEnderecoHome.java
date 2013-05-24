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

import br.com.infox.ibpm.entity.Endereco;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.itx.component.AbstractHome;


public abstract class AbstractEnderecoHome<T> extends AbstractHome<Endereco> {

	private static final long serialVersionUID = 1L;

	public void setEnderecoIdEndereco(Integer id) {
		setId(id);
	}

	public Integer getEnderecoIdEndereco() {
		return (Integer) getId();
	}

	@Override
	protected Endereco createInstance() {
		Endereco endereco = new Endereco();
		UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuarioHome != null) {
			endereco.setUsuario(usuarioHome.getDefinedInstance());
		}
		CepHome cepHome = (CepHome) Component.getInstance("cepHome", false);
		if (cepHome != null) {
			endereco.setCep(cepHome.getDefinedInstance());
		}
		return endereco;
	}

	@Override
	public String remove() {
		UsuarioHome usuario = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuario != null) {
			usuario.getInstance().getEnderecoList().remove(instance);
		}
		CepHome cep = (CepHome) Component.getInstance("cepHome", false);
		if (cep != null) {
			cep.getInstance().getEnderecoList().remove(instance);
		}
		return super.remove();
	}

	@Override
	public String remove(Endereco obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		if (action != null) {
			newInstance();
		}
		return action;
	}

	public List<Localizacao> getLocalizacaoList() {
		return getInstance() == null ? null : getInstance()
				.getLocalizacaoList();
	}

}