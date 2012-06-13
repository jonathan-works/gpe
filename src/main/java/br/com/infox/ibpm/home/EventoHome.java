package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.component.tree.EventoTreeHandler;
import br.com.infox.ibpm.entity.Evento;
 
@Name("eventoHome")
@BypassInterceptors
public class EventoHome extends AbstractEventoHome<Evento>{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void newInstance() {
		getInstance().setAtivo(Boolean.TRUE);
		limparTrees();
		super.newInstance();
	}
	
	private void limparTrees(){
		((EventoTreeHandler)getComponent(EventoTreeHandler.NAME)).clearTree();
	}
	
	public String inactiveRecursive(Evento evento) {
		if (evento.getEventoList().size()  > 0) {
			inativarFilhos(evento);
		}
		evento.setAtivo(Boolean.FALSE);
		String ret = super.update();
		limparTrees();
		return ret;
	}
	
	private void inativarFilhos(Evento evento){
		evento.setAtivo(Boolean.FALSE);
		Integer quantidadeFilhos = evento.getEventoList().size();
		for (int i = 0; i < quantidadeFilhos; i++) {
			inativarFilhos(evento.getEventoList().get(i));
		}
	}
	
	@Override
	public String update() {
		if (!getInstance().getAtivo()){
			inactiveRecursive(getInstance());
			return "updated";
		}
		return super.update();	
	}
	
}