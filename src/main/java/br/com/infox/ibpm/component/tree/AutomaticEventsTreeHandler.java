package br.com.infox.ibpm.component.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.EventoAgrupamento;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.home.ProcessoDocumentoHome;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.home.api.IProcessoDocumentoHome;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.service.AssinaturaDocumentoService;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

/**
 * Classe que realiza a tarefa de registrar os eventos e exibir a tree proveniente
 * de uma lista de Agrupamentos.
 * @author Infox
 *
 */
@Name(AutomaticEventsTreeHandler.NAME)
@Scope(ScopeType.PAGE)
@Install(precedence=Install.FRAMEWORK)
@BypassInterceptors 
public class AutomaticEventsTreeHandler extends AbstractTreeHandler<Evento> {

	public static final String LISTA_EVENTO_VARIABLE = "listaEvento";
	private static final LogProvider LOG = Logging.getLogProvider(AutomaticEventsTreeHandler.class);
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "automaticEventsTree";
	public static final String REGISTRA_EVENTO_EVENT = "EvenstTreeHandler.registrarEvento";
	public static final String REGISTRA_EVENTO_PD_EVENT = "EvenstTreeHandler.registrarEventoProcessoDocumento";
	public static final String AFTER_REGISTER_EVENT = "afterRegisterEvents";
	
	private Map<Evento, List<Evento>> rootsSelectedMap = new HashMap<Evento, List<Evento>>();
	private List<EventoBean> eventoBeanList = new ArrayList<EventoBean>();
	private String idEventos;
	private boolean isAllRegistred;
	private boolean renderTree = true;
	private boolean registred;
	
	public static AutomaticEventsTreeHandler instance() {
		return (AutomaticEventsTreeHandler) org.jboss.seam.Component.getInstance(AutomaticEventsTreeHandler.NAME);
	}
	
	/**
	 * Verifica se existe eventos a serem selecionados na treeView, caso todos existentes
	 * sejam obrigatórios a lista dos roots será vazia.
	 */
	public List<EntityNode<Evento>> getRoots(String agrupamentos) {
		if (Strings.isEmpty(agrupamentos)) {
			//Prevenir erro, quando não forem informados ids de agrupamentos.
			return Collections.emptyList();
		}
		if(rootList == null || rootList.isEmpty()) {
			if(idEventos == null) {
				idEventos = getIdEventos(agrupamentos);
			}
			rootList = new ArrayList<EntityNode<Evento>>();
			Query queryRoots = getEntityManager().createQuery(getQueryRoots(agrupamentos));
			EntityNode<Evento> entityNode = createNode();
			entityNode.setIgnore(getEntityToIgnore());
			rootList = entityNode.getRoots(queryRoots);
			if(rootList.size() == 0) {
				renderTree = false;
			}
			setAllRegistred(!"".equals(idEventos) && rootList.size() == 0);
		}
		
		return rootList;
	}
	
	public void carregaEventos(){
		if(ProcessoHome.instance().getTipoProcessoDocumento() == null){
			return;
		}
		
		if(JbpmUtil.getProcessVariable(LISTA_EVENTO_VARIABLE) != null){
			List<EventoBean> listEB = JbpmUtil.getProcessVariable(LISTA_EVENTO_VARIABLE);
			IProcessoDocumentoHome pdHome = ComponentUtil.getComponent(ProcessoDocumentoHome.NAME);
			for(EventoBean eb : listEB){
				if((eb.getIdTipoProcessoDocumento() == ProcessoHome.instance().getTipoProcessoDocumento().getIdTipoProcessoDocumento())
					&& eb.getIdProcessoDocumento() == pdHome.getInstance().getIdProcessoDocumento()){
					
					Evento eventoAux = eb.getEvento();
					if (rootList != null && rootList.size() > 0){
						while (eventoAux != null){
							for (EntityNode<Evento> root : rootList) {
								if (root.getEntity().equals(eventoAux)){
									setSelecao(eb.getEvento(), eb);
									if (!eventoBeanList.contains(eb))
										eventoBeanList.add(eb);
									eventoAux = null;
									break;
								}
							}
							if (eventoAux != null) {
								Evento eventoAuxiliar = EntityUtil.find(Evento.class, eventoAux.getIdEvento());
								if (eventoAuxiliar.getEventoSuperior() == null){
									eventoAux = null;
								}
								else {
									eventoAux = eventoAuxiliar.getEventoSuperior();
								}
							}
						}
					}
			   }    		
			}	
		}
	}
	
