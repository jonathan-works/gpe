package br.com.infox.epp.documento.modelo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.variableinstance.LongInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.consulta.bean.MovimentacoesBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.dao.MetadadoProcessoDAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.status.dao.StatusProcessoDao;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.ibpm.task.manager.UsuarioTaskInstanceManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloDocumentoFolhaTramitacoesSearch extends PersistenceController {
	
	@Inject
    private UsuarioTaskInstanceManager usuarioTaskInstanceManager;
    @Inject
    private ProcessoTarefaManager processoTarefaManager;
    @Inject
    private StatusProcessoDao statusProcessoDao;
    @Inject
    private MetadadoProcessoDAO metadadoProcessoDAO;
    @Inject
    private DocumentoManager documentoManager;
	
	public String gerarTextoModeloDocumento(Processo processo) {
		Collection<MovimentacoesBean> listaMovimentacoes = getMovimentacoes(processo);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div style=\"border-style: solid; border-width: thin; margin-top: 15px; padding: 3px; font-weight: bold;\">Setor ou grupo</div>");
		sb.append("<div>");
		sb.append(processo.getLocalizacao().getCaminhoCompletoFormatado());
		sb.append("</div>");
		
		sb.append("<div style=\"margin-top: 15px;\">");
		sb.append("<center><strong>Tramitações</strong></center>");
		sb.append("<table style=\"width: 100%; border: none;\">");
		sb.append("<tr><th style=\"border-style: solid; border-width: thin; width: 40%;\">Usuário</th><th style=\"border-style: solid; border-width: thin; width: 40%;\">Tarefa</th><th style=\"text-align:right;border-style: solid; border-width: thin; width: 20%;\">Data</th><tr>");
		for(MovimentacoesBean movimentacoesBean : listaMovimentacoes) {
			sb.append("<tr><td style=\"font-size:13px;\">");
			sb.append(movimentacoesBean.getUsuario().getNomeUsuario());
			sb.append("</td><td style=\"font-size:13px;\">");
			sb.append(movimentacoesBean.getTarefa().getTarefa());
			sb.append("</td><td style=\"text-align:right;font-size:12px;\">");
			sb.append(getDataFormatada(movimentacoesBean.getDataInicio()));
			sb.append("</td></tr></tr>");
		}
		sb.append("</table>");
		sb.append("</div>");
		
		String statusProcesso = getStatusProcesso(processo);
		if(!StringUtil.isEmpty(statusProcesso)) {
			sb.append("<div style=\"border-style: solid; border-width: thin; margin-top: 15px; font-weight: bold;\">Despacho / Parecer</div>");
			sb.append(statusProcesso);
		}
		
		sb.append("<div style=\"border-style: solid; border-width: thin; margin-top: 15px; font-weight: bold;\">Arquivos Anexados ao Processo</div>");
		for(Documento documento : documentoManager.getListAllDocumentoByProcessoOrderData(processo)) {
			sb.append("<div style=\"margin-left: 10px;\">");
			sb.append(documento.getDescricao());
			sb.append("</div>");
		}
		return sb.toString();
	}
	
	private String getDataFormatada(Date data) {
		return DateUtil.formatarData(data, "dd/MM/yyyy HH:mm:ss");
	}
	
	public Collection<MovimentacoesBean> getMovimentacoes(Processo processo){
        List<TaskInstance> list = getTaskInstanceListMovimentacoes(processo);
        
        Collection<MovimentacoesBean> beans = new TreeSet<>(new Comparator<MovimentacoesBean>() {
            @Override
            public int compare(MovimentacoesBean o1, MovimentacoesBean o2) {
                Date dataInicio = o1.getDataInicio();
                Date dataInicio2 = o2.getDataInicio();
                int result = dataInicio2.compareTo(dataInicio);
                if (result == 0){
                    Tarefa tarefa = o1.getTarefa();
                    Tarefa tarefa2 = o2.getTarefa();
                    String nomeTarefa = tarefa.getTarefa();
                    String nomeTarefa2 = tarefa2.getTarefa();
                    result = nomeTarefa.compareToIgnoreCase(nomeTarefa2);
                }
                return result;
            }
        });
        for (TaskInstance taskInstance : list) {
                beans.add(new MovimentacoesBean(processoTarefaManager.getByTaskInstance(taskInstance.getId()), usuarioTaskInstanceManager.find(taskInstance.getId()), taskInstance));
        }
        return beans;
    }
	
	private List<TaskInstance> getTaskInstanceListMovimentacoes(Processo processo) {
		List<org.jbpm.graph.exe.ProcessInstance> processInstances = getProcessosJbpm(processo);
		List<TaskInstance> taskInstanceList = new ArrayList<>();
		
		for(org.jbpm.graph.exe.ProcessInstance processInstance : processInstances) {
			if(processInstance.getTaskMgmtInstance().getTaskInstances() != null)
				taskInstanceList.addAll(processInstance.getTaskMgmtInstance().getTaskInstances());
		}
        return taskInstanceList;
    }
	
	private List<org.jbpm.graph.exe.ProcessInstance> getProcessosJbpm(Processo processo) {
        Session jbpmSession = ManagedJbpmContext.instance().getSession();
        String hql = "from org.jbpm.context.exe.variableinstance.LongInstance v where v.name = :nomeVariavel and value = :idProcesso";
        Query query = jbpmSession.createQuery(hql);
        query.setParameter("nomeVariavel", "processo");
        query.setParameter("idProcesso", processo.getIdProcesso().longValue());
        
        @SuppressWarnings("unchecked")
		List<LongInstance> variaveis = query.list();
        List<org.jbpm.graph.exe.ProcessInstance> retorno = new ArrayList<>(); 
        for (LongInstance var : variaveis) {
            retorno.add(var.getProcessInstance());
        }
        return retorno;
    }
	
	private String getStatusProcesso(Processo processo) {
		List<MetadadoProcesso> listaStatusProcesso = metadadoProcessoDAO.getMetadadoProcessoByType(processo, "statusProcesso");
		if(!listaStatusProcesso.isEmpty()) {
			return statusProcessoDao.find(Integer.valueOf(listaStatusProcesso.get(0).getValor())).getDescricao();
		}
		return "";
	}

}
