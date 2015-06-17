package br.com.infox.epp.processo.situacao.manager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Tuple;

import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.seam.security.SecurityUtil;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SituacaoProcessoManager {

	@Inject
	private SituacaoProcessoDAO situacaoProcessoDAO;
	@Inject
	protected SecurityUtil securityUtil;
	
	public Tuple getRoot(Integer idFluxo, TipoProcesso tipoProcesso, boolean expedidas) {
		return situacaoProcessoDAO.getRoot(idFluxo, tipoProcesso, expedidas);
	}
	
	public List<Tuple> getRootList(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas) {
		return situacaoProcessoDAO.getRootList(tipoProcesso, comunicacoesExpedidas);
	}
	
	public List<FluxoBean> getFluxosDisponiveis(List<TipoProcesso> tiposProcessosDisponiveis) {
		List<FluxoBean> result = new ArrayList<>();
		for (TipoProcesso tipoProcesso : tiposProcessosDisponiveis) {
			List<Tuple> tupleList = null;
			if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
				if (securityUtil.checkPage("/pages/Painel/comunicacoesRecebidas.seam")) {
					tupleList = situacaoProcessoDAO.getRootList(tipoProcesso, false);
					createFluxoBeanList(result, tipoProcesso, tupleList, false);
				}
				if (securityUtil.checkPage("/pages/Painel/comunicacoesExpedidas.seam")) {
					tupleList = situacaoProcessoDAO.getRootList(tipoProcesso, true);
					createFluxoBeanList(result, tipoProcesso, tupleList, true);
				}
			} else if (TipoProcesso.COMUNICACAO_NAO_ELETRONICA.equals(tipoProcesso)) {
				tupleList = situacaoProcessoDAO.getRootList(tipoProcesso, true);
				createFluxoBeanList(result, tipoProcesso, tupleList, true);
			} else {
				tupleList = situacaoProcessoDAO.getRootList(tipoProcesso, false);
				createFluxoBeanList(result, tipoProcesso, tupleList, false);
			}
		}
		return result;
	}

	public void createFluxoBeanList(List<FluxoBean> result, TipoProcesso tipoProcesso, List<Tuple> tupleList, boolean expedida) {
		for (Tuple tuple : tupleList) {
			FluxoBean fluxoBean = new FluxoBean();
			String nome = tuple.get("nomeFluxo", String.class);
			if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
				nome = expedida ? nome.concat(" - Expedidas") : nome.concat(" - Recebidas");
			}
			fluxoBean.setName(nome);
			fluxoBean.setProcessDefinitionId(tuple.get("idFluxo", Integer.class).toString());
			fluxoBean.setTipoProcesso(tipoProcesso);
			fluxoBean.setQuantidadeProcessos(tuple.get("qtProcesso", Long.class));
			fluxoBean.setBpmn20(false);
			fluxoBean.setExpedida(expedida);
			result.add(fluxoBean);
		}
	}

	public List<Integer> getIdProcessosAbertosByIdTarefa(Tuple selected, TipoProcesso tipoProcesso, boolean isComunicacaoExpedida) {
		return situacaoProcessoDAO.getIdProcessosAbertosByIdTarefa(selected, tipoProcesso, isComunicacaoExpedida);
	}

	public List<Tuple> getChildrenList(Integer idFluxo, TipoProcesso tipoProcesso, boolean comunicacoesExpedidas) {
		return situacaoProcessoDAO.getChildrenList(idFluxo, tipoProcesso, comunicacoesExpedidas);
	}

	public List<Tuple> getCaixaList(TipoProcesso tipoProcesso, Integer idTarefa, boolean comunicacoesExpedidas) {
		return situacaoProcessoDAO.getCaixaList(tipoProcesso, idTarefa, comunicacoesExpedidas);
	}

}
