package br.com.infox.ibpm.process.definition.graphical.layout.view;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.GraphModel;

import br.com.infox.ibpm.process.definition.graphical.layout.cell.JbpmDefaultCell;

public class JbpmCellViewFactory extends DefaultCellViewFactory {
	
	private static final long serialVersionUID = 1L;

	@Override
	public CellView createView(GraphModel model, Object cell) {
		if (cell instanceof JbpmDefaultCell) {
			JbpmDefaultCell jc = (JbpmDefaultCell) cell;
			if (jc.isTaskNode()) {
				return new TaskNodeCellView(cell);
			}
		}
		return super.createView(model, cell);
	}

}