	@Override
	public List<EntityNode<Evento>> getRoots() {
		return rootList;
	}
	
	@Override
	protected EntityNode<Evento> createNode() {
		return new EventsEntityNode(getQueryChildrenList());
	}
	
	/**
	 * Este método sobrecarregado irá ser usado pelo o getRoots() para obter a 
	 * query que irá trazer os registros que deverão aparecer na treeView.
	 * @param agrupamentos Conjunto de ids dos EventoAgrupamentos a serem exibidos.
	 * @return Query para buscar esses EventoAgrupamentos informados.
	 */
	protected String getQueryRoots(String agrupamentos) {
		StringBuilder builder = new StringBuilder();
		builder.append("select ea from EventoAgrupamento ea ")
			   .append("where ea.agrupamento.idAgrupamento in (");
		builder.append(agrupamentos);
		builder.append(") ");
		if(!"".equals(idEventos)) {
			builder.append("and ea.evento.idEvento not in ( ");
			builder.append(idEventos);
			builder.append(") ");
		}
		builder.append("order by ea.evento");
		return builder.toString();
	}

	@Override
	protected String getQueryChildren() {
		return "select n from Evento n where n.eventoSuperior = :"
			  + EntityNode.PARENT_NODE;
	}
	
	/**
	 * Método de implementação obrigatória, porém foi sobrescrevido
	 * o getRoots que chama outro QueryRoots(), por isso este perdeu 
	 * sua funcionalidade nesta classe.
	 */
	@Override
	protected String getQueryRoots() {
		return null;
	}

	/**
	 * Retorna a lista dos Eventos Agrupamentos referente aos agrupamentos 
	 * informados.
	 * @return Uma lista com os ids dos eventos agrupamentos concatenados
	 */
	private String getIdEventos(String agrupamentos) {
		StringBuilder idList = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		sb.append("select ea from EventoAgrupamento ea ")
		  .append("where ea.agrupamento.idAgrupamento in (")
		  .append(agrupamentos)
		  .append(")");
		Query q = getEntityManager().createQuery(sb.toString());
		List<EventoAgrupamento> geList = q.getResultList();
		eventoBeanList.clear();
		for (int i=0; i<geList.size(); i++) {
			if(geList.get(i).getEvento().getEventoList().size() == 0) {
				EventoBean eb = new EventoBean();
				eb.setEvento(geList.get(i).getEvento());
				eb.setMultiplo(geList.get(i).getMultiplo());
				eb.setExcluir(false);
				eb.setQuantidade(1);
				eventoBeanList.add(eb);
				if(i > 0) {
					if (idList.length() > 0 ){
						idList.append(", ");
					}
				}
				idList.append(geList.get(i).getEvento().getIdEvento());
			}
		}
    	return idList.toString();
	}
	
	/**
	 * Método que retira da lista e do map dos eventos selecionados o evento
	 * informado no parametro, ele também verifica se é o último evento folha
	 * contido na lista do map, caso sim, ele também remove o seu parent do
	 * entrySet do map.
	 * @param obj Evento que deseja-se retirar da lista e dos rootsSelectedMap
	 */
	public void removeEvento(Object obj) {
		EventoBean eb = (EventoBean) obj;
		eventoBeanList.remove(eb);
		Evento rootDad = getDad(eb.getEvento());
		if(rootsSelectedMap.get(rootDad) != null && rootsSelectedMap.get(rootDad).size() <= 1) {
			rootsSelectedMap.remove(rootDad);
		} else if (rootsSelectedMap.get(rootDad) != null){
			rootsSelectedMap.get(rootDad).remove(eb.getEvento());
		}
	}
	
	public List<EventoBean> getEventoBeanList() {
		return eventoBeanList;
	}
	
	public void setEventoBeanList(List<EventoBean> eventoBeanList) {
		this.eventoBeanList = eventoBeanList;
	}

