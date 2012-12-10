package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.component.tree.EventoTreeHandler;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.list.EventoList;
 
@Name(EventoHome.NAME)
@BypassInterceptors
public class EventoHome extends AbstractEventoHome<Evento>{

	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/Evento/eventoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Eventos.xls";
	
	public static final String NAME = "eventoHome";
	
	@Override
	public EntityList<Evento> getBeanList() {
		return EventoList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
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