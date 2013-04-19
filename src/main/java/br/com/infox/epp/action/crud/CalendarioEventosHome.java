package br.com.infox.epp.action.crud;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.entity.CalendarioEventos;
import br.com.infox.epp.list.CalendarioEventosList;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.itx.component.AbstractHome;

@Name(CalendarioEventosHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CalendarioEventosHome extends AbstractHome<CalendarioEventos>{

	public static final String NAME = "calendarioEventosHome";
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/CalendarioEventos/CalendarioEvTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "CalendarioEventos.xls";
	private LocalizacaoTreeHandler localizacaoTreeHandler = new LocalizacaoTreeHandler();
	
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
			calendar.set(Calendar.MONTH, cEventos.getMes()-1);
			
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

	@Override
	public EntityList<CalendarioEventos> getBeanList() {
		return CalendarioEventosList.instance();
	}

	@Override
	public String getTemplate() {
		return TEMPLATE;
	}

	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}	
	
}
