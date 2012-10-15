package br.com.infox.ibpm.home;

import br.com.infox.ibpm.entity.Evento;
import br.com.itx.component.AbstractHome;

public abstract class AbstractEventoHome<T> extends AbstractHome<Evento> {

	private static final long serialVersionUID = 1L;

	public void setEventoIdEvento(Integer id) {
		setId(id);
	}

	public Integer getEventoIdEvento() {
		return (Integer) getId();
	}

	@Override
	public String remove(Evento obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

}