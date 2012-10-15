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

import java.util.List;

import org.jboss.seam.Component;

import br.com.infox.ibpm.entity.BloqueioUsuario;
import br.com.itx.component.AbstractHome;


public abstract class AbstractBloqueioUsuarioHome<T>
		extends
			AbstractHome<BloqueioUsuario> {

	private static final long serialVersionUID = 1L;

	public void setBloqueioUsuarioIdBloqueioUsuario(Integer id) {
		setId(id);
	}

	public Integer getBloqueioUsuarioIdBloqueioUsuario() {
		return (Integer) getId();
	}

	@Override
	protected BloqueioUsuario createInstance() {
		BloqueioUsuario bloqueioUsuario = new BloqueioUsuario();
		UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuarioHome != null) {
			bloqueioUsuario.setUsuario(usuarioHome.getDefinedInstance());
		}
		return bloqueioUsuario;
	}

	@Override
	public String remove() {
		UsuarioHome usuario = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuario != null) {
			usuario.getInstance().getBloqueioUsuarioList().remove(instance);
		}
		return super.remove();
	}

	@Override
	public String remove(BloqueioUsuario obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		refreshGrid("bloqueioUsuarioGrid");
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		if (getInstance().getUsuario() != null) {
			List<BloqueioUsuario> usuarioList = getInstance().getUsuario()
					.getBloqueioUsuarioList();
			if (!usuarioList.contains(instance)) {
				getEntityManager().refresh(getInstance().getUsuario());
			}
		}
		newInstance();
		return action;
	}

}