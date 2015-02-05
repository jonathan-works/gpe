package br.com.infox.epp.processo.documento.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.processo.type.TipoProcesso;

@Name(ProcessoAnaliseDocumentoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoAnaliseDocumentoService {
	public static final String NAME = "processoAnaliseDocumentoService";
	
	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	@In
	private ProcessoManager processoManager;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In(required = false)
	private String codigoFluxoDocumento;
	@In
	private FluxoManager fluxoManager;
	@In
	private IniciarProcessoService iniciarProcessoService;
	
	public Processo criarProcessoAnaliseDocumentos(Processo processoPai) throws DAOException {
		Fluxo fluxoDocumento = getFluxoDocumento();
		List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxoDocumento);
		if (ncfs == null || ncfs.isEmpty()) {
			throw new DAOException("Não existe Natureza/Categoria para o fluxo " + fluxoDocumento.getFluxo());
		}
		
		Processo processoAnalise = new Processo();
		processoAnalise.setNaturezaCategoriaFluxo(ncfs.get(0));
		processoAnalise.setProcessoPai(processoPai);
		processoAnalise.setNumeroProcesso("");
		processoAnalise.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
		processoAnalise.setLocalizacao(Authenticator.getLocalizacaoAtual());
		processoAnalise.setUsuarioCadastro(Authenticator.getUsuarioLogado());
		processoAnalise.setDataInicio(new Date());
		processoManager.persist(processoAnalise);
		
		criarMetadadosProcessoAnalise(processoAnalise);
		
		return processoAnalise;
	}
	
	public void inicializarFluxoDocumento(Processo processoAnalise) throws DAOException {
		Map<String, Object> variaveisJbpm = new HashMap<>();
		MetadadoProcesso metadadoTipoProcesso = processoAnalise.getProcessoPai().getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
		if (metadadoTipoProcesso != null) {
			variaveisJbpm.put("tipoProcessoPai", metadadoTipoProcesso.getValue().toString());
		}
		variaveisJbpm.put("classificacaoDocumento", processoAnalise.getDocumentoList().get(0).getClassificacaoDocumento().getDescricao());
		iniciarProcessoService.iniciarProcesso(processoAnalise, variaveisJbpm);
	}
	
	private Fluxo getFluxoDocumento() throws DAOException {
		if (codigoFluxoDocumento == null) {
			throw new DAOException("Fluxo de documento não encontrado");
		}
		Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoDocumento);
		if (fluxo == null) {
			throw new DAOException("Fluxo de documento não encontrado");
		}
		return fluxo;
	}
	
	private void criarMetadadosProcessoAnalise(Processo processoAnalise) throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processoAnalise);
		MetadadoProcesso metadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.TIPO_PROCESSO, TipoProcesso.DOCUMENTO.toString());
		metadadoProcessoManager.persist(metadado);
		
		metadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.LOCALIZACAO_DESTINO, processoAnalise.getProcessoPai().getLocalizacao().getIdLocalizacao().toString());
		metadadoProcessoManager.persist(metadado);
	}
}
