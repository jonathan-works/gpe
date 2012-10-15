/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.jbpm.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.access.entity.Papel;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.ReflectionsUtil;


public class SwimlaneHandler {

	private Swimlane swimlane;
	private Localizacao localizacao;
	private Papel papel;
	private List<UsuarioLocalizacao> localPapelList = new ArrayList<UsuarioLocalizacao>();
	private boolean dirty;
	private List<Papel> papelList;
	private boolean contabilizar = false;
	
	public SwimlaneHandler(Swimlane swimlane) {
		this.swimlane = swimlane;
	}

	public Swimlane getSwimlane() {
		return swimlane;
	}

	public void setSwimlane(Swimlane swimlane) {
		this.swimlane = swimlane;
	}

	public String getName() {
		return swimlane.getName();
	}
	
	public void setName(String name) {
		Map<String, Swimlane> swimlanes = swimlane.getTaskMgmtDefinition().getSwimlanes();
		swimlanes.remove(swimlane.getName());
		ReflectionsUtil.setValue(swimlane, "name", name);
		swimlane.getTaskMgmtDefinition().addSwimlane(swimlane);
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
		this.papel = null;
		this.contabilizar = false;
		this.papelList = null;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	} 

	public void addLocalPapel() {
		if (localizacao == null) {
			return;
		}
		UsuarioLocalizacao u = new UsuarioLocalizacao();
		u.setLocalizacao(localizacao);
		u.setPapel(papel);
		u.setContabilizar(contabilizar);
		getLocalPapelList().add(u);
		buildExpression();
	}

	public List<Papel> getPapelList() {
		if (papelList == null) {
			papelList = new ArrayList<Papel>();
			if (localizacao != null) {
				EntityManager em = EntityUtil.getEntityManager();
				papelList = em.createQuery("select distinct l.papel " +
						"from UsuarioLocalizacao l " +
						"where l.localizacao = :loc ")
						.setParameter("loc", localizacao)
						.getResultList();

			}
		}
		return papelList;
	}
	
	public void removeLocalPapel(UsuarioLocalizacao u) {
		for (Iterator<UsuarioLocalizacao> i = localPapelList.iterator(); i
				.hasNext();) {
			UsuarioLocalizacao uloc = i.next();
			Localizacao l = uloc.getLocalizacao();
			Papel p = uloc.getPapel();
			boolean mesmoPapel = false;
			if (p == null) {
				mesmoPapel = u.getPapel() == null;
			}
			else {
				mesmoPapel = p.equals(u.getPapel());
			}
			if (l.equals(u.getLocalizacao()) && mesmoPapel) {
				i.remove();
			}
		}
		buildExpression();
	}

	private void buildExpression() {
		if (getLocalPapelList().isEmpty()) { 
			swimlane.setPooledActorsExpression(null);			
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("#{localizacaoAssignment.getPooledActors('");
			boolean first = true;
			for (UsuarioLocalizacao u : getLocalPapelList()) {
				if (! first) {
					sb.append(",");
				}
				sb.append(u.getLocalizacao().getIdLocalizacao());
				if (u.getPapel() != null) {
					sb.append(":").append(u.getPapel().getIdPapel());
				}
				sb.append(":").append(u.getContabilizar());
				first = false;
			}
			sb.append("')}");
			String expression = sb.toString();
			dirty = ! expression.equals(swimlane.getPooledActorsExpression());
			swimlane.setPooledActorsExpression(expression);
		}
	}
	
	public static List<SwimlaneHandler> createList(ProcessDefinition instance) {
		List<SwimlaneHandler> ret = new ArrayList<SwimlaneHandler>();
		Map<String, Swimlane> swimlanes = instance.getTaskMgmtDefinition().getSwimlanes();
		if (swimlanes == null) {
			return ret;
		}
		Collection<Swimlane> values = swimlanes.values();
		for (Swimlane swimlane : values) {
			SwimlaneHandler sh = new SwimlaneHandler(swimlane);
			String exp = swimlane.getPooledActorsExpression();
			if (exp != null) {
				StringTokenizer st = new StringTokenizer(
						swimlane.getPooledActorsExpression(), "(,)}");
				// pula o inicio
				st.nextToken();
				while (st.hasMoreTokens()) {
					String s = st.nextToken().trim();
					s = s.replaceAll("'", "");
					String local = s;
					Papel papel = null;
					if (s.contains(":")) {
						String[] splitted = s.split(":");
						local = splitted[0];
						String idPapel = null;
						if(splitted.length == 2) {
							idPapel = splitted[1];
							if("true".equals(idPapel)) {
								sh.setContabilizar(true);
							} else if("false".equals(idPapel)) {
								sh.setContabilizar(false);
							} else {
								papel = EntityUtil.getEntityManager().find(Papel.class, 
										Integer.parseInt(idPapel));
							}
						} else if(splitted.length == 3) {
							idPapel = splitted[1];
							papel = EntityUtil.getEntityManager().find(Papel.class, 
									Integer.parseInt(idPapel));
							if(splitted[2].equals("true")) {
								sh.setContabilizar(true);
							} else {
								sh.setContabilizar(false);
							}
						}
					}
					Localizacao loc = EntityUtil.getEntityManager().find(Localizacao.class, 
							Integer.parseInt(local));
					UsuarioLocalizacao u = new UsuarioLocalizacao();
					u.setLocalizacao(loc);
					u.setPapel(papel);
					u.setContabilizar(sh.getContabilizar());
					sh.getLocalPapelList().add(u);
				}
			}
			ret.add(sh);
		}
		return ret;
	}

	public boolean isDirty() {
		return dirty;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SwimlaneHandler) {
			SwimlaneHandler sh = (SwimlaneHandler) obj;
			return this.getSwimlane().getName().equals(sh.getSwimlane().getName());
		} 
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getSwimlane().getName().hashCode();
	}

	@Override
	public String toString() {
		return swimlane.getName();
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public Papel getPapel() {
		return papel;
	}

	public void setLocalPapelList(List<UsuarioLocalizacao> localPapelList) {
		this.localPapelList = localPapelList;
	}

	public List<UsuarioLocalizacao> getLocalPapelList() {
		return localPapelList;
	}

	public void setContabilizar(boolean contabilizar) {
		this.contabilizar = contabilizar;
	}

	public boolean getContabilizar() {
		return contabilizar;
	}
	
}