	/**
	 * Adiciona os eventos selecionados na lista de eventos a serem
	 * registrados e também os coloca no rootsSelectedMap, caso ainda
	 * não estejam em nenhum deles.
	 */
	public void setSelected(Evento selected, boolean isMultiplo) {
		if (selected.getEventoList().isEmpty()) {
			super.setSelected(selected);
			Evento parent = getDad(selected);
			List<Evento> l = rootsSelectedMap.get(parent);
			if (l == null) {
				l = new ArrayList<Evento>();
				rootsSelectedMap.put(parent, l);
				addSelected(selected, l, isMultiplo);
			} else if (!l.contains(selected)) {
				addSelected(selected, l, isMultiplo);
			}
			clearTree();
		}
	}
	
	public void setSelecao(Evento selected, EventoBean eb) {
		if (selected.getEventoList().isEmpty()) {
			super.setSelected(selected);
			Evento parent = getDad(selected);
			List<Evento> l = rootsSelectedMap.get(parent);
			if (l == null) {
				l = new ArrayList<Evento>();
				rootsSelectedMap.put(parent, l);
				l.add(selected);
				eventoBeanList.add(eb);
			} else if (!l.contains(selected)) {
				l.add(selected);
				eventoBeanList.add(eb);
			}
		}
	}

	private void addSelected(Evento selected, List<Evento> l, boolean isMultiplo) {
		IProcessoDocumentoHome documentoHome = ComponentUtil.getComponent(ProcessoDocumentoHome.NAME);
		l.add(selected);
		EventoBean eb = new EventoBean();
		eb.setEvento(selected);
		eb.setMultiplo(isMultiplo);
		eb.setExcluir(true);
		eb.setQuantidade(1);
		eb.setIdProcessoDocumento(documentoHome.getInstance().getIdProcessoDocumento());

		if (ProcessoHome.instance().getTipoProcessoDocumento() != null) {
			eb.setIdTipoProcessoDocumento(ProcessoHome.instance().getTipoProcessoDocumento().getIdTipoProcessoDocumento());
		}
		
		eventoBeanList.add(eb);
	}

	@Override
	public void selectListener(NodeSelectedEvent ev) {
		HtmlTree tree = (HtmlTree) ev.getSource();
		treeId = tree.getId();
		EventsEntityNode en = (EventsEntityNode) tree.getData(); 
		setSelected(en.getEntity(), isMultiplo(en));
		Events.instance().raiseEvent(getEventSelected(), getSelected());
	}	
	
	/**
	 * Método que varre a árvores até obter o registro pai (root) 
	 * @param en EventsEntityNode que foi selecionado pelo usuário na interface 
	 * @return true se o registro selecionado é múltiplo.
	 */
	private boolean isMultiplo(EventsEntityNode en) {
		if(en != null) {
			boolean isMultiplo = isMultiplo((EventsEntityNode) en.getParent());
			if(isMultiplo) {
				return true;
			}
			return en.isMultiplo();
		}
		return false;
	}
	
	/**
	 * Retorna qual é o pai do evento informado, sendo como base 
	 * a lista dos pais o rootList e não sua estrutura no banco de dados.
	 * @param e Evento que deseja-se saber o pai.
	 * @return Pai do evento informado contido no rootList.
	 */
	private Evento getDad(Evento evento) {		
	
		for(EntityNode<Evento> o : rootList) {
			if(o.getEntity().equals(evento)) {
				return evento;
			}
		}
		return getDad(evento.getEventoSuperior());
	}
	
	/**
	 * Verifica se ao menos um registro de cada root da tree exibida
	 * foi selecionado.
	 * @return True caso já estejam selecionados.
	 */
	public boolean getAllRootsSelected() {
		if(isAllRegistred() || rootList == null) {
			return true;
		}
		if(rootsSelectedMap.keySet() == null) {
			return false;
		}
		return getRoots().size() == rootsSelectedMap.keySet().size(); 
	}

	public void setRootsSelectedMap(Map<Evento, List<Evento>> rootsSelectedMap) {
		this.rootsSelectedMap = rootsSelectedMap;
	}

