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
package br.com.infox.ibpm.jbpm.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.entity.TarefaEvento;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.home.ParametroHome;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.itx.component.Util;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name("registraEventoAction")
@Scope(ScopeType.SESSION)
@Install(precedence=Install.FRAMEWORK)
@BypassInterceptors
@Startup
public class RegistraEventoAction extends AbstractEventoAction {
	
	private static final LogProvider LOG = Logging.getLogProvider(RegistraEventoAction.class);
	private static final long serialVersionUID = 1L;
	private static final String FLUXO = "fluxo";
	private static final String TAREFA = "tarefa";
	public static final String REGISTRAR_EVENTO_ACTION_EVENT = "registrarEventosEvent";
	
	@Override
	public String getExpression() {
		return "registraEventoAction.registra";
	}
	
	@Override
	public String getFileName() {
		return "registraEvento.xhtml";
	}
	
	@Override
	public String getLabel() {
		return "Registrar um evento no processo";
	}
	
	@Override
	public boolean isPublic() {
		return true;
	}
	
	public void registraPorNome(String nomeAgrupamento) throws Exception {
		registraPorNome(nomeAgrupamento, new Date());
	}
	
	@Observer("registraEventoPorNome")
	@SuppressWarnings("unchecked")
	public void registraPorNome(String nomeAgrupamento, Date data) throws Exception {
		String hql = "select o from Agrupamento o where o.agrupamento = :nomeAgrupamento";
		EntityManager em = EntityUtil.getEntityManager();
		Query q = em.createQuery(hql);
		q.setParameter("nomeAgrupamento", nomeAgrupamento);
		List<Agrupamento> resultList = q.getResultList();
		String[] ids = new String[resultList.size()];
		int i = 0;
		for (Agrupamento agrupamento : resultList) {
			ids[i] = String.valueOf(agrupamento.getIdAgrupamento());
			i++;
		}
		if (i > 0) {
			registra(data, ids);
		} else {
			throw new RegistraEventoException("Agrupamento de Evento não Encontrado: " + nomeAgrupamento);
		}
	}

	/**
	 * Método que registra determinados agrupamentos em específico para um nó
	 * independente de seu tipo.
	 * @param idList
	 */
	public void registra(String...idList) {
		registra(new Date(), idList);
	}
	
	/**
	 * Método que registra determinados agrupamentos em específico para um nó
	 * independente de seu tipo.
	 * @param idList
	 * @param data 
	 */
	@SuppressWarnings("unchecked")
	public void registra(Date dataEvento, String...idList) {
		try {
			boolean mustClose = Util.beginTransaction();
			List<Integer> ids = new ArrayList<Integer>();
			for (String s : idList) {
				ids.add(Integer.parseInt(s));
			}
			StringBuilder sb = new StringBuilder();
			sb.append("select eventos.evento from Agrupamento o ");
			sb.append("join o.eventoAgrupamentoList eventos ");
			sb.append("where o.idAgrupamento in (:idList)");
			List<Evento> list = EntityUtil.getEntityManager().createQuery(sb.toString())
										  .setParameter("idList", ids).getResultList();
			Usuario usuario = (Usuario) Contexts.getSessionContext().get("usuarioLogado");
			
			Processo processo = null;
			try {
				processo = JbpmUtil.getProcesso();
			} catch (Exception e) { /* Não dar erro quando não houver conetxto do jbpm */}
			
			if(processo == null) {
				processo = ProcessoHome.instance().getInstance();
				if(processo == null) {
					return;
				}
			}
			for (Evento e : list) {
				registrarEvento(processo, e, usuario, dataEvento);
			}
			if(mustClose) {
				Util.commitTransction();
			}
		} catch (Exception ex) {
			String action = "registrar eventos da expressão definida no fluxo " +
							"#{registraEventoAction.registra()}: ";
			LOG.warn(action, ex);
			throw new AplicationException(AplicationException.
					createMessage(action+ex.getLocalizedMessage(), 
								  "registra()", 
								  "RegistraEventoAction", 
								  "BPM"));
		}
	}
	
