package br.com.infox.epa.action.crud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epa.entity.CalendarioEventos;
import br.com.infox.epa.list.CalendarioEventosList;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ExcelExportUtil;

@Name(CalendarioEventosHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CalendarioEventosHome extends AbstractHome<CalendarioEventos>{

	public static final String NAME = "calendarioEventosHome";
	private static final long serialVersionUID = 1L;
	
	public static final String TEMPLATE = "/CalendarioEventos/CalendarioEvTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "CalendarioEventos.xls";
	private List<CalendarioEventos> calendarioEvBeanList = new ArrayList<CalendarioEventos>();
	private LocalizacaoTreeHandler localizacaoTreeHandler = new LocalizacaoTreeHandler();
	
	public void exportarXLS() {
		calendarioEvBeanList = CalendarioEventosList.instance().list();
		try {
			if (!calendarioEvBeanList.isEmpty()){
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
	
	public void exportarXLS (String template) throws ExcelExportException {
		String urlTemplate = new Util().getContextRealPath() + template;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("calendarioEventos", calendarioEvBeanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, DOWNLOAD_XLS_NAME);
	}

	public LocalizacaoTreeHandler getLocalizacaoTreeHandler() {
		return localizacaoTreeHandler;
	}

	public void setLocalizacaoTreeHandler(
			LocalizacaoTreeHandler localizacaoTreeHandler) {
		this.localizacaoTreeHandler = localizacaoTreeHandler;
	}

	private void setData()	{
		CalendarioEventos cEventos = this.instance;
		
		if (cEventos.getDataEvento() == null)	{
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String[] stringData = sdf.format(cEventos.getDataEvento()).split("/");
		
		cEventos.setDia(Integer.parseInt(stringData[0]));
		cEventos.setMes(Integer.parseInt(stringData[1]));
		if (cEventos.getRepeteAno())	{
			cEventos.setAno(null);
		} else {
			cEventos.setAno(Integer.parseInt(stringData[2]));
		}
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		setData();
		return super.beforePersistOrUpdate();
	}
	
	@Override
	public void setId(Object id) {
		boolean changed = ((id != null) && !id.equals(getId()));
		super.setId(id);
		if (changed)	{
			CalendarioEventos cEventos = instance;
			Calendar calendar = Calendar.getInstance();
			
			calendar.set(Calendar.DATE, cEventos.getDia());
			calendar.set(Calendar.MONTH, cEventos.getMes());
			
			if (cEventos.getAno() == null)	{
				cEventos.setRepeteAno(Boolean.TRUE);
				calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())));
			} else {
				cEventos.setRepeteAno(Boolean.FALSE);
				calendar.set(Calendar.YEAR, cEventos.getAno());
			}
			cEventos.setDataEvento(calendar.getTime());
		}
	}	
	
}
