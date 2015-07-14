package br.com.infox.index;

import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import br.com.infox.epp.system.EppProperties;
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
    
    @In
    private SessionAssistant sessionAssistant; 
    
    @Create
    public void init() {
        if(EppProperties.getProperty(EppProperties.PROPERTY_DESENVOLVIMENTO).equals("true")){
        	return;
        }
    	Session session = sessionAssistant.getSession();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        try {
            fullTextSession.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            LOG.error("Não possível realizar a indexação do lucene", e);
        }
    }
    
}