	/**
	 * Método que substituiu o registra(String...idList) para verificação dos eventos disponíveis.
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	@Observer(Event.EVENTTYPE_TRANSITION)
	public void verificarEventos(ExecutionContext context) {
		try {
			Transition t = context.getTransition();
			StringBuilder sb = new StringBuilder();
			String fluxoName = context.getProcessDefinition().getName();
			String taskName = t.getTo().getName();
			Query q;
			if(t.getFrom().getNodeType() == NodeType.Task) {
				sb.append("select et from TarefaEvento et inner join et.tarefa t ") 
				  .append("where et.evento = 'ET' and ")
				  .append("t.fluxo.fluxo = :fluxo and ")
				  .append("t.tarefa = :tarefa and ")
				  .append("et.tarefaOrigem.tarefa = :tarefaOrigem");
				q = EntityUtil.getEntityManager().createQuery(sb.toString());
				q.setParameter(FLUXO, fluxoName);
				q.setParameter(TAREFA, taskName);
				q.setParameter("tarefaOrigem", t.getFrom().getName());
				for(TarefaEvento tarefaEvento : (List<TarefaEvento>) q.getResultList()) {
					registrarTarefaEventos(tarefaEvento);
				}
				sb = new StringBuilder();
				sb.append("select et from TarefaEvento et inner join et.tarefa t ") 
				  .append("where et.evento = 'ET' and ")
				  .append("t.fluxo.fluxo = :fluxo and ")
				  .append("t.tarefa = :tarefa and ")
				  .append("et.tarefaOrigem is null");
				q = EntityUtil.getEntityManager().createQuery(sb.toString());
				q.setParameter(FLUXO, fluxoName);
				q.setParameter(TAREFA, taskName);
				for(TarefaEvento tarefaEvento : (List<TarefaEvento>) q.getResultList()) {
					if(!registrarTarefaEventos(tarefaEvento)) {
						return;
					}
				}
			}
		} catch (Exception ex) {
			String action = "verificar se existem novos eventos para serem registrados na tarefa: ";
			LOG.warn(action, ex);
			throw new AplicationException(AplicationException.
					createMessage(action+ex.getLocalizedMessage(), 
								  "verificarEventos()", 
								  "RegistraEventoAction", 
								  "BPM"));
		}
	}
	
	/**
	 * Método que extrai de TarefaEvento a lista de Eventos para serem inseridos
	 * na tabela ProcessoEvento
	 * @param tarefaEvento - TarefaEvento que deseja se obter a lista de Eventos
	 * a serem registrados.
	 */
	@SuppressWarnings("unchecked")
	private boolean registrarTarefaEventos(TarefaEvento tarefaEvento) {
		try {
			if(tarefaEvento != null) {
				Usuario usuario = (Usuario) Contexts.getSessionContext().get("usuarioLogado");
				Processo processo = JbpmUtil.getProcesso();
				if(processo == null) {
					processo = ProcessoHome.instance().getInstance();
					if(processo == null) {
						return false;
					}
				}
				StringBuilder sb = new StringBuilder();
				sb.append("select eal.evento from TarefaEvento te ")
				  .append("inner join te.tarefaEventoAgrupamentoList teal ")
				  .append("inner join teal.agrupamento a ")
				  .append("inner join a.eventoAgrupamentoList eal ")
				  .append("where te = :tarefaEvento");
				Query qEventos = EntityUtil.getEntityManager().createQuery(sb.toString());
				qEventos.setParameter("tarefaEvento", tarefaEvento);
				for (Evento e : (List<Evento>) qEventos.getResultList()) {
					registrarEvento(processo, e, usuario);
				}
			}
		} catch (Exception ex) {
			String action = "registrar os eventos da tarefa: ";
			LOG.warn(action, ex);
			throw new AplicationException(AplicationException.
					createMessage(action+ex.getLocalizedMessage(), 
								  "registraTarefaEventos()", 
								  "RegistraEventoAction", 
								  "BPM"));
		}
		return true;
	}
	
	/**
	 * Método que registra o evento na tabela ProcessoEvento
	 * @param processo - Processo para ser registrado o evento 
	 * @param evento - Evento a ser registrado
	 * @param usuario - Usuario que está registrando esses eventos
	 */
	public void registrarEvento(Processo processo, Evento evento, Usuario usuario) {
		registrarEvento(processo, evento, usuario, new Date());
	}
	
