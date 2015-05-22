package br.com.infox.index;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import br.com.infox.hibernate.session.SessionAssistant;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Scope(ScopeType.APPLICATION)
@Name(MassiveReindexer.NAME)
@Install
@Startup
public class MassiveReindexer {

    public static final String NAME = "massiveReindexer";
    private static final LogProvider LOG = Logging.getLogProvider(MassiveReindexer.class);
    
    @In private SessionAssistant sessionAssistant; 
    
    @Create
    /**
     * Reconstrói os índices do Lucene sempre que o epp é iniciado
     * */
    public void init() {
//        Session session = sessionAssistant.getSession();
//        FullTextSession fullTextSession = Search.getFullTextSession(session);
//        try {
//            fullTextSession.createIndexer().startAndWait();
//        } catch (InterruptedException e) {
//            LOG.error("Não possível realizar a indexação do lucene", e);
//        }
    }
    
}
