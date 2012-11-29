package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.component.tree.EventoTreeHandler;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.list.EventoList;
import br.com.itx.component.Util;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ExcelExportUtil;
 
@Name(EventoHome.NAME)
@BypassInterceptors
public class EventoHome extends AbstractEventoHome<Evento>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "eventoHome";
	
	public static final String TEMPLATE = "/Evento/eventoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Eventos.xls";
	private List<Evento> eventoBeanList = new ArrayList<Evento>();
	
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
	
	
	
	public void exportarXLS() {
		eventoBeanList = EventoList.instance().list();
		try {
			if (!eventoBeanList.isEmpty()){
				exportarXLS(TEMPLATE);
			}
			else{
				FacesMessages.instance().add(Severity.INFO, "Não há dados para exportar!");
			}
		} catch (ExcelExportException e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro ao exportar arquivo." + e.getMessage());
			e.printStackTrace();
		}	
	}
	
	private void exportarXLS (String template) throws ExcelExportException {
		String urlTemplate = new Util().getContextRealPath() + template;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("evento", eventoBeanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, DOWNLOAD_XLS_NAME);
	}
	
}