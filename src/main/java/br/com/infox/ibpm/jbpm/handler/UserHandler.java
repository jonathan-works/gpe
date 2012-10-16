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
package br.com.infox.ibpm.jbpm.handler;

import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.itx.util.EntityUtil;


@Name("userHandler")
@BypassInterceptors
public class UserHandler {

	private static final LogProvider LOG = Logging.getLogProvider(UserHandler.class);
	
	public String getNomeUsuario(TaskInstance task) {
		String login = task.getActorId();
		if (login == null || login.equals("")) {
			return getLocalizacao(task);
		}
		Usuario u = getUsuario(login);
		if (u != null) {
			return u.getLogin();
		}
		return null;
	}

	public Usuario getUsuario(String login) {
		if (login == null || login.equals("")) {
			return null;
		}
		Usuario u = null;
		try {
			u = (Usuario) EntityUtil.getEntityManager()
				.createQuery("select u from Usuario u where login=:login")
				.setParameter("login", login)
				.getSingleResult();
		} catch (NoResultException e) {
			LOG.warn("Usuário não encontrado. Login: " + login);
		} catch (Exception e) {
			LOG.error("Erro ao buscar usuário. Login: " + login, e);
		}
		return u;
	}
	
	private String getLocalizacao(TaskInstance task) {
		String localizacao = JbpmUtil.instance().getLocalizacao(task).getCaminho();
		return "Local: " + localizacao;
	}
	
}