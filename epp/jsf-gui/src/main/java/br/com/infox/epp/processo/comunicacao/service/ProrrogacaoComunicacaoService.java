package br.com.infox.epp.processo.comunicacao.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.task.home.TaskInstanceHome;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProrrogacaoComunicacaoService {

	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@Inject
	private CalendarioEventosManager calendarioEventosManager;
	
	public static boolean isProrrogacaoAutomaticaPorModelo() {
		String isProrrogacaoAutomaticaPorModelo = (String) Parametros.IS_PRORROGACAO_AUTOMATICA_POR_MODELO_COMUNICACAO.getValue();
		if (isProrrogacaoAutomaticaPorModelo != null && !isProrrogacaoAutomaticaPorModelo.isEmpty() && "true".equalsIgnoreCase(isProrrogacaoAutomaticaPorModelo)) {
			return true;
		}
		return false;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void prorrogarPrazo(DestinatarioModeloComunicacao destinatario, int diasProrrogacao) throws DAOException {
		Processo comunicacao = destinatario.getProcesso();
		prorrogarPrazoDestinatario(comunicacao, diasProrrogacao);
		criaMetadadoAnalisePedidoProrrogacao(comunicacao);
		if (isProrrogacaoAutomaticaPorModelo()) {
			List<DestinatarioModeloComunicacao> destinatariosList = modeloComunicacaoManager.listDestinatatiosByModeloComunicacao(destinatario.getModeloComunicacao());
			for (DestinatarioModeloComunicacao destinatarioModeloComunicacao : destinatariosList) {
				if (!destinatarioModeloComunicacao.equals(destinatario)) {
					Processo comunicacaoDestinatario = destinatarioModeloComunicacao.getProcesso();
					if (comunicacaoDestinatario.getMetadado(ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO) == null &&
							comunicacaoDestinatario.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO) != null) {
						prorrogarPrazoDestinatario(destinatarioModeloComunicacao.getProcesso(), diasProrrogacao);
					}
				}
			}
		}
	}

	private void prorrogarPrazoDestinatario(Processo comunicacao, int diasProrrogacao) {
		MetadadoProcesso metadadoDataFimCumprimento = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
		Date novoPrazo = DateUtil.getEndOfDay(calendarioEventosManager.getPrimeiroDiaUtil(metadadoDataFimCumprimento.<Date>getValue(), diasProrrogacao));
		metadadoDataFimCumprimento.setValor(new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(novoPrazo));
		metadadoProcessoManager.update(metadadoDataFimCumprimento);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void finalizarAnaliseProrrogacao(Processo comunicacao) throws DAOException{
		finalizarAnalisePedido(comunicacao);
		if (comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO) == null) {
			criaMetadadoAnalisePedidoProrrogacao(comunicacao);
		}
	}
	
	//TODO ver porque recebe processo se não usa, usa apenas o taskInstanceHome
	private void finalizarAnalisePedido(Processo comunicacao) throws DAOException{
		TaskInstanceHome.instance().end(TaskInstanceHome.instance().getName());
	}
	
	private void criaMetadadoAnalisePedidoProrrogacao(Processo comunicacao) {
		MetadadoProcessoProvider metadadoProvider = new MetadadoProcessoProvider(comunicacao);
		MetadadoProcesso metadadoAnalise = metadadoProvider.gerarMetadado(ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO, 
				new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(DateTime.now().toDate()));
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoAnalise));
	}
}
