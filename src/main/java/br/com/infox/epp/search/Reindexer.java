package br.com.infox.epp.search;

import static br.com.infox.constants.WarningConstants.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import br.com.infox.core.transaction.TransactionService;

@Name("reindexer")
@BypassInterceptors
@Scope(ScopeType.EVENT)
public class Reindexer {

    private static final LogProvider LOG = Logging.getLogProvider(Reindexer.class);

    @SuppressWarnings(UNCHECKED)
    public void execute() {
        LOG.warn("----------- Criando indices de documentos das tarefas -------------");
        Session session = ManagedJbpmContext.instance().getSession().getSessionFactory().openSession();
        TransactionService.commitTransction();
        session.getTransaction().setTimeout(30 * 60);
        session.getTransaction().begin();
        Query query = session.createQuery("select ti from org.jbpm.taskmgmt.exe.TaskInstance as ti");
        query.setFetchSize(50);
        ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);
        int i = 0;
        try {
            File path = Indexer.getIndexerPath();
            delete(path);
            Indexer indexer = new Indexer(path);
            while (scroll.next()) {
                i++;
                if (i % 1000 == 0) {
                    LOG.warn("Indices Criados: " + i);
                }
                TaskInstance ti = (TaskInstance) scroll.get(0);
                Map<String, String> fields = new HashMap<String, String>();
                fields.put("conteudo", getTextoIndexavel(SearchHandler.getConteudo(ti)));
                indexer.index(ti.getId() + "", Collections.EMPTY_MAP, fields);
            }
        } catch (IOException e) {
            LOG.error(".execute()", e);
        }
        scroll.close();
        session.getTransaction().commit();
        LOG.warn(MessageFormat.format("----------- indices de documentos de tarefas criadas: {0} ------------- ", i));
    }

    public static void delete(File path) {
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    public static String getTextoIndexavel(String texto) {
        Document doc = Jsoup.parse(texto);
        return doc.body().text();
    }

}
