package br.com.infox.epa.action.crud;


import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import br.com.infox.epa.entity.TempoMedioProcesso;
import br.com.itx.component.AbstractHome;

/**
 * 
 *
 */
@Name(TempoMedioProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class TempoMedioProcessoAction extends AbstractHome<TempoMedioProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tempoMedioProcessoAction";

	private List<TempoMedioProcesso> listTempoMedioProcesso = new ArrayList<TempoMedioProcesso>();
	
	
	@Override
	public void create() {
		super.create();
		instantiateList();
	}
	
	@SuppressWarnings("unchecked")
	public void instantiateList()	{
		StringBuilder sb = new StringBuilder();
		sb.append("select o");
		sb.append(" from TempoMedioProcesso o");
		listTempoMedioProcesso = getEntityManager().createQuery(sb.toString()).getResultList(); 
	}
	
	public List<TempoMedioProcesso> getListTempoMedioProcesso() {
		return listTempoMedioProcesso;
	}

	public void setListTempoMedioProcesso(
			List<TempoMedioProcesso> listTempoMedioProcesso) {
		this.listTempoMedioProcesso = listTempoMedioProcesso;
	}
	
}