package br.com.infox.ibpm.jbpm;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.jbpm.layout.JbpmLayout;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.FacesUtil;

@Name(ProcessBuilderGraph.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessBuilderGraph {
	
	public static final String NAME = "processBuilderGraph";
	private transient JbpmLayout layout;
	
	private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilderGraph.class);
	
	@Create
	public void init() {
	    pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	}
	
	/**
	 * Parâmetro Object obj é utilizado pela página graph.xhtml pelo componente
	 * mediaOutput
	 * 
	 * @param out
	 * @param obj
	 * @throws IOException
	 */
	public void paintGraph(OutputStream out, Object obj) throws IOException {
		JbpmLayout layoutOut = getLayout();
		if (layoutOut != null) {
			layoutOut.paint(out);
		}
	}
	
	public boolean isGraphImage() {
		String path = FacesUtil.getServletContext(null).getRealPath(
				"/Assunto/definicao/" + pb.getId() + "/processImage.png");
		return new File(path).canRead();
	}
	
	private synchronized JbpmLayout getLayout() {
		if (layout == null) {
			try {
				layout = new JbpmLayout(pb.getInstance());
			} catch (Exception e) {
				LOG.error(
						"Erro ao construir a imagem do fluxo: "
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
			LOG.error("Erro ao construir a imagem do fluxo: " + e.getMessage(),
					e);
			return null;
		}
	}
	
	public void clear(){
		layout = null;
	}
}
