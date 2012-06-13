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

import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.component.AbstractHome;


public abstract class AbstractFluxoHome<T> extends AbstractHome<Fluxo> {

	private static final long serialVersionUID = 1L;

	public void setFluxoIdFluxo(Integer id) {
		setId(id);
	}

	public Integer getFluxoIdFluxo() {
		return (Integer) getId();
	}

	@Override
	protected Fluxo createInstance() {
		Fluxo fluxo = new Fluxo();
		UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuarioHome != null) {
			fluxo.setUsuarioPublicacao(usuarioHome.getDefinedInstance());
		}
		return fluxo;
	}

	@Override
	public String remove() {
		UsuarioHome usuario = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuario != null) {
			usuario.getInstance().getFluxoList().remove(instance);
		}
		return super.remove();
	}

	public String remove(Fluxo obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		refreshGrid("fluxoGrid");
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		if (getInstance().getUsuarioPublicacao() != null) {
			List<Fluxo> usuarioPublicacaoList = getInstance()
					.getUsuarioPublicacao().getFluxoList();
			if (!usuarioPublicacaoList.contains(instance)) {
				getEntityManager()
						.refresh(getInstance().getUsuarioPublicacao());
			}
		}
		//newInstance();
		return action;
	}
}