package br.com.infox.ibpm.process.definition.graphical.layout;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;

import br.com.infox.ibpm.process.definition.graphical.layout.view.JbpmCellViewFactory;

public class JbpmGraph extends JGraph {

	private static final long serialVersionUID = 1L;

	public JbpmGraph(GraphModel model) {
		super(model);
		getGraphLayoutCache().setFactory(new JbpmCellViewFactory());
	}
	
	
}
