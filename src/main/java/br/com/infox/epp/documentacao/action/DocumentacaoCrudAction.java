package br.com.infox.epp.documentacao.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

@Name(DocumentacaoCrudAction.NAME)
@Scope(ScopeType.APPLICATION)
public class DocumentacaoCrudAction implements Serializable {

    private static final LogProvider LOG = Logging
            .getLogProvider(DocumentacaoCrudAction.class);
    private static final long serialVersionUID = 1L;

    public static final String NAME = "documentacaoCrudAction";
    private String texto;

    @Create
    public void init() {
        initDefaultDocumentation();
    }

    public String getTexto() {
        initDefaultDocumentation();
        return texto;
    }

    private void initDefaultDocumentation() {
        final StringBuilder sb = new StringBuilder("");
        try {
            final File file = new File(ServletLifecycle.getServletContext()
                    .getRealPath("Documentacao/documentacaoPadrao.txt"));
            final BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(file));

            while (bufferedReader.ready()) {
                sb.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            texto = sb.toString();
        } catch (final Exception e) {
            LOG.warn("falha ao carregar arquivo de documentação padrão", e);
        }
    }

}
