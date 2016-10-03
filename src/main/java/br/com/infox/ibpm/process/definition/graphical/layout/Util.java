package br.com.infox.ibpm.process.definition.graphical.layout;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;

import org.jgraph.JGraph;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class Util {

    private static final LogProvider LOG = Logging.getLogProvider(Util.class);

    public static RenderedImage toImage(JGraph graph) {
        Object[] cells = graph.getRoots();
        if (cells.length > 0) {
            Rectangle bounds = graph.getCellBounds(cells).getBounds();
            graph.toScreen(bounds);

            // Create a Buffered Image
            Dimension d = bounds.getSize();
            BufferedImage img = new BufferedImage(d.width + 10, d.height + 10, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setColor(graph.getBackground());
            graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
            graphics.translate(-bounds.x + 5, -bounds.y + 5);

            Object[] selection = graph.getSelectionCells();
            boolean gridVisible = graph.isGridVisible();
            graph.setGridVisible(false);
            graph.clearSelection();

            graph.paint(graphics);

            graph.setSelectionCells(selection);
            graph.setGridVisible(gridVisible);

            return img;
        }
        return null;
    }

    public static ImageIcon readImageIcon(String path) {
        if (null == path) {
            return null;
        }
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        return null == url ? null : new ImageIcon(url);
    }

    public static void writeText(File file, boolean append, String text) {
        if (file != null && text != null) {
            try {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                FileWriter out = new FileWriter(file, append);
                out.write(text);
                out.close();
            } catch (IOException e) {
                LOG.error(".writeText(file, append, text)", e);
            }
        }
    }

}
