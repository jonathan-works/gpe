package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_SESSAO_ANEXAR;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.ID_JBPM_TASK_PARAM;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_ANEXOS_PUBLICOS;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_TASKINSTANCE;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.PARAM_IDS_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.PARAM_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.PARAM_TIPO_NUMERACAO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.TOTAL_DOCUMENTOS_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.USUARIO_PARAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.service.SigiloDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.hibernate.session.SessionAssistant;

@AutoCreate
@Name(DocumentoDAO.NAME)
public class DocumentoDAO extends DAO<Documento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoDAO";

    @In
    private SessionAssistant sessionAssistant;
    @In
    private SigiloDocumentoService sigiloDocumentoService;

    public Integer getNextSequencial(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PROCESSO, processo);
        parameters.put(PARAM_TIPO_NUMERACAO, TipoNumeracaoEnum.S);
        return getNamedSingleResult(NEXT_SEQUENCIAL, parameters);
    }

    public String getModeloDocumentoByIdDocumento(Integer idDocumento) {
        Documento documento = find(idDocumento);
        if (documento != null) {
            return documento.getDocumentoBin().getModeloDocumento();
        }
        return null;
    }

    public List<Documento> getAnexosPublicos(long idJbpmTask) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_JBPM_TASK_PARAM, idJbpmTask);
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        String query = LIST_ANEXOS_PUBLICOS;
        if (usuarioLogado != null) {
            parameters.put(USUARIO_PARAM, usuarioLogado);
            query = LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO;
        }
        return getNamedResultList(query, parameters);
    }

    protected FullTextEntityManager getFullTextEntityManager() {
        return (FullTextEntityManager) super.getEntityManager();
    }

    public List<Documento> getListDocumentoByProcesso(Processo processo) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(PARAM_PROCESSO, processo);
        return getNamedResultList(LIST_DOCUMENTO_BY_PROCESSO, params);
    }

    public List<Documento> getDocumentoListByTask(TaskInstance task) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(ID_JBPM_TASK_PARAM, task.getId());
        return getNamedResultList(LIST_DOCUMENTO_BY_TASKINSTANCE, params);
    }

    @SuppressWarnings(UNCHECKED)
    public List<Documento> pesquisar(String searchPattern) throws TooManyClauses, ParseException {
        Session session = sessionAssistant.getSession();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        List<Documento> ret = new ArrayList<Documento>();
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, new String[] { "nome", "texto" },
                new BrazilianAnalyzer(Version.LUCENE_36));
        Query luceneQuery;
        try {
            luceneQuery = parser.parse(searchPattern);
        } catch (TooManyClauses | ParseException e) {
            throw e;
        }
        FullTextQuery hibernateQuery = fullTextSession.createFullTextQuery(luceneQuery, Documento.class);
        List<Documento> temp = hibernateQuery.list();
        for (Documento documento : temp) {
            if (documento.getAnexo()) {
                ret.add(documento);
            }
        }
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        for (Documento documento : ret) {

            if (!sigiloDocumentoService.possuiPermissao(documento, usuarioLogado)) {
                ret.remove(documento);
            }
        }
        return ret;
    }

    public int getTotalDocumentosProcesso(Processo processo) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PROCESSO, processo);
        Number total = getNamedSingleResult(TOTAL_DOCUMENTOS_PROCESSO, params);
        if (total == null) {
            return 0;
        }
        return total.intValue();
    }

    public List<Documento> getDocumentosSessaoAnexar(Processo processo, List<Integer> idsDocumentos) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PROCESSO, processo);
        params.put(PARAM_IDS_DOCUMENTO, idsDocumentos);
        return getNamedResultList(DOCUMENTOS_SESSAO_ANEXAR, params);
    }

    public Documento getDocumentoMaisRecentePorClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("classificacaoDocumento", classificacaoDocumento);
        StringBuilder sb = new StringBuilder();
        sb.append("select d");
        sb.append(" from ClassificacaoDocumento cd");
        sb.append(" inner join cd.documentoList d");
        sb.append(" where cd = :").append("classificacaoDocumento");
        sb.append(" order by d.dataInclusao desc");
        return getSingleResult(sb.toString(), parameters);
    }
}
