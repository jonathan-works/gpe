package br.com.infox.ibpm.jbpm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.jbpm.layout.JbpmLayout;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.FacesUtil;

@Name(ProcessBuilderGraph.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessBuilderGraph {
	
	private static final String BASE_IMG_PATH = new Util().getContextRealPath() + "/img/fluxoGraph/";
	public static final String NAME = "processBuilderGraph";
	private transient JbpmLayout layout;
	
	private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilderGraph.class);
	
	private File graph;
	private String baseImagePath;
	
	public void paintGraph() throws IOException {
		JbpmLayout layoutOut = getLayout();
		createGraphFile();
		FileOutputStream out = new FileOutputStream(graph);
		layoutOut.paint(out);
		out.close();
	}
	
	private void createGraphFile() {
		prepareImagePath();
		if (graph != null) {
			graph.delete();
		}
		graph = new File(this.baseImagePath + new Util().getRandom());
	}
	
	private void prepareImagePath() {
		this.baseImagePath = BASE_IMG_PATH + Authenticator.getUsuarioLogado().getIdPessoa();
		File imagePathDir = new File(this.baseImagePath);
		if (!imagePathDir.exists()) {
			imagePathDir.mkdirs();
		}
	}

	@Observer("org.jboss.seam.endConversation")
	public void deleteGraph() {
		if (graph != null) {
			graph.delete();
		}
	}
	
	public boolean isGraphImage() {
		String path = FacesUtil.getServletContext(null).getRealPath(
				"/Assunto/definicao/" + pb.getId() + "/processImage.png");
		return new File(path).canRead();
	}
	
	public String getGraphPath() {
		return this.baseImagePath + graph.getName();
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
