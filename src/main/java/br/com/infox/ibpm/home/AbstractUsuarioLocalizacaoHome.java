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

import org.jboss.seam.Component;

import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.itx.component.AbstractHome;


public abstract class AbstractUsuarioLocalizacaoHome<T>
		extends
			AbstractHome<UsuarioLocalizacao> {

	private static final long serialVersionUID = 1L;

	public void setUsuarioLocalizacaoIdUsuarioLocalizacao(Integer id) {
		setId(id);
	}

	public Integer getUsuarioLocalizacaoIdUsuarioLocalizacao() {
		return (Integer) getId();
	}

	@Override
	protected UsuarioLocalizacao createInstance() {
		UsuarioLocalizacao usuarioLocalizacao = new UsuarioLocalizacao();
		UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuarioHome != null) {
			usuarioLocalizacao.setUsuario(usuarioHome.getInstance());
		}
		LocalizacaoHome localizacaoHome = (LocalizacaoHome) Component
				.getInstance("localizacaoHome", false);
		if (localizacaoHome != null) {
			usuarioLocalizacao.setLocalizacao(localizacaoHome
					.getDefinedInstance());
		}
		return usuarioLocalizacao;
	}

	@Override
	public String remove() {
		UsuarioHome usuario = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuario != null) {
			usuario.getInstance().getUsuarioLocalizacaoList().remove(instance);
		}
		LocalizacaoHome localizacao = (LocalizacaoHome) Component.getInstance(
				"localizacaoHome", false);
		if (localizacao != null) {
			localizacao.getInstance().getUsuarioLocalizacaoList().remove(
					instance);
		}
		return super.remove();
	}

	@Override
	public String remove(UsuarioLocalizacao obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		newInstance();
		return action;
	}

}