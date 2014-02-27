package br.com.infox.ibpm.process.definition.graphical;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Base64;

import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.graphical.layout.JbpmLayout;
import br.com.infox.jboss.context.ContextFacade;
import br.com.infox.jboss.util.ComponentUtil;

@Name(ProcessBuilderGraph.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessBuilderGraph implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "processBuilderGraph";
    private transient JbpmLayout layout;

    private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
    private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilderGraph.class);

    @Create
    public void init() {
        pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
    }

    private String encodedGraph;

    public void paintGraph() throws IOException {
        JbpmLayout layoutOut = getLayout();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        layoutOut.paint(out);
        out.close();
        encodedGraph = Base64.encodeBytes(out.toByteArray());
    }

    public String getEncodedGraph() {
        return encodedGraph;
    }

    public boolean isGraphImage() {
        String path = ContextFacade.getServletContext(null).getRealPath("/Assunto/definicao/"
                + pb.getId() + "/processImage.png");
        return new File(path).canRead();
    }

    private synchronized JbpmLayout getLayout() {
        if (layout == null) {
            try {
                layout = new JbpmLayout(pb.getInstance());
            } catch (Exception e) {
                LOG.error("Erro ao construir a imagem do fluxo: "
                        + e.getMessage(), e);
            }
        }
        return layout;
    }

    public String getMap() {
        JbpmLayout layoutOut = getLayout();
        try {
            return layoutOut != null ? layoutOut.getMap() : null;
        } catch (Exception e) {
            LOG.error("Erro ao construir a imagem do fluxo: " + e.getMessage(), e);
            return null;
        }
    }

    public void clear() {
        layout = null;
    }
}
