package br.com.infox.ibpm.process.definition.graphical;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.inject.Named;

import org.jboss.seam.util.Base64;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.graphical.layout.JbpmLayout;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.context.ContextFacade;

@Named
@ViewScoped
public class ProcessBuilderGraph implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilderGraph.class);

    private JbpmLayout layout;
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
        String path = ContextFacade.getServletContext(null).getRealPath("/Assunto/definicao/" + getProcessBuilder().getId() + "/processImage.png");
        return new File(path).canRead();
    }

    private synchronized JbpmLayout getLayout() {
        if (layout == null) {
            try {
                layout = new JbpmLayout(getProcessBuilder().getInstance());
            } catch (Exception e) {
                LOG.error("Erro ao construir a imagem do fluxo: " + e.getMessage(), e);
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
    
    public ProcessBuilder getProcessBuilder() {
        return BeanManager.INSTANCE.getReference(ProcessBuilder.class);
    }
}
