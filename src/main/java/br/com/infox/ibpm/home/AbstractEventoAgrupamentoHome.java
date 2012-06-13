package br.com.infox.ibpm.home;

import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.infox.ibpm.entity.EventoAgrupamento;
import br.com.itx.component.AbstractHome;

public class AbstractEventoAgrupamentoHome<T> extends AbstractHome<EventoAgrupamento> {

	private static final long serialVersionUID = 1L;

	public void setEventoAgrupamentoIdEventoAgrupamento(Integer id) {
		setId(id);
	}

	public Integer getEventoAgrupamentoIdEventoAgrupamento() {
		return (Integer) getId();
	}
	
	@Override
	public String remove() {
		AgrupamentoHome agrupamentoHome = AgrupamentoHome.instance();
		if (agrupamentoHome != null) {
			agrupamentoHome.getInstance().getEventoAgrupamentoList().remove(instance);
		}
		return super.remove();
	}
	
	@Override
	public void newInstance() {
		AutomaticEventsTreeHandler geth = new AutomaticEventsTreeHandler();
		geth.clearTree();
		super.newInstance();
	}
	
}