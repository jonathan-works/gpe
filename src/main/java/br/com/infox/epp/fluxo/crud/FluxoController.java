package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Named
@ConversationScoped
public class FluxoController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private Conversation conversation;
	
	private Fluxo fluxo;
	
	@PostConstruct
	public void init() {
		if (conversation.isTransient()) {
			conversation.begin();
		}
	}

	public Fluxo getFluxo() {
		EntityManager entityManager = EntityManagerProducer.getEntityManager();
		if (fluxo != null && fluxo.getIdFluxo() != null && !entityManager.contains(fluxo)) {
			fluxo = entityManager.find(Fluxo.class, fluxo.getIdFluxo());
		}
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public String getConversationId() {
		return conversation.getId();
	}
}