	public Map<Evento, List<Evento>> getRootsSelectedMap() {
		return rootsSelectedMap;
	}
	
	public void registrarEventosJbpm(ProcessoDocumento pd){
		List<EventoBean> lista = JbpmUtil.getProcessVariable(LISTA_EVENTO_VARIABLE);
		if(lista == null){
			lista = new ArrayList<EventoBean>(0);
		}
		
		lista = limparEventoTemporarioPorTipo(lista, pd);
		
		for(EventoBean eb : eventoBeanList){
			eb.setIdProcessoDocumento(pd.getIdProcessoDocumento());
			eb.setIdTipoProcessoDocumento(ProcessoHome.instance().getTipoProcessoDocumento().getIdTipoProcessoDocumento());
			lista.add(eb);
		}
		
		JbpmUtil.setProcessVariable(LISTA_EVENTO_VARIABLE, lista);			
	}

	/*
	 * Limpa todos os eventos do processoDocumento.
	 */
	@SuppressWarnings("static-access")
	private void limparEventoTemporario(List<EventoBean> lista, ProcessoDocumento pd){
		List<EventoBean> listaEb = new ArrayList<EventoBean>(0);
		if (lista != null) {
			for (EventoBean eventoBean : lista) {
				if (eventoBean.getIdProcessoDocumento() != pd.getIdProcessoDocumento()){
					listaEb.add(eventoBean);
				}
			}
		}
		JbpmUtil.instance().setProcessVariable(LISTA_EVENTO_VARIABLE, listaEb);
	}
	
	/*
	 * Limpa os eventos do processoDocumento de acordo com o tipoProcessoDocumento selecionado.
	 */
	@SuppressWarnings("static-access")
	private List<EventoBean> limparEventoTemporarioPorTipo(List<EventoBean> lista, ProcessoDocumento pd){
		List<EventoBean> listaEb = new ArrayList<EventoBean>(0);
		for (EventoBean eventoBean : lista) {
			if (!(eventoBean.getIdProcessoDocumento() == pd.getIdProcessoDocumento() &&
				  eventoBean.getIdTipoProcessoDocumento() == ProcessoHome.instance().getTipoProcessoDocumento().getIdTipoProcessoDocumento())){
				listaEb.add(eventoBean);
			}
		}
		JbpmUtil.instance().setProcessVariable(LISTA_EVENTO_VARIABLE, listaEb);
		return listaEb;
	}
	
	/**
	 * Método que registra os eventos selecionados, este método pode ser invocado
	 * por vários lugares, incluindo a assinatura digital. Ele também limpa a tree
	 * e seta uma variável para verificar se existe ou não mais eventos a serem 
	 * registrados nessa tarefa.
	 */
	public void registraEventos() {		
		ProcessoDocumento pd = EntityUtil.find(ProcessoDocumento.class,ProcessoHome.instance().getIdProcessoDocumento());
		if (pd != null){
			AssinaturaDocumentoService documentoService = new AssinaturaDocumentoService();
			if(documentoService.isDocumentoAssinado(pd.getIdProcessoDocumento())){
				inserirEventosProcesso(pd);	
				if(ProcessInstance.instance() != null && JbpmUtil.getProcessVariable(LISTA_EVENTO_VARIABLE) != null)
					limparEventoTemporario((List<EventoBean>) JbpmUtil.getProcessVariable(LISTA_EVENTO_VARIABLE), pd);
			}
			else {
				registrarEventosJbpm(pd);			
			}
		}
		else {
			inserirEventosProcesso(null);	
		}
		
	}

	@Observer(REGISTRA_EVENTO_PD_EVENT)
	public void registraEventosProcessoDocumento(ProcessoDocumento pd) {
		inserirEventosProcesso(pd);	
	}

