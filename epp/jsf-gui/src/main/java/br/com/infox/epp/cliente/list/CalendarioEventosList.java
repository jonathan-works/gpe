package br.com.infox.epp.cliente.list;

import java.util.Date;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cliente.entity.CalendarioEventos;

@Named(CalendarioEventosList.NAME)
@ViewScoped
public class CalendarioEventosList extends DataList<CalendarioEventos> {

    public static final String NAME = "calendarioEventosList";

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from CalendarioEventos o";
    private static final String DEFAULT_ORDER = "o.dataInicio, o.dataFim";

    private Localizacao localizacao;
    private Date dataInicioPeriodo;
    private Date dataFimPeriodo;
    private TipoEvento tipoEvento;
    private String descricaoEvento;

    @Override
    protected void addRestrictionFields() {
        addRestrictionField("localizacao", RestrictionType.igual);
        addRestrictionField("tipoEvento", RestrictionType.igual);
        addRestrictionField("descricaoEvento", RestrictionType.contendoLower);
        addRestrictionField("dataInicioPeriodo","( o.dataInicio >= #{calendarioEventosList.dataInicioPeriodo} or o.dataFim >= #{calendarioEventosList.dataInicioPeriodo} )");
        addRestrictionField("dataFimPeriodo","( o.dataInicio <= #{calendarioEventosList.dataFimPeriodo} or o.dataFim <= #{calendarioEventosList.dataFimPeriodo} )");
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public String getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(String descricao) {
        this.descricaoEvento = descricao;
    }

    public Date getDataInicioPeriodo() {
        return dataInicioPeriodo;
    }

    public void setDataInicioPeriodo(Date dataInicioPeriodo) {
        this.dataInicioPeriodo = dataInicioPeriodo;
    }

    public Date getDataFimPeriodo() {
        return dataFimPeriodo;
    }

    public void setDataFimPeriodo(Date dataFimPeriodo) {
        this.dataFimPeriodo = dataFimPeriodo;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

}
