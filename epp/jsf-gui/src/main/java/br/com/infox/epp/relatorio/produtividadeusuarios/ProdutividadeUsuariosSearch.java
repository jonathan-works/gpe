package br.com.infox.epp.relatorio.produtividadeusuarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.entity.UsuarioPerfil_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.auditoria.HistoricoMetadadoProcesso;
import br.com.infox.epp.processo.metadado.auditoria.HistoricoMetadadoProcesso_;
import br.com.infox.epp.processo.movimentacoes.MovimentacaoTarefa;
import br.com.infox.epp.processo.movimentacoes.MovimentacaoTarefa_;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;
import br.com.infox.epp.relatorio.produtividadeusuarios.view.ProdutividadeUsuariosAssuntoQuantidadeVO;
import br.com.infox.epp.relatorio.produtividadeusuarios.view.ProdutividadeUsuariosLocalizacaoVO;
import br.com.infox.epp.relatorio.produtividadeusuarios.view.ProdutividadeUsuariosVO;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProdutividadeUsuariosSearch extends PersistenceController {
	
	@Inject
	private UsuarioLoginDAO usuarioLoginDAO;
	@Inject
	private LocalizacaoSearch localizacaoSearch;
	
	public List<ProdutividadeUsuariosVO> gerarRelatorio(List<UsuarioLogin> listaUsuario, List<Fluxo> listaAssunto, Date dataInicial, Date dataFinal) {
		List<ProdutividadeUsuariosVO> listaProdutividadeUsuariosVO = new ArrayList<ProdutividadeUsuariosVO>();
		List<Localizacao> listaLocalizacoesPossiveis = localizacaoSearch.retrieveLocalizacaoByEstruturaFilho(Authenticator.getLocalizacaoAtual().getEstruturaFilho());
		listaLocalizacoesPossiveis.add(0, Authenticator.getLocalizacaoAtual());
		for(UsuarioLogin usuarioLogin : listaUsuario) {
			usuarioLogin = usuarioLoginDAO.find(usuarioLogin.getIdUsuarioLogin());
			ProdutividadeUsuariosVO produtividadeUsuariosVO = new ProdutividadeUsuariosVO();
			produtividadeUsuariosVO.setUsuario(usuarioLogin.getNomeUsuario());
			List<Localizacao> listaLocalizacaoAdicionada = new ArrayList<Localizacao>();
			for(UsuarioPerfil usuarioPerfil : usuarioLogin.getUsuarioPerfilList()) {
				Localizacao localizacaoUsuario = usuarioPerfil.getLocalizacao();
				if(!listaLocalizacaoAdicionada.contains(localizacaoUsuario) && listaLocalizacoesPossiveis.contains(localizacaoUsuario)) {
					listaLocalizacaoAdicionada.add(localizacaoUsuario);
					ProdutividadeUsuariosLocalizacaoVO produtividadeUsuariosLocalizacaoVO = new ProdutividadeUsuariosLocalizacaoVO();
					produtividadeUsuariosLocalizacaoVO.setLocalizacao(localizacaoUsuario.getLocalizacao());
					for(Fluxo fluxo : listaAssunto) {
						ProdutividadeUsuariosAssuntoQuantidadeVO produtividadeUsuariosAssuntoQuantidadeVO = new ProdutividadeUsuariosAssuntoQuantidadeVO();
						produtividadeUsuariosAssuntoQuantidadeVO.setAssunto(fluxo.getFluxo());
						produtividadeUsuariosAssuntoQuantidadeVO.setQtdIniciada(getQuantidadeProcessosIniciadosPor(usuarioLogin, localizacaoUsuario, fluxo, dataInicial, dataFinal));
						produtividadeUsuariosAssuntoQuantidadeVO.setQtdEmAndamento(getQuantidadeProcessosEmAndamentoPor(usuarioLogin, localizacaoUsuario, fluxo, dataInicial, dataFinal));
						produtividadeUsuariosAssuntoQuantidadeVO.setQtdArquivadas(getQuantidadeProcessosFinalizadosPor(usuarioLogin, localizacaoUsuario, fluxo, dataInicial, dataFinal));
						produtividadeUsuariosLocalizacaoVO.getListaAssuntoQtdVO().add(produtividadeUsuariosAssuntoQuantidadeVO);
					}
					produtividadeUsuariosVO.getListaLocalizacaoVO().add(produtividadeUsuariosLocalizacaoVO);
				}
			}
			Collections.sort(produtividadeUsuariosVO.getListaLocalizacaoVO());
			listaProdutividadeUsuariosVO.add(produtividadeUsuariosVO);
		}
		
		return listaProdutividadeUsuariosVO;
	}

	public List<UsuarioLogin> getUsuariosLocalizacaoAbaixoHierarquia() {
		List<Localizacao> listaLocalizacaoAbaixoAtual = localizacaoSearch.retrieveLocalizacaoByEstruturaFilho(Authenticator.getLocalizacaoAtual().getEstruturaFilho());
		
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UsuarioLogin> query = cb.createQuery(UsuarioLogin.class);
        Root<UsuarioLogin> usuarioLogin = query.from(UsuarioLogin.class);
        Join<?, UsuarioPerfil> usuarioPerfil = usuarioLogin.join(UsuarioLogin_.usuarioPerfilList);
        Join<?, Localizacao> localizacao = usuarioPerfil.join(UsuarioPerfil_.localizacao);
        query.select(usuarioLogin).distinct(true);
        query.where(localizacao.in(listaLocalizacaoAbaixoAtual));
        query.orderBy(cb.asc(usuarioLogin.get(UsuarioLogin_.nomeUsuario)));
        return getEntityManager().createQuery(query).getResultList();
	}
	
	private long getQuantidadeProcessosIniciadosPor(UsuarioLogin usuarioLogin, Localizacao localizacao, Fluxo fluxo, Date dataInicial, Date dataFinal) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Processo> processo = query.from(Processo.class);
        Join<?, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo);
        query.select(cb.count(processo));
        query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo), cb.equal(processo.get(Processo_.localizacao), localizacao), cb.equal(processo.get(Processo_.usuarioCadastro), usuarioLogin), cb.between(processo.get(Processo_.dataInicio), dataInicial, dataFinal));
        return getEntityManager().createQuery(query).getSingleResult();
	}
	
	private long getQuantidadeProcessosEmAndamentoPor(UsuarioLogin usuarioLogin, Localizacao localizacao, Fluxo fluxo, Date dataInicial, Date dataFinal) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MovimentacaoTarefa> movimentacaoTarefa = query.from(MovimentacaoTarefa.class);
        Join<?, UsuarioTaskInstance> usuarioTaskInstance = movimentacaoTarefa.join(MovimentacaoTarefa_.usuarioTaskInstance);
        Join<?, Processo> processo = movimentacaoTarefa.join(MovimentacaoTarefa_.processos);
        Join<?, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo);
        query.select(cb.count(movimentacaoTarefa));
        query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo), cb.equal(usuarioTaskInstance.get(UsuarioTaskInstance_.usuario), usuarioLogin), cb.equal(usuarioTaskInstance.get(UsuarioTaskInstance_.localizacaoExterna), localizacao), cb.between(movimentacaoTarefa.get(MovimentacaoTarefa_.create), dataInicial, dataFinal), cb.isNull(movimentacaoTarefa.get(MovimentacaoTarefa_.end)));
        return getEntityManager().createQuery(query).getSingleResult();
	}
	
	private long getQuantidadeProcessosFinalizadosPor(UsuarioLogin usuarioLogin, Localizacao localizacao, Fluxo fluxo, Date dataInicial, Date dataFinal) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
        Root<HistoricoMetadadoProcesso> historicoMetadadoProcesso = query.from(HistoricoMetadadoProcesso.class);
        Root<StatusProcesso> statusProcesso = query.from(StatusProcesso.class);
        Root<MovimentacaoTarefa> movimentacaoTarefa = query.from(MovimentacaoTarefa.class);
        Join<?, UsuarioTaskInstance> usuarioTaskInstance = movimentacaoTarefa.join(MovimentacaoTarefa_.usuarioTaskInstance);
        Join<?, Processo> processo = movimentacaoTarefa.join(MovimentacaoTarefa_.processos);
        Join<?, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo);
        query.multiselect(processo, usuarioTaskInstance.get(UsuarioTaskInstance_.localizacaoExterna));
        query.where(cb.equal(statusProcesso.get(StatusProcesso_.idStatusProcesso).as(String.class), historicoMetadadoProcesso.get(HistoricoMetadadoProcesso_.valor)),
        		cb.equal(processo.get(Processo_.idProcesso), historicoMetadadoProcesso.get(HistoricoMetadadoProcesso_.idProcesso)),
        		cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo),
        		cb.equal(statusProcesso.get(StatusProcesso_.descricao), "Arquivado"),
        		cb.equal(usuarioTaskInstance.get(UsuarioTaskInstance_.usuario), usuarioLogin), cb.equal(usuarioTaskInstance.get(UsuarioTaskInstance_.localizacaoExterna), localizacao.getIdLocalizacao()), cb.between(processo.get(Processo_.dataFim), dataInicial, dataFinal));
        
        List<Processo> listaProcesso = new ArrayList<Processo>();
        for(Tuple tuple : getEntityManager().createQuery(query).getResultList()) {
        	if(!listaProcesso.contains(tuple.get(0, Processo.class)) && tuple.get(1, Localizacao.class).equals(localizacao)) {
        		listaProcesso.add(tuple.get(0, Processo.class));
        	}
        }
        return listaProcesso.size();
	}
}