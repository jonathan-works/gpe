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
			if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
				if (securityUtil.checkPage("/pages/Painel/comunicacoesRecebidas.seam")) {
					List<FluxoBean> fluxos = situacaoProcessoDAO.getFluxoList(tipoProcesso, false, numeroProcessoRoot);
					renameFluxoComunicao(fluxos, false);
					result.addAll(fluxos);
				}
				if (securityUtil.checkPage("/pages/Painel/comunicacoesExpedidas.seam")) {
				    List<FluxoBean> fluxos = situacaoProcessoDAO.getFluxoList(tipoProcesso, true, numeroProcessoRoot);
                    renameFluxoComunicao(fluxos, true);
                    result.addAll(fluxos);
				}
			} else if (TipoProcesso.COMUNICACAO_NAO_ELETRONICA.equals(tipoProcesso)) {
			    List<FluxoBean> fluxos = situacaoProcessoDAO.getFluxoList(tipoProcesso, false, numeroProcessoRoot);
                result.addAll(fluxos);
            } else {
                List<FluxoBean> fluxos = situacaoProcessoDAO.getFluxoList(tipoProcesso, false, numeroProcessoRoot);
                result.addAll(fluxos);
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public void loadTasks(FluxoBean fluxoBean) {
	    if (fluxoBean.getTasks() == null) {
	        List<TaskBean> taskBeans = situacaoProcessoDAO.getTaskIntances(fluxoBean);
            for (TaskBean taskBean : taskBeans) {
                fluxoBean.addTaskDefinition(taskBean);
            }
	    }
	}
	
    private void renameFluxoComunicao(List<FluxoBean> fluxoBeanList, boolean expedida) {
	    for (FluxoBean fluxoBean : fluxoBeanList) {
	        String fluxoName = fluxoBean.getName();
	        fluxoBean.setName(fluxoName.concat(expedida ? "-Expedidas" : "-Recebidas"));
	    }
	}

}
