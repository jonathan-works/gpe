package br.com.infox.epp.cliente.crud;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cliente.entity.CalendarioEventos;

@Name(CalendarioEventosCrudAction.NAME)
public class CalendarioEventosCrudAction extends AbstractCrudAction<CalendarioEventos> {
    
    public static final String NAME = "calendarioEventosCrudAction";
    
    private void setData()  {
        if (getInstance().getDataEvento() == null)   {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String[] stringData = sdf.format(getInstance().getDataEvento()).split("/");
        getInstance().setDia(Integer.parseInt(stringData[0]));
        getInstance().setMes(Integer.parseInt(stringData[1]));
        if (getInstance().getRepeteAno())    {
            getInstance().setAno(null);
        } else {
            getInstance().setAno(Integer.parseInt(stringData[2]));
        }
    }
    
    public void setId(Object id) {
        boolean changed = ((id != null) && !id.equals(getId()));
        super.setId(id);
        if (changed)    {
            ajustarData();
        }
    }

    private void ajustarData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, getInstance().getDia());
        calendar.set(Calendar.MONTH, getInstance().getMes()-1);
        if (getInstance().getAno() == null)  {
            getInstance().setRepeteAno(Boolean.TRUE);
            calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())));
        } else {
            getInstance().setRepeteAno(Boolean.FALSE);
            calendar.set(Calendar.YEAR, getInstance().getAno());
        }
        getInstance().setDataEvento(calendar.getTime());
    }
    
    @Override
    protected boolean beforeSave() {
        setData();
        return super.beforeSave();
    }

}
