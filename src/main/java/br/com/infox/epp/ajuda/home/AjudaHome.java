/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.epp.ajuda.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.ajuda.entity.Ajuda;
import br.com.infox.epp.ajuda.entity.HistoricoAjuda;
import br.com.infox.epp.ajuda.entity.Pagina;
import br.com.infox.epp.ajuda.manager.AjudaManager;
import br.com.infox.epp.ajuda.manager.PaginaManager;
import br.com.infox.epp.ajuda.util.HelpUtil;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.HibernateUtil;

@Name(AjudaHome.NAME)
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings(WarningConstants.UNCHECKED)
public class AjudaHome extends AbstractHome<Ajuda>  {

	private static final String ALTERACAO_CONCLUIDA = "Alteração concluída.";
    private static final String INDICES_CRIADOS = "----------- Indices criados -------------";
    private static final String CRIANDO_INDICES = "----------- Criando indices -------------";
    public static final String NAME = "ajudaHome";
    private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(AjudaHome.class);
	
	private String viewId;
	private Pagina pagina;
	private String textoPesquisa;
	@SuppressWarnings(WarningConstants.RAWTYPES)
	private List resultado;
	private Ajuda anterior;
	
	@In private PaginaManager paginaManager;
	@In private AjudaManager ajudaManager;
	
	@Override
	public Ajuda createInstance() {
		instance = new Ajuda();
		Ajuda ajuda = ajudaManager.getAjudaByPaginaUrl(viewId);
		if (ajuda != null){
			instance.setTexto(ajuda.getTexto());
			anterior = ajuda;
		}
		instance.setPagina(getPagina());
		return instance;
	}
	
	@SuppressWarnings(WarningConstants.RAWTYPES)
	public List getResultadoPesquisa() throws ParseException {
		if (getTextoPesquisa() == null) {
			return null;
		}
		if (resultado == null) {
			resultado = new ArrayList();
			FullTextEntityManager em = (FullTextEntityManager) getEntityManager();
			String[] fields = new String[]{"texto"};
			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, HelpUtil.getAnalyzer());
			parser.setAllowLeadingWildcard(true);
			org.apache.lucene.search.Query query = parser.parse("+"+getTextoPesquisa()+"+");
			
			FullTextQuery textQuery = em.createFullTextQuery(query, Ajuda.class);

			for (Object o : textQuery.getResultList()) {
				Ajuda a = (Ajuda) o;
				String s = HelpUtil.getBestFragments(query, a.getTexto());
				resultado.add(new Object[] {a, s});
			}
		}
		return resultado;
	}

	public void reindex() {
		LOG.info(CRIANDO_INDICES);
		FullTextEntityManager em = (FullTextEntityManager) getEntityManager();
		Session session = HibernateUtil.getSession();
		org.hibernate.Query query = session.createQuery("select a from Ajuda a");
		query.setCacheMode(CacheMode.IGNORE);
		query.setFetchSize(50);
		ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);		
		while (scroll.next()) {
			Ajuda a = (Ajuda) scroll.get(0);
		    em.index(a);
		}
		scroll.close();
		LOG.info(INDICES_CRIADOS);
	}
	
	public void reindexNoTransaction() {
		LOG.info(CRIANDO_INDICES);
		Util.commitTransction();
		FullTextEntityManager em = (FullTextEntityManager) EntityUtil.getEntityManager();
		FullTextSession fullTextSession = (FullTextSession) em.getDelegate();
		org.hibernate.Query query = fullTextSession.createQuery("select a from Ajuda a");
		query.setCacheMode(CacheMode.IGNORE);
		query.setFetchSize(50);
		ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);		
		while (scroll.next()) {
			Ajuda a = (Ajuda) scroll.get(0);
			fullTextSession.index(a);
		}
		scroll.close();
		LOG.info(INDICES_CRIADOS);		
	}

	@Override
	public String persist() {
		Pagina page = verificaPagina();
		if (page == null) {
			page = inserirPagina();
		}
		Context session = Contexts.getSessionContext();
		UsuarioLogin user =  (UsuarioLogin) session.get("usuarioLogado");
		instance.setUsuario(user);
		instance.setDataRegistro(new Date());
		instance.setPagina(page);
		String ret = super.persist();
		if ("persisted".equals(ret)) {
			if (anterior != null){
				HistoricoAjuda historico = new HistoricoAjuda();
				historico.setDataRegistro(anterior.getDataRegistro());
				historico.setPagina(anterior.getPagina());
				historico.setTexto(anterior.getTexto());
				historico.setUsuario(anterior.getUsuario());
	
				getEntityManager().remove(anterior);
				getEntityManager().persist(historico);
				EntityUtil.flush();
			}
			newInstance();
		}
		return ret;
	}
	
	public Pagina verificaPagina(){
		return paginaManager.getPaginaByUrl(viewId);
	}
	
	public Pagina inserirPagina(){
		Pagina page = new Pagina();
		page.setUrl(viewId);
		page.setDescricao(viewId);
		getEntityManager().persist(page);
		EntityUtil.flush();
		return getEntityManager().find(page.getClass(), page.getIdPagina());
	}

	public Pagina getPagina() {
		if (pagina == null) {
			return verificaPagina();
		}
		return pagina;
	}

	public void setViewId(String viewId, boolean clearSearch) {
		this.viewId = viewId;
		this.pagina = null;
		createInstance();
		if (clearSearch) {
			setTextoPesquisa(null);
		}
	}
	
	public String getViewId() {
		return viewId;
	}
	
	public void setView(String view) {
		setViewId(view, true);
	}
	
	public String getView() {
		return null;
	}
	
	public String getTextoPesquisa() {
		return textoPesquisa;
	}

	public void setTextoPesquisa(String textoPesquisa) {
		this.resultado = null;
		this.textoPesquisa = textoPesquisa;
	}
	
	public String getTexto() {
		String texto = null;
		if (instance != null) {
			texto = instance.getTexto();

			if (textoPesquisa != null && texto != null) {
				QueryParser parser = new QueryParser(Version.LUCENE_36, "texto", HelpUtil.getAnalyzer());
				try {
					org.apache.lucene.search.Query query = parser.parse(textoPesquisa);
					String highlighted = HelpUtil.highlightText(query, texto, false);
					if (!highlighted.equals("")) {
						texto = highlighted;
					}
				} catch (ParseException e) {
					LOG.error(".getTexto()", e);
				}
			}
		}
		return texto;
	}
	
	@SuppressWarnings(WarningConstants.RAWTYPES)
	@Override
	public ValueExpression getCreatedMessage() {
		return createValueExpression(ALTERACAO_CONCLUIDA);
	}
	
}