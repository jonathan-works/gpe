package br.com.infox.ibpm.process.definition.graphical.layout;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.ibpm.node.DecisionNode;
import br.com.infox.ibpm.process.definition.graphical.layout.cell.JbpmDefaultCell;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.util.JbpmUtil;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class JbpmLayout {

    private ProcessDefinition processDefinition;
    private String map;
    private JGraph graph;
    private Map<Node, TaskInstance> taskInstanceMap;

    public JbpmLayout(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }

    public JbpmLayout(ProcessDefinition processDefinition,
            Map<Node, TaskInstance> taskInstanceMap) {
        this.processDefinition = processDefinition;
        this.taskInstanceMap = taskInstanceMap;
    }

    public void paint(OutputStream out) throws IOException {
        if (graph == null) {
            makeGraph();
        }
        ImageIO.write(Util.toImage(graph), "png", out);
        makeMap();
    }

    public void drawGraph(File file) throws Exception {
        JGraph graph = makeGraph();
        file.getParentFile().mkdirs();
        ImageIO.write(Util.toImage(graph), "png", file);
    }

    @SuppressWarnings(UNCHECKED)
    private String makeMap() {
        if (graph == null) {
            makeGraph();
        }
        if (map != null) {
            return map;
        }
        StringBuilder sb = new StringBuilder();
        for (CellView view : graph.getGraphLayoutCache().getCellViews()) {
            if (view.getCell() instanceof JbpmDefaultCell) {
                JbpmDefaultCell cell = (JbpmDefaultCell) view.getCell();
                Rectangle2D r = view.getBounds();
                sb.append("nodes.addToMap('").append(cell.getNodeIndex()).append("','").append(r.getMinX() + 5).append(",").append(r.getMinY() + 5).append(",").append(r.getMaxX() + 5).append(",").append(r.getMaxY() + 5).append("', '").append(cell.getNode().getName()).append("'");
                if (cell.isTaskNode()) {
                    TaskNode node = (TaskNode) cell.getNode();
                    sb.append(",{");
                    for (Object o : node.getTasks()) {
                        Task t = (Task) o;
                        if (t.getTaskController() != null) {
                            List<VariableAccess> list = t.getTaskController().getVariableAccesses();
                            getTaskInfo(sb, list);
                        }
                    }
                    sb.append("}");
                } else {
                    sb.append(",{}");
                }
                if (cell.getNode().getNodeType() == NodeType.Decision) {
                    DecisionNode decNode = (DecisionNode) cell.getNode();
                    sb.append(", \"" + decNode.getDecisionExpression() + "\"");
                }
                sb.append(");\n");

            }
        }
        return sb.toString();
    }

    private void getTaskInfo(StringBuilder sb, List<VariableAccess> list) {
        for (int i = 0; list != null && i < list.size(); i++) {
            VariableAccess v = list.get(i);
            String[] mappedName = v.getMappedName().split(":");
            String name;
            if (mappedName.length == 1) {
                name = "";
            } else {
                name = processDefinition.getName() + ":" + mappedName[1];
            }
            String component = InfoxMessages.getInstance().get(VariableType.valueOf(mappedName[0]).getLabel());
            sb.append(i).append(": {name:'").append(JbpmUtil.getJbpmMessages().get(name)).append("', type:'").append(component).append("', readonly:'").append(!v.isWritable()).append("'}");
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
    }

    public String getMap() {
        if (map == null) {
            map = makeMap();
        }
        return map;
    }

    @SuppressWarnings(RAWTYPES)
    private JGraph makeGraph() {
        GraphModel model = new DefaultGraphModel();
        graph = new JbpmGraph(model);
        // Control-drag should clone selection
        graph.setCloneable(true);

        graph.setGridEnabled(true);
        graph.setGridVisible(true);

        // Enable edit without final RETURN keystroke
        graph.setInvokesStopCellEditing(true);

        // When over a cell, jump to its default port (we only have one, anyway)
        graph.setJumpToDefaultPort(true);

        List<DefaultGraphCell> cellList = new ArrayList<DefaultGraphCell>();
        Map<Node, DefaultGraphCell> nodes = new HashMap<Node, DefaultGraphCell>();
        processDefinition.getNodes();
        insertNodes(cellList, nodes);
        // Insert the cells via the cache, so they get selected
        graph.getGraphLayoutCache().insert(cellList.toArray());

        performLayout();

        Object[] roots = graph.getRoots(); // replace getRoots with your own
        // Object array of the cell tree roots. NOTE: these are the root cell(s)
        // of
        // the tree(s), not the roots of the graph model.
        JGraphFacade facade = new JGraphFacade(graph, roots); // Pass the
        // facade the JGraph instance
        JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout(); // Create
                                                                          // an
        // instance of the appropriate layout

        layout.setDeterministic(true);

        layout.run(facade); // Run the layout on the facade.
        Map nested = facade.createNestedMap(true, true); // Obtain a map
        // of the resulting attribute changes from the facade
        graph.getGraphLayoutCache().edit(nested); // Apply the results to
        // the actual graph

        JPanel panel = new JPanel();
        panel.setDoubleBuffered(false); // Always turn double buffering off when
                                        // exporting
        panel.add(graph);
        panel.setVisible(true);
        panel.setEnabled(true);
        panel.addNotify(); // workaround to pack() on a JFrame
        panel.validate();
        return graph;
    }

    private void insertNodes(List<DefaultGraphCell> cellList,
            Map<Node, DefaultGraphCell> nodes) {
        if (taskInstanceMap == null) {
            for (Object o : processDefinition.getNodes()) {
                Node from = (Node) o;
                DefaultGraphCell vertexFrom = addNode(from, nodes, cellList);
                List<Transition> transitions = from.getLeavingTransitions();
                if (transitions != null) {
                    for (Transition t : transitions) {
                        Node to = t.getTo();
                        DefaultGraphCell vertexTo = addNode(to, nodes, cellList);
                        addEdge(vertexFrom, vertexTo, cellList);
                    }
                }
            }
        } else {
            DefaultGraphCell vertexTo = null;
            for (Entry<Node, TaskInstance> e : taskInstanceMap.entrySet()) {
                Node from = e.getKey();
                DefaultGraphCell vertexFrom = addNode(from, nodes, cellList);
                if (vertexTo != null && !vertexTo.equals(vertexFrom)) {
                    addEdge(vertexTo, vertexFrom, cellList);
                }
                List<Transition> transitions = from.getLeavingTransitions();
                if (transitions != null) {
                    for (Transition t : transitions) {
                        Node to = t.getTo();
                        if (taskInstanceMap.containsKey(to)) {
                            if (e.getValue().getEnd() != null) {
                                vertexTo = addNode(to, nodes, cellList);
                                addEdge(vertexFrom, vertexTo, cellList);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void performLayout() {
        SugiyamaLayoutAlgorithm layout = new SugiyamaLayoutAlgorithm();
        Properties p = new Properties();
        p.put(SugiyamaLayoutAlgorithm.KEY_HORIZONTAL_SPACING, "300");
        p.put(SugiyamaLayoutAlgorithm.KEY_VERTICAL_SPACING, "100");
        layout.perform(graph, true, p);
    }

    private DefaultGraphCell addNode(Node node,
            Map<Node, DefaultGraphCell> nodes, List<DefaultGraphCell> cellList) {
        DefaultGraphCell vertexFrom = nodes.get(node);
        if (vertexFrom == null) {
            TaskInstance ti = null;
            if (taskInstanceMap != null) {
                ti = taskInstanceMap.get(node);
            }
            vertexFrom = new JbpmDefaultCell(node, ti);
            nodes.put(node, vertexFrom);
            cellList.add(vertexFrom);
        }
        return vertexFrom;
    }

    private void addEdge(DefaultGraphCell from, DefaultGraphCell to,
            List<DefaultGraphCell> cellList) {
        DefaultEdge edge = new DefaultEdge();
        edge.setSource(from.getFirstChild());
        if (to != null) {
            edge.setTarget(to.getFirstChild());
        }
        cellList.add(edge);
        int arrow = GraphConstants.ARROW_TECHNICAL;
        GraphConstants.setLineEnd(edge.getAttributes(), arrow);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        // para mudar o tipo da linha (BEZIER, SPLINE, ORTHOGONAL)
        // Routing routing = new DefaultEdge.DefaultRouting();
        // GraphConstants.setRouting(edge.getAttributes(), routing );
        GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_SPLINE);
    }

}
