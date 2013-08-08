package br.com.infox.epp.action;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.manager.CategoriaManager;
import br.com.infox.epp.manager.ProcessoEpaTarefaManager;
import br.com.infox.util.DateUtil;

@Name(value=DashBoardAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class DashBoardAction {

	public static final String NAME = "dashBoardAction";
	
	@In
	private CategoriaManager categoriaManager;
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
	private boolean showForaPrazoGrid = false;
	
	private List<Object[]> categoriaList;
	private List<Object[]> tarefaForaPrazoList;
	private List<Object[]> tarefaPertoLimiteList;

	@Create
	public void init() {
		categoriaList = categoriaManager.listProcessoByCategoria();
		tarefaPertoLimiteList = processoEpaTarefaManager.listTarefaPertoLimite();
	}
	
	public void byProcessoForaPrazo(Categoria c, Double foraPrazoFluxo) {
		consultarCategoria(c, foraPrazoFluxo, true);
	}
	
	public void byProcessoForaTarefa(Categoria c, Double foraPrazoTarefa) {
		consultarCategoria(c, foraPrazoTarefa, false);
	}

	private void consultarCategoria(Categoria c, Double foraPrazo,
									boolean foraPrazoFluxo) {
		if(foraPrazo != 0) {
				if(foraPrazoFluxo) {
					tarefaForaPrazoList = processoEpaTarefaManager.listForaPrazoFluxo(c);
				} else {
					tarefaForaPrazoList = processoEpaTarefaManager.listForaPrazoTarefa(c);
				}
			if(tarefaForaPrazoList != null && tarefaForaPrazoList.size() > 0) {
				showForaPrazoGrid = true;
			}
		}
	}
	
	public String getTempo(Integer tempo) {
		int dias = 0;
		int minutos = 0;
		int horas = DateUtil.minutesToHour(tempo);
		StringBuilder sb = new StringBuilder();
		if(horas > DateUtil.HORAS_DO_DIA) {
			dias = horas / DateUtil.HORAS_DO_DIA;
			horas = horas % DateUtil.HORAS_DO_DIA;
			sb.append(dias+" dia(s) "+horas+" hora(s)");
		} else {
			minutos = tempo % DateUtil.MINUTOS_DA_HORA;
			sb.append(horas+" hora(s) "+minutos+" minuto(s)");
		}
		return sb.toString();
	}
	
	public void setCategoriaList(List<Object[]> categoriaList) {
		this.categoriaList = categoriaList;
	}

	public List<Object[]> getCategoriaList() {
		return categoriaList;
	}

	public void setShowForaPrazoGrid(boolean showForaPrazoGrid) {
		this.showForaPrazoGrid = showForaPrazoGrid;
	}

	public boolean getShowForaPrazoGrid() {
		return showForaPrazoGrid;
	}

	public void setTarefaForaPrazoList(List<Object[]> tarefaForaPrazoList) {
		this.tarefaForaPrazoList = tarefaForaPrazoList;
	}

	public List<Object[]> getTarefaForaPrazoList() {
		return tarefaForaPrazoList;
	}

	public void setTarefaPertoLimiteList(List<Object[]> tarefaPertoLimiteList) {
		this.tarefaPertoLimiteList = tarefaPertoLimiteList;
	}

	public List<Object[]> getTarefaPertoLimiteList() {
		return tarefaPertoLimiteList;
	}
	
}