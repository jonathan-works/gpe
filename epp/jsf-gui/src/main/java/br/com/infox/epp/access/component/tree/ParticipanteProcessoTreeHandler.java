package br.com.infox.epp.access.component.tree;

import javax.inject.Named;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.partes.controller.AbstractParticipantesController;
import br.com.infox.epp.processo.partes.controller.ParticipantesProcessoController;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@ViewScoped
@Named("participanteProcessoTree")
public class ParticipanteProcessoTreeHandler extends AbstractTreeHandler<ParticipanteProcesso> {

	private static final long serialVersionUID = 1L;
    public static final String EVENT_SELECTED = "evtSelectParticipante";

	@Override
	protected String getQueryRoots() {
		return "select o from ParticipanteProcesso o  " +
				"where o.processo.idProcesso = " + getIdProcesso() +
				" and o.participantePai is null order by o.caminhoAbsoluto";
	}

	@Override
	protected String getQueryChildren() {
		return "select o from ParticipanteProcesso o where o.participantePai = :"
					+ EntityNode.PARENT_NODE + " order by o.caminhoAbsoluto";
	}

	@Override
    protected String getEventSelected() {
        return EVENT_SELECTED;
    }

    public int getIdProcesso(){
	    AbstractParticipantesController controller = Beans.getReference(ParticipantesProcessoController.class);
    	return controller.getProcesso().getIdProcesso();
    }

    @Override
    protected EntityNode<ParticipanteProcesso> createNode() {
        return new ParticipanteProcessoEntityNode(getQueryChildrenList());
    }

}
