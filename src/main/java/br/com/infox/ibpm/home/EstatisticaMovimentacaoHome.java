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
package br.com.infox.ibpm.home;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.bean.EstatisticaMovimentacao;
import br.com.infox.ibpm.component.tree.AssuntoTreeHandler;
import br.com.infox.util.Duracao;
import br.com.itx.util.EntityUtil;


@Scope(ScopeType.CONVERSATION)
@Name("estatisticaMovimentacaoHome")
@BypassInterceptors
public class EstatisticaMovimentacaoHome implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	private EstatisticaMovimentacao instance = new EstatisticaMovimentacao();
	private Integer idPesquisa;
	

	public Integer getIdPesquisa() {
		return idPesquisa;
	}
	public void setIdPesquisa(Integer idFluxoPesquisa) {
		this.idPesquisa = idFluxoPesquisa;
	}	
	
	public EstatisticaMovimentacao getInstance() {
		return instance;
	}
	
	public void setInstance(EstatisticaMovimentacao instance) {
		this.instance = instance;
	}
	
	public boolean isEditable() {
		return true;
	}

	public EntityManager getEntityManager(){
		return EntityUtil.getEntityManager();
	}
	
	public void limparTela(){
		instance = new EstatisticaMovimentacao();
		setIdPesquisa(null);
	}

	
	@SuppressWarnings("unchecked")
	public String getTempoMaximoMov() {
		StringBuffer ejbql = new StringBuffer();
		ejbql.append("select max(o.duracao) from Estatistica o ");
		ejbql.append("inner join o.fluxo.assuntoList assList ");		
		ejbql.append("where 1=1 ");
		
		processaQueryWhere(ejbql);

		Query q =  getEntityManager().createQuery(ejbql.toString());
		processaQueryParametros(q);

		List<Long> resultList = q.getResultList();
		String resp = "-";
		if (resultList.get(0) != null) {
			resp = millisecondToDay(resultList.get(0));
		}
		
		return resp;
	}
	
	@SuppressWarnings("unchecked")
	public String getTempoMinimoMov() {
		StringBuffer ejbql = new StringBuffer();
		ejbql.append("select min(o.duracao) from Estatistica o ");
		ejbql.append("inner join o.fluxo.assuntoList assuntoList ");		
		ejbql.append("where 1=1 ");
		
		processaQueryWhere(ejbql);

		Query q =  getEntityManager().createQuery(ejbql.toString());
		processaQueryParametros(q);

		List<Long> resultList = q.getResultList();
		String resp = "-";
		if (resultList.get(0) != null) {
			resp = millisecondToDay(resultList.get(0));
		}
		
		return resp;
	}
	
	@SuppressWarnings("unchecked")
	public String getTempoMedioMov() {
		StringBuffer ejbql = new StringBuffer();
		ejbql.append("select cast(avg(o.duracao) as long) from Estatistica o ");
		ejbql.append("inner join o.fluxo.assuntoList assList ");		
		ejbql.append("where 1=1 ");
		
		processaQueryWhere(ejbql);

		Query q =  getEntityManager().createQuery(ejbql.toString());
		processaQueryParametros(q);

		List<Long> resultList = q.getResultList();
		String resp = "-";
		if (resultList.get(0) != null) {
			resp = millisecondToDay(resultList.get(0));
		}
		
		return resp;
	}

	public String getNumProcessosMov() {
		StringBuffer ejbql = new StringBuffer();
		ejbql.append("select count (distinct o.processo) from Estatistica o ");
		ejbql.append("inner join o.fluxo.assuntoList assList ");		
		ejbql.append("where 1=1 ");
		
		processaQueryWhere(ejbql);
		Query q =  getEntityManager().createQuery(ejbql.toString());
		processaQueryParametros(q);
		Long resp = (Long) q.getResultList().get(0);
		return resp.toString();
	}
	
	private void processaQueryParametros(Query q) {
		if (instance.getDataInicio() != null ) {
			q.setParameter("dataInicio", instance.getDataInicio());	
		}
		if (instance.getDataFim() != null ) {
			q.setParameter("dataFim", instance.getDataFim());	
		}
	}
	
	
	private void processaQueryWhere(StringBuffer ejbql) {
		if (instance.getDataInicio() != null ) { ejbql.append(" and o.dataInicio >= :dataInicio "); }
		if (instance.getDataFim() != null ) { ejbql.append(" and o.dataFim <= :dataFim ");	}
		if (getAssTree().getSelected() != null) { ejbql.append(" and assList.fluxo = :fluxo ");	}			
	}
	@SuppressWarnings("unchecked")
	public List<Object[]> getGridEstatistica() {
		StringBuffer ejbql = new StringBuffer();
		ejbql.append("select o.nomeFluxo, o.taskName, avg(o.duracao)," +
				" sum(o.duracao), count(o.duracao) ");
		ejbql.append("from Estatistica o ");
		ejbql.append("inner join o.fluxo.assuntoList assList ");
		ejbql.append("where 1=1 ");
		
		processaQueryWhere(ejbql);
		
		ejbql.append(" group by o.nomeFluxo, o.taskName ");
		ejbql.append(" order by o.nomeFluxo, o.taskName ");

		Query q =  getEntityManager().createQuery(ejbql.toString());
		
		processaQueryParametros(q);
		
		return q.getResultList();
	}
	
	private AssuntoTreeHandler getAssTree() {
		AssuntoTreeHandler handler = (AssuntoTreeHandler) Component.getInstance("assuntoTree");
		return handler;
	}
	
	public static String millisecondToDay(long duracao) {
		Duracao d = new Duracao(duracao);
		return d.toString();
	}
	
	
	public String getHomeName() {
		return "estatisticaMovimentacaoHome";
	}
	
}