	@SuppressWarnings("null")
	private void inserirEventosProcesso(ProcessoDocumento pd) {
		try {
			if(eventoBeanList == null || eventoBeanList.size() == 0) {
				return;
			}
			Usuario usuario = (Usuario) Contexts.getSessionContext().get(Authenticator.USUARIO_LOGADO);
			Processo processo = null;
			
			Integer idProcesso = ProcessoHome.instance().getInstance().getIdProcesso();
			if(idProcesso != null){
				processo = EntityUtil.find(Processo.class, idProcesso);
			}else{
				processo = JbpmUtil.getProcesso();
			}
			StringBuilder sb = new StringBuilder();
			sb.append("insert into core.tb_processo_evento (id_processo, id_evento, id_usuario, " +
					  "dt_atualizacao");
			org.jbpm.taskmgmt.exe.TaskInstance taskInstance = TaskInstance.instance();
			org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
			if(processInstance != null) {
				sb.append(", id_process_instance");
			}
			if(taskInstance != null){
				sb.append(", id_tarefa");
				sb.append(", id_jbpm_task");
			}
			if (pd != null){
				sb.append(", id_processo_documento");
			}
			sb.append(") ")
			  .append("values (:processo, :evento, :usuario, :data");
			if(processInstance != null) {
				sb.append(", :processInstance");
			}
			if(taskInstance != null){
				sb.append(", :tarefa");
				sb.append(", :idJbpm");
			}
			if (pd != null){
				sb.append(", :idProcessoDocumento");
			}
			sb.append(")");
			Query q = EntityUtil.getEntityManager().createNativeQuery(sb.toString());
			for (EventoBean eb : eventoBeanList) {
				for (int i=0; i<eb.getQuantidade();i++) {
					q.setParameter("processo", processo.getIdProcesso());
					q.setParameter("evento", eb.getEvento().getIdEvento());
					q.setParameter("usuario", usuario.getIdUsuario());
					q.setParameter("data", new Date());
					Tarefa t = null;
					if(taskInstance != null){
						q.setParameter("idJbpm", taskInstance.getId());
						t = JbpmUtil.getTarefa(taskInstance.getName(), 
											   processInstance.getProcessDefinition().getName());
						q.setParameter("tarefa", t.getIdTarefa());
					}
					if(processInstance != null) {
						q.setParameter("processInstance", processInstance.getId());
					}
					if(pd != null) {
						q.setParameter("idProcessoDocumento", pd.getIdProcessoDocumento());
					}
					q.executeUpdate();
				}
			}
			clearTree();
			rootsSelectedMap = new HashMap<Evento, List<Evento>>();
			eventoBeanList.clear();
			registred = true;
			Events.instance().raiseEvent(AFTER_REGISTER_EVENT);
		} catch (Exception ex) {
			String action = "registrar os eventos do tipo tarefa: ";
			LOG.warn(action, ex);
			throw new AplicationException(AplicationException.
					createMessage(action+ex.getLocalizedMessage(), 
								  "registraEventos()", 
								  "AutomaticEventsTreeHandler", 
								  "BPM"));
		}
	}

	public boolean getRenderTree() {
		return renderTree;
	}

	public void setRenderTree(Boolean renderTree) {
		this.renderTree = renderTree;
	}

	public Boolean getRegistred() {
		return registred;
	}
	
	public void setRegistred(boolean registred) {
		this.registred = registred;
	}
	
	public void clearList(){
		eventoBeanList = new ArrayList<EventoBean>();
		rootList = null;
		idEventos = null;
	}

	public void setAllRegistred(boolean isAllRegistred) {
		this.isAllRegistred = isAllRegistred;
	}

	public boolean isAllRegistred() {
		return isAllRegistred;
	}
	
	/**
	 * Verifica se o evento já foi lançado.
	 * @param documento
	 * @return
	 */
	public Boolean verificaRegistroEventos(ProcessoDocumento documento) {
		StringBuilder sql = new StringBuilder();		
		sql.append("select o.processoDocumento ");
		sql.append("from ProcessoEvento o where o.processo.idProcesso = :idProcesso ");
		sql.append("and o.processoDocumento.idProcessoDocumento = :idProcessoDoc ");
		Query q = EntityUtil.getEntityManager().createQuery(sql.toString());	
		q.setParameter("idProcesso", documento.getProcesso().getIdProcesso());
		q.setParameter("idProcessoDoc", documento.getIdProcessoDocumento());
		if (q.getResultList().size() > 0) {
			return true;
		}				
		return false;
	}
	
}