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
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.UsuarioTaskInstance;
import br.com.itx.util.EntityUtil;


@Name("userHandler")
@BypassInterceptors
@Scope(ScopeType.EVENT)
public class UserHandler {

	private static final LogProvider LOG = Logging.getLogProvider(UserHandler.class);
	private Long idTask;
	private String owner;
	private Integer idProcesso;
	
	public String getNomeUsuario(TaskInstance task) {
		String login = task.getActorId();
		if (login == null || login.equals("")) {
			return getLocalizacao(task);
		}
		UsuarioLogin u = getUsuario(login);
		if (u != null) {
			return u.getLogin();
		}
		return null;
	}
	
	public String getActorIdTarefaAtual(Integer idProcesso){
		try {
			if (this.idProcesso == null || !this.idProcesso.equals(idProcesso)) {
				String hql = "select ul.login from UsuarioLogin ul where ul.idPessoa = (" +
					"select uti.idUsuario from UsuarioTaskInstance uti where uti.idTaskInstance = :idTaskInstance)";
				Query query = EntityUtil.createQuery(hql).setParameter("idTaskInstance", getIdTaskAtual(idProcesso));
				this.owner = EntityUtil.getSingleResult(query);
				this.idProcesso = idProcesso;
			}
			return this.owner;
		} catch (NoResultException nre){
			//TODO Verificar porque o Painel de Usuário ainda tenta buscar a tarefa que foi finalizada e passada para o próximo nó do fluxo
			nre.printStackTrace();
			return null;
		}
	}

	/*
	 * O try/catch não será mais necessário quando o e-PP começar a rodar numa base limpa
	 * ele foi necessário porque antes não eram guardados os usuários das tarefas finalizadas
	 * assim, como existiam tarefas sem usuários na base antigo o getSingleResult lançava uma exceção
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
				.createQuery("select o from UsuarioLogin o where o.idPessoa = :idUsuario")
				.setParameter("idUsuario", uti.getIdUsuario())
				.getSingleResult();
		return user.getNome();
	}
	
	public UsuarioLogin getUsuario(String login) {
		if (login == null || login.equals("")) {
			return null;
		}
		UsuarioLogin u = null;
		try {
			u = (UsuarioLogin) EntityUtil.getEntityManager()
				.createQuery("select u from UsuarioLogin u where login=:login")
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
	
	private Long getIdTaskAtual(Integer idProcesso){
		if (idTask == null) {
			String hql = "select o.idTaskInstance from SituacaoProcesso o where o.idProcesso = :idProcesso";
			idTask = (Long) EntityUtil.createQuery(hql).setParameter("idProcesso", idProcesso).getSingleResult();
		}
		return idTask;
	}
	
}