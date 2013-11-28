package br.com.infox.ibpm.process.definition.graphical.layout.view;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class TaskNodeCellView extends VertexView {

	private static final long serialVersionUID = 1L;

	public TaskNodeCellView(Object cell) {
		super(cell);
	}

	@Override
	public CellViewRenderer getRenderer() {
		return new TaskNodeRenderer();
	}

}
