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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.access.entity.Papel;
import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name(UsuarioLocalizacaoHome.NAME)
public class UsuarioLocalizacaoHome
		extends AbstractUsuarioLocalizacaoHome<UsuarioLocalizacao> {

	public static final String NAME = "usuarioLocalizacaoHome";
	public static final String AFTER_NEW_INSTANCE_EVENT = "usuarioLocalizacao.afterNewInstanceEvent";
	private static final long serialVersionUID = 1L;
	private Localizacao localizacao;
	private Localizacao estrutura;
	private Papel papel;
	
	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	public static UsuarioLocalizacaoHome instance() {
		return ComponentUtil.getComponent("usuarioLocalizacaoHome");
	}

	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if (isManaged() && changed) {
			localizacao = getInstance().getLocalizacao();
			papel = getInstance().getPapel();
			estrutura = getInstance().getEstrutura();
		}
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		localizacao = null;
		papel = null;
		Events.instance().raiseEvent(AFTER_NEW_INSTANCE_EVENT, getInstance());
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if(localizacao != null && papel != null){
			getInstance().setLocalizacao(localizacao);
			getInstance().setPapel(papel);
			getInstance().setEstrutura(estrutura);
		}
		if (getInstance().getResponsavelLocalizacao() == null){
			getInstance().setResponsavelLocalizacao(Boolean.FALSE);
		}
		return true;
	}
	
	@Override
	public String persist() {
		if (getInstance().getResponsavelLocalizacao() == null){
			getInstance().setResponsavelLocalizacao(Boolean.FALSE);
		}
		if (checkPapelLocalizacao(instance)){
			FacesMessages.instance().clear();
			FacesMessages.instance().add(Severity.ERROR, "Localização e papel duplicados");
			return null;
		}
		UsuarioLogin usuario = getInstance().getUsuario();
		usuario.getUsuarioLocalizacaoList().add(getInstance());
		String msg = super.persist();
		getInstance().setUsuario(usuario);
		
		//TODO isso tem de sair daqui, usar um observer para o evento de usuarioLocalizacao persist 
		AbstractTreeHandler<?> tree = getComponent("localizacaoSetorPJETree");
		if (tree != null) {
			tree.clearTree();
		}
		tree = getComponent("papelUsuarioLocalizacaoPJETree");
		if (tree != null) {
			tree.clearTree();
		}
		tree = getComponent("localizacaoEstruturaTree");
		if (tree != null) {
			tree.clearTree();
		}
		tree = getComponent("uadLocalizacaoTree");
		if (tree != null) {
			tree.clearTree();
		}
		tree = getComponent("papelTree");
		if (tree != null) {
			tree.clearTree();
		}
		FacesMessages.instance().clear();
		FacesMessages.instance().add("Registro inserido com sucesso");
		return msg;
	}
	
	@Override
	public String update() {
		if (checkPapelLocalizacaoUpdate(instance)){
			FacesMessages.instance().clear();
			FacesMessages.instance().add(Severity.ERROR, "Localização e papel duplicados");
			return null;
		}
		String update = super.update();
		return update;
	}
	
	@Override
	public String remove(UsuarioLocalizacao obj) {
		setInstance(obj);
		UsuarioLogin usuario = getInstance().getUsuario();
		String msg = super.remove(obj);
		getInstance().setUsuario(usuario);
		return msg;
	}
	
	public String removeFromUsuarioLogin(UsuarioLocalizacao ul){
		ul.getUsuario().getUsuarioLocalizacaoList().remove(ul);
		EntityUtil.flush();
		return "removed";
	}
	
	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public Papel getPapel() {
		return papel;
	}
	
	@Observer("evtSelectLocalizacaoEstrutura")
	public void setLocalizacaoEstrutura(Localizacao localizacao, Localizacao estrutura) {
		setEstrutura(estrutura);
	}

	public void setEstrutura(Localizacao estrutura) {
		this.estrutura = estrutura;
	}

	public Localizacao getEstrutura() {
		return estrutura;
	}	
	
	public boolean checkPapelLocalizacao(UsuarioLocalizacao usuarioLocalizacao){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(o) from UsuarioLocalizacao o where ");
		sb.append(" o.usuario = :usuario and o.papel = :papel and ");
		sb.append("o.localizacao = :localizacao");
		if (estrutura != null) {
			sb.append(" and o.estrutura = :estrutura");
		}
		String sql = sb.toString();
		EntityManager em = getEntityManager();
		Query query = em.createQuery(sql)
			.setParameter("usuario", usuarioLocalizacao.getUsuario())
			.setParameter("papel", papel)
			.setParameter("localizacao", localizacao);
		if (estrutura != null) {
			query.setParameter("estrutura", estrutura);
		}		
		Long u = (Long) query.getSingleResult();
		return u > 0;
	}
	

	public boolean checkPapelLocalizacaoUpdate(UsuarioLocalizacao usuarioLocalizacao){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(o) from UsuarioLocalizacao o where ");
		sb.append(" o.usuario = :usuario and o.papel = :papel and ");
		sb.append(" o.localizacao = :localizacao and o.idUsuarioLocalizacao != :idUsuarioLocalizacao");
		if (estrutura != null) {
			sb.append(" and o.estrutura = :estrutura");
		}
		String sql = sb.toString();
		EntityManager em = getEntityManager();
		Query query = em.createQuery(sql)
			.setParameter("usuario", usuarioLocalizacao.getUsuario())
			.setParameter("papel", papel)
			.setParameter("localizacao", localizacao)
			.setParameter("idUsuarioLocalizacao", usuarioLocalizacao.getIdUsuarioLocalizacao());
		if (estrutura != null) {
			query.setParameter("estrutura", estrutura);
		}		
		Long u = (Long) query.getSingleResult();
		return u > 0;
	}
}