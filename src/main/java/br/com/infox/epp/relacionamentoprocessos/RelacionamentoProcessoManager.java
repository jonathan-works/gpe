package br.com.infox.epp.relacionamentoprocessos;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ValidationException;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.service.ProcessoService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RelacionamentoProcessoManager extends Manager<RelacionamentoProcessoDAO, RelacionamentoProcesso> {

	@Inject
	private RelacionamentoManager relacionamentoManager;
	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private ProcessoManager processoManager;
    @Inject
    private ProcessoService processoService;
    @Inject
    private CategoriaManager categoriaManager;
    @Inject
    private NaturezaManager naturezaManager;

	private static final long serialVersionUID = 1L;
	public static final String NAME = "relacionamentoProcessoManager";

	public boolean existeRelacionamentoInterno(String numeroProcesso1, String numeroProcesso2) {
		Relacionamento relacionamento = new Relacionamento();
		
		Processo p1 = getProcessoByNumero(numeroProcesso1);
		Processo p2 = getProcessoByNumero(numeroProcesso2);
		
		RelacionamentoProcessoInterno rp1 = new RelacionamentoProcessoInterno(relacionamento, p1);
		RelacionamentoProcessoInterno rp2 = new RelacionamentoProcessoInterno(relacionamento, p2);
		
		return existeRelacionamento(rp1, rp2);		
	}
	
	public boolean existeRelacionamento(RelacionamentoProcessoInterno rp1, RelacionamentoProcesso rp2) {
		return getDao().existeRelacionamento(rp1, rp2);
	}

	private Processo getProcessoByNumero(String numeroProcesso) {
		Processo p = processoManager.getProcessoByNumero(numeroProcesso);
		if(p == null) {
			throw new ValidationException("Processo com número '" + numeroProcesso + "' não encontrado");
		}
		return p;
	}

	/**
	 * Faz o relacionamento entre dois processos internos
	 * 
	 * @param idProcesso1
	 * @param idProcesso2
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void relacionarProcessosInternos(String numeroProcesso1, String numeroProcesso2, TipoRelacionamentoProcesso tipoRelacionamento, String motivo) {
		Processo p1 = getProcessoByNumero(numeroProcesso1);
		Processo p2 = getProcessoByNumero(numeroProcesso2);

		if (existeRelacionamentoInterno(numeroProcesso1, numeroProcesso2)) {
			throw new ValidationException(String.format("Processos '%s' e '%s' já relacionados", numeroProcesso1, numeroProcesso2));
		}
		if (p1.equals(p2)) {
			throw new ValidationException("Processo não pode se relacionar com ele mesmo");
		}
		
		Relacionamento relacionamento = new Relacionamento();
		
		UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
		if (usuarioLogin == null) {
			usuarioLogin = usuarioLoginManager.getUsuarioSistema();
		}
		
		relacionamento.setDataRelacionamento(new Date());
		relacionamento.setNomeUsuario(usuarioLogin.getNomeUsuario());
		relacionamento.setTipoRelacionamentoProcesso(tipoRelacionamento);
		relacionamento.setMotivo(motivo);
		relacionamento.setAtivo(true);
		
		RelacionamentoProcessoInterno rp1 = new RelacionamentoProcessoInterno(relacionamento, p1);
		RelacionamentoProcessoInterno rp2 = new RelacionamentoProcessoInterno(relacionamento, p2);
		
		relacionamentoManager.persist(relacionamento);

		persist(rp1);
		persist(rp2);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void relacionarProcessosPorNaturezaCategoriaMetadados(Integer idProcesso, TipoRelacionamentoProcesso tipoRelacionamento, String motivo, String codigoNatureza, String codigoCategoria, Map<String, Object> metadados) {
		Processo processo = getProcesso(idProcesso);
        Natureza natureza = naturezaManager.getByCodigo(codigoNatureza);
        Categoria categoria = categoriaManager.getByCodigo(codigoCategoria);
		
		List<Processo> processos = processoService.getProcessosContendoNaturezaCategoriaMetadados(natureza, categoria, metadados);
		relacionarProcessosInternos(tipoRelacionamento, motivo, processo, processos);
	}
	
	/**
	 * Relaciona um processo interno a outros que contenham todos os metadados (com os mesmos valores) passados
	 * @param definicoesMetadados Definições específicas de metadados que serão utilizados para converter valores para pesquisa no banco
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void relacionarProcessosPorMetadados(Integer idProcesso, TipoRelacionamentoProcesso tipoRelacionamento, String motivo, Map<String, Object> metadados) {
		Processo processo = getProcesso(idProcesso);
		
		List<Processo> processos = processoService.getProcessosContendoMetadados(metadados);
		relacionarProcessosInternos(tipoRelacionamento, motivo, processo, processos);
	}

	private Processo getProcesso(Integer idProcesso) {
		Processo processo = processoManager.find(idProcesso);
		if(processo == null) {
			throw new ValidationException("Processo não encontrado");
		}
		return processo;
	}

	private void relacionarProcessosInternos(TipoRelacionamentoProcesso tipoRelacionamento, String motivo,
			Processo processo, List<Processo> processosRelacionados) {
		for(Processo processoRelacionado : processosRelacionados) {
			String numeroProcesso = processo.getNumeroProcesso();
			String numeroProcessoRelacionado = processoRelacionado.getNumeroProcesso();
			
			if(!processo.equals(processoRelacionado) && !existeRelacionamentoInterno(numeroProcesso, numeroProcessoRelacionado)) {
				relacionarProcessosInternos(numeroProcesso, numeroProcessoRelacionado, tipoRelacionamento, motivo);
			}
		}
	}
	

}
