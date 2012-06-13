package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.EventoAgrupamento;
import br.com.itx.util.EntityUtil;

@Name("eventoAgrupamentoHome")
@BypassInterceptors
public class EventoAgrupamentoHome extends AbstractEventoAgrupamentoHome<EventoAgrupamento> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String persist() {
		instance.setAgrupamento(AgrupamentoHome.instance().getInstance());
		List<Evento> eventoList = getPersistedEventoList();
		Evento temp = instance.getEvento();
		boolean multiplo = instance.getMultiplo();
		if(havePersistedDad(instance.getEvento(), eventoList)){
			FacesMessages.instance().add(
					StatusMessage.Severity.ERROR, 
					"Esse registro já está associado implicitamente por hierarquia");
			return null;
		}
		havePersistedChildrens(instance.getEvento(), eventoList);
		newInstance();
		instance.setAgrupamento(AgrupamentoHome.instance().getInstance());
		instance.setEvento(temp);
		instance.setMultiplo(multiplo);
		String persist = super.persist();
		newInstance();
		refreshGrid("eventoAgrupamentoGrid");
		return persist;
	}

	private boolean havePersistedDad(Evento inst, List<Evento> eventoList) {
		boolean ret = false;
		if(inst.getEventoSuperior() != null) {
			if(eventoList.contains(inst.getEventoSuperior())) {
				return true;
			}
			ret = havePersistedDad(inst.getEventoSuperior(), eventoList);
		}
		return ret;
	}
	
	private void havePersistedChildrens(Evento inst, List<Evento> eventoList) {
		for(Evento ge : inst.getEventoList()) {
			if(eventoList.contains(ge)) {
				StringBuilder sb = new StringBuilder();
				sb.append("select o from EventoAgrupamento o ");
				sb.append("where o.evento = :evento and ");
				sb.append("o.agrupamento = :agrupamento");
				Query hql = getEntityManager().createQuery(sb.toString());
				hql.setParameter("evento", ge);
				hql.setParameter("agrupamento", instance.getAgrupamento());
				instance = (EventoAgrupamento) hql.getSingleResult();
				AgrupamentoHome agrupamentoHome = AgrupamentoHome.instance();
				if (agrupamentoHome != null) {
					agrupamentoHome.getInstance().getEventoAgrupamentoList().remove(instance);
				}
				getEntityManager().remove(instance);
			    EntityUtil.flush();
			}
			havePersistedChildrens(ge, eventoList);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Evento> getPersistedEventoList() {
		List<Evento> eventoList = new ArrayList<Evento>();
		StringBuilder sb = new StringBuilder();
		sb.append("select o from EventoAgrupamento o ");
		sb.append("where o.agrupamento.idAgrupamento = ?1");
		Query hql = getEntityManager().createQuery(sb.toString());
		hql.setParameter(1, instance.getAgrupamento().getIdAgrupamento());
		List<EventoAgrupamento> eventoAgrupamentoList = (List<EventoAgrupamento>) hql.getResultList();
		for (EventoAgrupamento gea : eventoAgrupamentoList) {
			eventoList.add(gea.getEvento());
		}
		return eventoList;
	}
	
	@Override
	public String remove() {
		String remove = super.remove();
		newInstance();
		refreshGrid("eventoAgrupamentoGrid");
		return remove;
	}
}