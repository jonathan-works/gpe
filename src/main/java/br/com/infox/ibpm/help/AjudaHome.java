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
package br.com.infox.ibpm.help;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
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
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.entity.help.Ajuda;
import br.com.infox.ibpm.entity.help.HistoricoAjuda;
import br.com.infox.ibpm.entity.help.Pagina;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.HibernateUtil;

@Name("ajudaHome")
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unchecked")
public class AjudaHome extends AbstractHome<Ajuda>  {

	private static final String	RAWTYPES	= "rawtypes";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(AjudaHome.class);
	
	private String viewId;
	private Pagina pagina;
	private String textoPesquisa;
	@SuppressWarnings(RAWTYPES)
	private List resultado;
	private Ajuda anterior;
	
	@In
	protected EntityManager entityManager;
	
	@Override
	public Ajuda createInstance() {
		instance = new Ajuda();
		List<Ajuda> ajudaList = getEntityManager()
			.createQuery("select a from Ajuda a " +
						 "where a.pagina.url = :url " +
						 "order by a.dataRegistro desc")
			.setParameter("url", viewId)
			.getResultList();
		if (ajudaList.size() > 0) {
			anterior = ajudaList.get(0);
			instance.setTexto(anterior.getTexto());
		}
		instance.setPagina(getPagina());
		return instance;
	}
	
	@SuppressWarnings(RAWTYPES)
	public List getResultadoPesquisa() throws ParseException {
		if (getTextoPesquisa() == null) {
			return null;
		}
		if (resultado == null) {
			resultado = new ArrayList();
			FullTextEntityManager em = (FullTextEntityManager) getEntityManager();
			String[] fields = new String[]{"texto"};
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, HelpUtil.getAnalyzer());
			parser.setAllowLeadingWildcard(true);
			org.apache.lucene.search.Query query = parser.parse("*"+getTextoPesquisa()+"*");
			
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
		LOG.info("----------- Criando indices -------------");
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
		LOG.info("----------- Indices criados -------------");
	}
	
	public void reindexNoTransaction() {
		LOG.info("----------- Criando indices -------------");
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
		LOG.info("----------- Indices criados -------------");		
	}

	@Override
	public String persist() {
		Pagina page = verificaPagina();
		if (page == null) {
			page = inserirPagina();
		}
		Context session = Contexts.getSessionContext();
		Usuario user =  (Usuario) session.get("usuarioLogado");
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
			refreshGrid("historicoAjudaGrid");
			newInstance();
		}
		return ret;
	}
	
	public Pagina verificaPagina(){
		String sql = "select o from Pagina o " +
					 "where o.url = :url";
		Query q = getEntityManager().createQuery(sql);
		q.setParameter("url", viewId);
		if (q.getResultList().size() > 0){
			return (Pagina) q.getSingleResult();
		}
		return null;
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
			String ejbql = "select p from Pagina p "
					+ "where p.url = :url";
			Query query = getEntityManager().createQuery(ejbql);
			query.setParameter("url", viewId);
			try {
				pagina = (Pagina) query.getSingleResult();
			} catch (Exception e) {
				// para o caso de nao encontrar
			}
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
				QueryParser parser = new QueryParser("texto", HelpUtil.getAnalyzer());
				try {
					org.apache.lucene.search.Query query = parser.parse(textoPesquisa);
					String highlighted = HelpUtil.highlightText(query, texto, false);
					if (!highlighted.equals("")) {
						texto = highlighted;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}	
			}
		}
		return texto;
	}
	
	@Override
	public ValueExpression getCreatedMessage() {
		return createValueExpression("Alteração concluída.");
	}
	
}