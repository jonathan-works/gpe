package br.com.infox.epp.processo.situacao.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.TaskBean;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.seam.security.SecurityUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SituacaoProcessoManager {

	@Inject
	private SituacaoProcessoDAO situacaoProcessoDAO;
	@Inject
	protected SecurityUtil securityUtil;
	
	public List<FluxoBean> getFluxos(List<TipoProcesso> tiposProcessosDisponiveis, String numeroProcessoRoot) {
		List<FluxoBean> result = new ArrayList<>();
		for (TipoProcesso tipoProcesso : tiposProcessosDisponiveis) {
			List<TaskBean> taskBeanList = null;
			if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
				if (securityUtil.checkPage("/pages/Painel/comunicacoesRecebidas.seam")) {
					taskBeanList = situacaoProcessoDAO.getTaskIntances(tipoProcesso, false, numeroProcessoRoot);
					createFluxoBeanList(result, tipoProcesso, taskBeanList, false);
				}
				if (securityUtil.checkPage("/pages/Painel/comunicacoesExpedidas.seam")) {
					taskBeanList = situacaoProcessoDAO.getTaskIntances(tipoProcesso, true, numeroProcessoRoot);
					createFluxoBeanList(result, tipoProcesso, taskBeanList, true);
				}
			} else if (TipoProcesso.COMUNICACAO_NAO_ELETRONICA.equals(tipoProcesso)) {
				taskBeanList = situacaoProcessoDAO.getTaskIntances(tipoProcesso, true, numeroProcessoRoot);
				createFluxoBeanList(result, tipoProcesso, taskBeanList, false);
            } else {
				taskBeanList = situacaoProcessoDAO.getTaskIntances(tipoProcesso, false, numeroProcessoRoot);
				createFluxoBeanList(result, tipoProcesso, taskBeanList, false);
			}
		}
		Collections.sort(result);
		return result;
	}

	private void createFluxoBeanList(List<FluxoBean> fluxoBeanList, TipoProcesso tipoProcesso, List<TaskBean> taskBeanList, boolean expedida) {
		for (TaskBean taskBean : taskBeanList) {
		    String nomeFluxo = taskBean.getNomeFluxo();
		    if (fluxoBean == null) {
		        fluxoBean = createFluxoBean(tipoProcesso, expedida, taskBean);
		        fluxoBeanList.add(fluxoBean);
		    }
		    fluxoBean.addTaskDefinition(taskBean);
		}
	}

    private FluxoBean createFluxoBean(TipoProcesso tipoProcesso, boolean expedida, TaskBean taskBean) {
        FluxoBean fluxoBean;
        fluxoBean = new FluxoBean();
        String nome = taskBean.getNomeFluxo();
		    if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
		        nomeFluxo = expedida ? nomeFluxo.concat(" - Expedidas") : nomeFluxo.concat(" - Recebidas");
		    }
		    FluxoBean fluxoBean = getFluxoBeanByProcessDefinitionId(fluxoBeanList, nomeFluxo);
		    if (fluxoBean == null) {
		        fluxoBean = createFluxoBean(tipoProcesso, expedida, taskBean, nomeFluxo);
		        fluxoBeanList.add(fluxoBean);
		    }
		    fluxoBean.addTaskDefinition(taskBean);
		}
	}

    private FluxoBean createFluxoBean(TipoProcesso tipoProcesso, boolean expedida, TaskBean taskBean, String nome) {
        FluxoBean fluxoBean;
        fluxoBean = new FluxoBean();
        fluxoBean.setName(nome);
        fluxoBean.setProcessDefinitionId(taskBean.getIdFluxo().toString());
        fluxoBean.setTipoProcesso(tipoProcesso);
        fluxoBean.setBpmn20(false);
        fluxoBean.setExpedida(expedida);
        return fluxoBean;
    }
	
	private FluxoBean getFluxoBeanByProcessDefinitionId(List<FluxoBean> result, String nomeFluxo) {
	    for (FluxoBean fluxoBean : result) {
	        if (fluxoBean.getName().equals(nomeFluxo)) {
	            return fluxoBean;
	        }
	    }
	    return null;
	}

}
