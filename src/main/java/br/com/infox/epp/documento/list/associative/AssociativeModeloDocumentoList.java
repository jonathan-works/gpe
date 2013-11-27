package br.com.infox.epp.documento.list.associative;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.ibpm.process.definition.fitter.TaskFitter;

@Name(AssociativeModeloDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class AssociativeModeloDocumentoList extends EntityList<ModeloDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "associativeModeloDocumentoList";

	private static final String DEFAULT_EJBQL = "select o from ModeloDocumento o";
	private static final String DEFAULT_ORDER = "tituloModeloDocumento";

	@In(required=false)
	private TaskFitter taskFitter;

	@Override
	protected void addSearchFields() {
		addSearchField("tituloModeloDocumento", SearchCriteria.CONTENDO);
		addSearchField("tipoModeloDocumento", SearchCriteria.IGUAL);
		addSearchField("ativo", SearchCriteria.IGUAL);
	}

	@Override
	protected String getDefaultEjbql() {
		if (validateFitter()){
			return DEFAULT_EJBQL;
		} else{
			return DEFAULT_EJBQL + " where o not in (#{taskFitter.currentTask.currentVariable.modeloDocumentoList})";
		}
	}

    private boolean validateFitter() {
        return taskFitter == null || taskFitter.getCurrentTask() == null || 
                taskFitter.getCurrentTask().getCurrentVariable() == null || 
                taskFitter.getCurrentTask().getCurrentVariable().getModeloDocumentoList() == null ||
                taskFitter.getCurrentTask().getCurrentVariable().getModeloDocumentoList().isEmpty();
    }

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}
	
	@Override
	public List<ModeloDocumento> getResultList() {
		setEjbql(getDefaultEjbql());
		return super.getResultList();
	}

	@Override
	@Transactional
	public Long getResultCount() {
		setEjbql(getDefaultEjbql());
		return super.getResultCount();
	}
	
}
