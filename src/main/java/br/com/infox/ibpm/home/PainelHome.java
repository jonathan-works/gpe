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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.core.Expressions;

import br.com.itx.util.EntityUtil;


public class PainelHome { 

	private Map<String,Map<String,String>> valores = new LinkedHashMap<String, Map<String,String>>();
	private String acao;

	public List<Map<String,String>> getValores() {
		return new ArrayList<Map<String,String>>(valores.values());
	}

	public void setValores(List<String> valores) {
		for (String valor : valores) {
			String[] s = valor.split(";");
			Map<String,String> m = new HashMap<String, String>();
			m.put("label", (String) Expressions.instance().createValueExpression(s[0]).getValue() );
			m.put("query", s[1]);
			m.put("qtd", eval(s[1]) + "");
			m.put("url", s[2]);			
			m.put("acao", s[3]);
			this.valores.put(s[3],m);
		}
	}

	private int eval(String query) {		
		EntityManager em = EntityUtil.getEntityManager();
		return em.createQuery(query).getResultList().size();
	}
	
	@SuppressWarnings("rawtypes")
	public List getList() {
		EntityManager em = EntityUtil.getEntityManager();
		return em.createQuery(valores.get(acao).get("query")).getResultList();
	}
	
	public String getAcao() {
		return acao;
	}
 
	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getTitulo() {
		return valores.get(acao).get("label");
	}
	
}