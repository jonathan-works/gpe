package br.com.infox.epp.processo.comunicacao.list;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.query.PastaQuery;
import br.com.infox.epp.processo.entity.Processo;

@Name(DocumentoDisponivelComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class DocumentoDisponivelComunicacaoList extends EntityList<Documento> implements ActionListener {
	public static final String NAME = "documentoDisponivelComunicacaoList";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoDisponivelComunicacaoList.class);

	private static final String DEFAULT_EJBQL = "select o from Documento o "
			+ "inner join o.documentoBin bin "
			+ "left join bin.assinaturas a "
			+ "where o.processo = #{documentoDisponivelComunicacaoList.processo} and o.excluido = false and "
			+ "bin.minuta = false and "
			+ "not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) "
			+ "and DocumentoSuficientementeAssinado(bin.id) = true ";
	
	private static final String DEFAULT_ORDER = "a.dataAssinatura, o.dataInclusao desc";
	
	@In
	private PastaManager pastaManager;
	@In
	private ActionMessagesService actionMessagesService;

	private Processo processo;
	private Set<Integer> idsDocumentos = new HashSet<>();
	private Map<String, Object> paramsPasta = new HashMap<>();
	private String filterPasta = PastaQuery.FILTER_SUFICIENTEMENTE_ASSINADO + PastaQuery.FILTER_SIGILO + PastaQuery.FILTER_EXCLUIDO;
	
	@Override
	protected void addSearchFields() {
		addSearchField("pasta", SearchCriteria.IGUAL);
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}
	
	public void adicionarIdDocumento(Integer id) {
		idsDocumentos.add(id);
		refreshQuery();
		if (!paramsPasta.containsKey(PastaQuery.PARAM_IDS_DOCUMENTOS)) {
			paramsPasta.put(PastaQuery.PARAM_IDS_DOCUMENTOS, idsDocumentos);
			filterPasta += PastaQuery.FILTER_DOCUMENTOS;
		}
	}
	
	public void removerIdDocumento(Integer id) {
		idsDocumentos.remove(id);
		refreshQuery();
		if (idsDocumentos.isEmpty()) {
			paramsPasta.remove(PastaQuery.PARAM_IDS_DOCUMENTOS);
			filterPasta = PastaQuery.FILTER_SUFICIENTEMENTE_ASSINADO + PastaQuery.FILTER_SIGILO + PastaQuery.FILTER_EXCLUIDO;
		}
	}
	
	private void refreshQuery() {
		StringBuilder hql = new StringBuilder(DEFAULT_EJBQL);
		if (!idsDocumentos.isEmpty()) {
			hql.append(" and o.id not in (");
			Iterator<Integer> it = idsDocumentos.iterator();
			while (it.hasNext()) {
				hql.append(it.next());
				if (it.hasNext()) {
					hql.append(",");
				}
			}
			hql.append(")");
		}
		setEjbql(hql.toString());
	}
	
	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
		Documento entity = getEntity();
		if (entity.getPasta() == null) {
			try {
				entity.setPasta(pastaManager.getDefaultFolder(processo));
			} catch (DAOException e) {
				LOG.error("", e);
				actionMessagesService.handleDAOException(e);
			}
		}
	}

	@Override
	public void processAction(ActionEvent event) throws AbortProcessingException {
		Map<String, Object> attributes = event.getComponent().getAttributes();
        Object o = attributes.get("pastaToSelect");
        if (o instanceof Pasta) {
            getEntity().setPasta((Pasta) o);
            return;
        }
	}
	
	public String getTextoPasta(Pasta pasta) {
		int totalDocumentosPasta = pastaManager.getTotalDocumentosPasta(pasta, filterPasta, paramsPasta);
		return pastaManager.getNomePasta(pasta, totalDocumentosPasta);
	}
}
