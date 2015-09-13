package br.com.infox.ibpm.process.definition.graphical.layout.cell;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.MailNode;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import br.com.infox.ibpm.node.DecisionNode;
import br.com.infox.ibpm.process.definition.graphical.layout.Util;

public class JbpmDefaultCell extends DefaultGraphCell {

    private static final long serialVersionUID = 1L;
    private static Map<Class<?>, String> iconType;
    static {
        iconType = new HashMap<Class<?>, String>();
        iconType.put(StartState.class, "icons/start_state_enabled.gif");
        iconType.put(EndState.class, "icons/end_state_enabled.gif");
        iconType.put(ProcessState.class, "icons/process_state_enabled.gif");
        iconType.put(Fork.class, "icons/fork_enabled.gif");
        iconType.put(Join.class, "icons/join_enabled.gif");
        iconType.put(DecisionNode.class, "icons/decision_enabled.gif");
        iconType.put(TaskNode.class, "icons/task_node_enabled.gif");
        iconType.put(MailNode.class, "icons/mail_node_enabled.gif");
        iconType.put(org.jbpm.graph.node.MailNode.class, "icons/mail_node_enabled.gif");
    }

    protected Color bgColor = new Color(220, 220, 255);
    boolean raisedBorder = true;
    private Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 250, 50);

    private TaskInstance taskInstance;
    private Node node;
    private String swimlaneName;
    private int swimlaneIndex;

    public JbpmDefaultCell(Node node) {
        super(node.getName());
        setNode(node);
        createCell();
        if (node instanceof TaskNode) {
            TaskNode tnode = (TaskNode) node;
            Task task = (Task) tnode.getTasks().iterator().next();
            List<Swimlane> l = new ArrayList<Swimlane>(node.getProcessDefinition().getTaskMgmtDefinition().getSwimlanes().values());
            swimlaneIndex = l.indexOf(task.getSwimlane());
            swimlaneName = task.getSwimlane().getName();
        }

    }

    public JbpmDefaultCell(Node node, TaskInstance taskInstance) {
        this(node);
        setTaskInstance(taskInstance);
    }

    protected void createCell() {
        GraphConstants.setBounds(this.getAttributes(), bounds);
        GraphConstants.setGradientColor(this.getAttributes(), getBgColor());
        GraphConstants.setBackground(getAttributes(), getBgColor());
        GraphConstants.setIcon(getAttributes(), Util.readImageIcon(getIconPath()));
        GraphConstants.setOpaque(this.getAttributes(), true);
        if (raisedBorder) {
            GraphConstants.setBorder(this.getAttributes(), BorderFactory.createRaisedBevelBorder());
        } else {
            GraphConstants.setBorderColor(this.getAttributes(), Color.black);
        }
        this.addPort();
    }

    protected String getIconPath() {
        if (iconType.containsKey(node.getClass())) {
            return iconType.get(node.getClass());
        }
        return "icons/node_enabled.gif";
    }

    protected void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    protected Color getBgColor() {
        return bgColor;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public int getNodeIndex() {
        return node.getProcessDefinition().getNodes().indexOf(node);
    }

    public boolean isTaskNode() {
        return node instanceof TaskNode;
    }

    public void setTaskInstance(TaskInstance taskInstance) {
        this.taskInstance = taskInstance;
    }

    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    public void setSwimlaneName(String swimlaneName) {
        this.swimlaneName = swimlaneName;
    }

    public String getSwimlaneName() {
        return swimlaneName;
    }

    public void setSwimlaneIndex(int swimlandeIndex) {
        this.swimlaneIndex = swimlandeIndex;
    }

    public int getSwimlaneIndex() {
        return swimlaneIndex;
    }

}
