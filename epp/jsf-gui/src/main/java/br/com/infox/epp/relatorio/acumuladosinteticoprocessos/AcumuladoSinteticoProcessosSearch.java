package br.com.infox.epp.relatorio.acumuladosinteticoprocessos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
import br.com.infox.epp.relatorio.acumuladosinteticoprocessos.view.AcumuladoSinteticoProcessosLocalizacaoVO;
import br.com.infox.epp.relatorio.acumuladosinteticoprocessos.view.AcumuladoSinteticoProcessosVO;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AcumuladoSinteticoProcessosSearch extends PersistenceController {
	
	@Inject
	private LocalizacaoSearch localizacaoSearch;

	public List<AcumuladoSinteticoProcessosLocalizacaoVO> gerarRelatorio(List<Fluxo> listaAssunto, List<String> listaStatus, List<String> listaMes, Integer ano, Localizacao localizacao){
		List<AcumuladoSinteticoProcessosLocalizacaoVO> resultRelatorio = new ArrayList<AcumuladoSinteticoProcessosLocalizacaoVO>();
		List<Localizacao> listaLocalizacoesPossiveis = localizacaoSearch.retrieveLocalizacaoByEstruturaFilho(Authenticator.getLocalizacaoAtual().getEstruturaFilho());
		for(Localizacao localizacaoPossivel : listaLocalizacoesPossiveis) {
			AcumuladoSinteticoProcessosLocalizacaoVO acumuladoSinteticoProcessosLocalizacaoVO = new AcumuladoSinteticoProcessosLocalizacaoVO();
			acumuladoSinteticoProcessosLocalizacaoVO.setLocalizacao(localizacaoPossivel.getLocalizacao());
			if(listaStatus.isEmpty() || listaStatus.contains("Em andamento")) {
				acumuladoSinteticoProcessosLocalizacaoVO.setListaRelatorioEmAndamento(buscarRelatorio(listaAssunto, "Em andamento", listaMes, ano, localizacaoPossivel));
			}
			if(listaStatus.isEmpty() || listaStatus.contains("Arquivados/Finalizados")) {
				acumuladoSinteticoProcessosLocalizacaoVO.setListaRelatorioFinalizadoArquivado(buscarRelatorio(listaAssunto, "Arquivados/Finalizados", listaMes, ano, localizacaoPossivel));
			}
			resultRelatorio.add(acumuladoSinteticoProcessosLocalizacaoVO);
		}
		Collections.sort(resultRelatorio);
		return resultRelatorio;
	}
	
	@SuppressWarnings("unchecked")
	private List<AcumuladoSinteticoProcessosVO> buscarRelatorio(List<Fluxo> listaAssunto, String status, List<String> listaMes, Integer ano, Localizacao localizacao) {
		StringBuilder builderQuery = new StringBuilder("Select p.nr_processo, pd.name_, u.nm_usuario, p.dt_inicio, p.dt_fim from tb_processo as p"
				+ " inner join jbpm_processinstance as pn on p.id_jbpm = pn.id_"
				+ " inner join jbpm_processdefinition as pd on pd.id_ = pn.processdefinition_"
				+ " inner join tb_usuario_login as u on u.id_usuario_login = p.id_usuario_cadastro_processo"
				+ " inner join tb_localizacao as l on l.id_localizacao = p.id_localizacao"
				+ " where l.id_localizacao = :idLocalizacao and p.id_jbpm = pn.id_ and pd.name_ in :assunto");
		
		if(status.equals("Em andamento")) {
			builderQuery.append(" and p.dt_fim IS NULL");
		}
		
		String andOrBetween = " and (";
		for(String mes : listaMes) {
			builderQuery.append(andOrBetween);
			if(status.equals("Em andamento")) {
				builderQuery.append(" p.dt_inicio between ");
			} else if (status.equals("Arquivados/Finalizados")) {
				builderQuery.append(" p.dt_fim between ");
			}
			builderQuery.append(getIntervaloEntreDatasBetween(mes, ano));
			andOrBetween = " or ";
		}
		
		if(status.equals("Em andamento")) {
			builderQuery.append(") order by p.dt_inicio");
		} else if (status.equals("Arquivados/Finalizados")) {
			builderQuery.append(") order by p.dt_fim");
		}
		
		List<Object[]> resultSet = getEntityManager().createNativeQuery(builderQuery.toString()).setParameter("assunto", listaAssunto).setParameter("idLocalizacao", localizacao.getIdLocalizacao()).getResultList();
		List<AcumuladoSinteticoProcessosVO> listaResultado = new ArrayList<AcumuladoSinteticoProcessosVO>();
		for(Object[] result : resultSet) {
			listaResultado.add(new AcumuladoSinteticoProcessosVO(getResultString(result[0]), getResultString(result[1]), getResultString(result[2]), getResultDate(result[3]), getResultDate(result[4])));
		}
		return listaResultado;
	}
	
	private String getResultString(Object result) {
		return result == null? "" : result.toString();
	}
	
	private Date getResultDate(Object result) {
		return result == null? null : new Date(((Timestamp) result).getTime());
	}
	
	private String getIntervaloEntreDatasBetween(String mes, Integer ano) {
		int indexMes = DateUtil.getListaTodosMeses().indexOf(mes);
		return String.format("'%3$d-%2$02d-01' and '%3$d-%2$02d-%1$02d'", getUltimoDiaMes(ano, indexMes), indexMes + 1, ano);
	}
	
	private int getUltimoDiaMes(int ano, int mes) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(ano, mes, 1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}
