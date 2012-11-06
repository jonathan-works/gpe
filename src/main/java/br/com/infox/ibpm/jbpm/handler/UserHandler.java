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

import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.UsuarioTaskInstance;
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

	/*
	 * O try/catch n�o ser� mais necess�rio quando o e-PP come�ar a rodar numa base limpa
	 * ele foi necess�rio porque antes n�o eram guardados os usu�rios das tarefas finalizadas
	 * assim, como existiam tarefas sem usu�rios na base antigo o getSingleResult lan�ava uma exce��o
	 * TODO retirar o try/catch
	 * */
	public String getUsuarioByTarefa(TaskInstance task){
		UsuarioTaskInstance uti;
		try{
		uti = (UsuarioTaskInstance)  
				EntityUtil.getEntityManager()
				.createQuery("select o from UsuarioTaskInstance o where o.idTaskInstance = :idTaskInstance")
				.setParameter("idTaskInstance", task.getId())
				.getSingleResult();
		} catch (Exception e){
			System.out.println(e.getMessage());
			return "";
		}
		UsuarioLogin user = (UsuarioLogin)
				EntityUtil.getEntityManager()
				.createQuery("select o from UsuarioLogin o where o.idUsuario = :idUsuario")
				.setParameter("idUsuario", uti.getIdUsuario())
				.getSingleResult();
		return user.getNome();
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
			LOG.warn("Usu�rio n�o encontrado. Login: " + login);
		} catch (Exception e) {
			LOG.error("Erro ao buscar usu�rio. Login: " + login, e);
		}
		return u;
	}
	
	private String getLocalizacao(TaskInstance task) {
		String localizacao = JbpmUtil.instance().getLocalizacao(task).getCaminho();
		return "Local: " + localizacao;
	}
	
}