	/**
	 * Método que registra o evento na tabela ProcessoEvento
	 * @param processo - Processo para ser registrado o evento 
	 * @param evento - Evento a ser registrado
	 * @param usuario - Usuario que está registrando esses eventos
	 * @param dataEvento - Momento em que o evento foi registrado
	 */
	public void registrarEvento(Processo processo, Evento evento, Usuario usuario, Date dataEvento) {
		try {
			if(evento == null) {
				return;
			}
			StringBuilder sqlInsert = new StringBuilder();
			sqlInsert.append("insert into public.tb_processo_evento (id_processo, ")
			         .append("id_evento, id_usuario, dt_atualizacao");
			org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
			String taskName = null;
			if(TaskInstance.instance() != null){
				taskName = TaskInstance.instance().getName();
			}
			Tarefa t = null;
			if(processInstance != null){
				/*
			     * JbpmUtil.getJbpmSession().flush();
			     * Este método este sendo executado para garantir que o processoInstance seja persistido antes que o evento seja
			     * lançado, pois ao gravar o evento esta dando erro de violação de chave, porque o processoInstance ainda não foi
			     * persistido no banco.
			     */
			    JbpmUtil.getJbpmSession().flush();
				t = JbpmUtil.getTarefa(taskName, processInstance.getProcessDefinition().getName());
			}
			if(processInstance != null && t != null) {
				sqlInsert.append(", id_process_instance, id_tarefa");
			}
			sqlInsert.append(") ");
			sqlInsert.append("values (:processo, :evento, :usuario, :data");
			if(processInstance != null && t != null) {
				sqlInsert.append(", :processInstance, :tarefa");
			}
			sqlInsert.append(")");
			org.hibernate.Query qInsert = JbpmUtil.getJbpmSession().createSQLQuery(sqlInsert.toString());
			qInsert.setParameter("processo", processo.getIdProcesso());
			qInsert.setParameter("evento", evento.getIdEvento());
			if(usuario == null) {
				qInsert.setParameter("usuario", Integer.parseInt(ParametroHome.getParametro(ParametroHome.ID_USUARIO_SISTEMA)));
			} else {
				qInsert.setParameter("usuario", usuario.getIdUsuario());
			}
			qInsert.setParameter("data", dataEvento);
			if(processInstance != null && t != null) {
				qInsert.setParameter("processInstance", processInstance.getId());
				qInsert.setParameter("tarefa", t.getIdTarefa());
			}
			qInsert.executeUpdate();
			if(evento.getStatus() != null) {
				String sql = "update public.tb_processo set id_status = :status " +
							 "where id_processo = :processo";
				org.hibernate.Query qStatus = JbpmUtil.getJbpmSession().createSQLQuery(sql);
				qStatus.setParameter("status", evento.getStatus().getIdStatus());
				qStatus.setParameter("processo", JbpmUtil.getProcesso().getIdProcesso());
				qStatus.executeUpdate();
				Events.instance().raiseEvent(REGISTRAR_EVENTO_ACTION_EVENT, evento);
			}
		} catch (Exception ex) {
			String action = "registrar o evento "+evento.getEvento()+": ";
			LOG.warn(action, ex);
			throw new AplicationException(AplicationException.createMessage(action+ex.getLocalizedMessage(), 
					"registrarEvento()", 
					"RegistraEventoAction",
					"BPM"));
		}
	}
	
	/**
	 * Método chamado no TaskInstanceForm, que irá verificar se existem novos
	 * eventos que devem ser registrados neste exato momento.
	 */
	@SuppressWarnings("unchecked")
	public void verificarNovosEventos() {
		String taskName = TaskInstance.instance().getTask().getName();
		String processDefinitionName = TaskInstance.instance().getTask().getProcessDefinition().getName();
		Processo processo = JbpmUtil.getProcesso();
		if(processo == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select et from TarefaEvento et ")
		  .append("inner join et.tarefa t ")
		  .append("where not exists (select etpt ")
		  .append("from ProcessoTarefaEvento etpt ")
		  .append("where etpt.processo = :processo and ")
		  .append("etpt.tarefaEvento = et) and ")
		  .append("et.evento != 'ET' and ")
		  .append("t.tarefa = :tarefa and ")
		  .append("t.fluxo.fluxo = :fluxo");
		Query q = EntityUtil.getEntityManager().createQuery(sb.toString());
		q.setParameter("processo", processo);
		q.setParameter(FLUXO, processDefinitionName);
		q.setParameter(TAREFA, taskName);
		List<TarefaEvento> tarefaEvento = q.getResultList();
		if(tarefaEvento != null && tarefaEvento.size() > 0) {
			sb = new StringBuilder();
			sb.append("insert into public.tb_processo_tarefa_evento (id_processo, ")
			  .append("id_tarefa_evento, in_registrado) values (:processo, ")
			  .append(":tarefaEvento, 'N')");
			q = EntityUtil.getEntityManager().createNativeQuery(sb.toString());
			for (TarefaEvento te : tarefaEvento) {
				q.setParameter("processo", processo.getIdProcesso());
				q.setParameter("tarefaEvento", te.getIdTarefaEvento());
				q.executeUpdate();
			}
		}
	}
	
	public static RegistraEventoAction instance() {
		return ComponentUtil.getComponent("registraEventoAction");
	}
	
	public class RegistraEventoException extends Exception {

		private static final long serialVersionUID = 1L;

		public RegistraEventoException() {
			super();
		}

		public RegistraEventoException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public RegistraEventoException(String arg0) {
			super(arg0);
		}

		public RegistraEventoException(Throwable arg0) {
			super(arg0);
		}
	}
	
}