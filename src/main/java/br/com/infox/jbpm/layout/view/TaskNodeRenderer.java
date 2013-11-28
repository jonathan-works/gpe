package br.com.infox.jbpm.layout.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jgraph.graph.VertexRenderer;

import br.com.infox.jbpm.layout.cell.JbpmDefaultCell;

public class TaskNodeRenderer extends VertexRenderer {

	private static final long serialVersionUID = 1L;

	private static final Color[] COLORS = new Color[] {new Color(0x05, 0x88, 0xc0),
		new Color(118,197,238), 
		new Color(0,145,64), 
		new Color(131,194,41), 
		new Color(102,255,205), 
		new Color(253,158,102), 
		new Color(151,0,201), 
		Color.ORANGE, Color.MAGENTA, Color.PINK, Color.YELLOW, Color.CYAN };

	@Override
	public void paint(Graphics g) {
		setIcon(null);
		this.setVerticalAlignment(TOP);
		super.paint(g);
		JbpmDefaultCell cell = (JbpmDefaultCell) view.getCell();
		TaskNode node = (TaskNode) cell.getNode();
		if (! node.getTasks().isEmpty()) {
			String name = cell.getSwimlaneName();
			int i = cell.getSwimlaneIndex();
			Graphics2D g2d = (Graphics2D) g;
			int x = 3;
			int y = getHeight() - 18;
			if (i == -1) {
				i = 0;
				name = "<Não Atribuida>";		
			} else {
				if (i >= COLORS.length) {
					i = i - COLORS.length;
				}
				Color c = COLORS[i];
				g2d.setColor(c);
				g2d.fillRect(x, y, getWidth() - 5, 16);
			}
			if (cell.getTaskInstance() != null) {
				TaskInstance ti = cell.getTaskInstance();
				if (ti.getEnd() == null) {
					g2d.setColor(new Color(255, 00, 00));
				} else {
					g2d.setColor(Color.GREEN.darker());
				}
				g2d.fillRect(getWidth() - 30, 2, 28 , getHeight() - 4);
			}
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
			Rectangle2D textSize = g2d.getFontMetrics().getStringBounds(name, g2d);
			int xPos = (int) ((getWidth() - textSize.getWidth()) / 2);
			g2d.drawString(name, xPos, y + 12);
		}
	}
	
}
