package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@AutoCreate
@Name(ParticipanteProcessoTreeHandler.NAME)
public class ParticipanteProcessoTreeHandler extends AbstractTreeHandler<ParticipanteProcesso> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "participanteProcessoTree";
    public static final String EVENT_SELECTED = "evtSelectParticipante";
    
	@Override
	protected String getQueryRoots() {
		return "select o from ParticipanteProcesso o  " + 
				"where o.processo.idProcesso = " + ProcessoEpaHome.instance().getProcessoIdProcesso() + 
				" and o.participantePai is null order by o.nome";
	}
	
	@Override
	protected String getQueryChildren() {
		return "select o from ParticipanteProcesso o where o.participantePai = :" + EntityNode.PARENT_NODE;
	}
	
    @Override
    protected String getEventSelected() {
        return EVENT_SELECTED;
    }


}
