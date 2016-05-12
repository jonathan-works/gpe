package br.com.infox.epp.processo.iniciar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.tipoParte.TipoParteSearch;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class IniciarProcessoView implements Serializable {

	private static final long serialVersionUID = 1L;

    @Inject
    private IniciarProcessoService iniciarProcessoService;
    @Inject
    private NatCatFluxoLocalizacaoDAO natCatFluxoLocalizacaoDAO;
    @Inject
    private PessoaFisicaDAO pessoaFisicaDAO;
    @Inject
    private PessoaJuridicaDAO pessoaJuridicaDAO;
    @Inject
    private MeioContatoManager meioContatoManager;
    @Inject
    private TipoParteSearch tipoParteSearch;

    private List<NaturezaCategoriaFluxoItem> naturezaCategoriaFluxoItemList;
    private List<TipoParte> tipoParteList;
    
    private NaturezaCategoriaFluxoItem naturezaCategoriaFluxoItem;
    private Processo processo;
    private IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO;
    private List<IniciarProcessoParticipanteVO> participanteProcessoList;
    
    @PostConstruct
    private void init() {
        Localizacao localizacao = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        Papel papel = Authenticator.getPapelAtual();
        UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        createProcesso(localizacao, usuarioLogin);
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        participanteProcessoList = new ArrayList<>();
        naturezaCategoriaFluxoItemList = natCatFluxoLocalizacaoDAO.listByLocalizacaoAndPapel(localizacao, papel);
        tipoParteList = tipoParteSearch.findAll();
    }

    private void createProcesso(Localizacao localizacao, UsuarioLogin usuarioLogin) {
        processo = new Processo();
        processo.setLocalizacao(localizacao);
        processo.setUsuarioCadastro(usuarioLogin);
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setProcessoRoot(processo);
    }
    
    public void onSelectNaturezaCategoriaFluxoItem() {
        if (naturezaCategoriaFluxoItem != null) {
            processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxoItem.getNaturezaCategoriaFluxo());
        }
    }
    
    public void onChangeTipoPessoa() {
        TipoPessoaEnum tipoPessoa = iniciarProcessoParticipanteVO.getTipoPessoa();
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        iniciarProcessoParticipanteVO.setTipoPessoa(tipoPessoa);
    }
    
    public void onChangeParticipanteCpf() {
        PessoaFisica pessoaFisica = pessoaFisicaDAO.searchByCpf(iniciarProcessoParticipanteVO.getCodigo());
        if (pessoaFisica != null) {
            iniciarProcessoParticipanteVO.loadPessoaFisica(pessoaFisica);
            MeioContato meioContato = meioContatoManager.getMeioContatoByPessoaAndTipo(pessoaFisica, TipoMeioContatoEnum.EM);
            if (meioContato != null) {
                iniciarProcessoParticipanteVO.loadMeioContato(meioContato);
            }
        } else {
            iniciarProcessoParticipanteVO.limparDadosPessoaFisica();
        }
    }
    
    public void onChangeParticipanteCnpj() {
        PessoaJuridica pessoaJuridica = pessoaJuridicaDAO.searchByCnpj(iniciarProcessoParticipanteVO.getCodigo());
        if (pessoaJuridica != null) {
            iniciarProcessoParticipanteVO.loadPessoaJuridica(pessoaJuridica);
        } else {
            iniciarProcessoParticipanteVO.limparDadosPessoaJuridica();
        }
    }

    public void adicionarParticipante() {
        iniciarProcessoParticipanteVO.adicionar();
        participanteProcessoList.add(iniciarProcessoParticipanteVO);
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
    }
    
    public void removerParticipante(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO) {
        participanteProcessoList.remove(iniciarProcessoParticipanteVO);
    }
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void iniciarProcesso() {
        if (naturezaCategoriaFluxoItem == null) {
            throw new BusinessException("Selecione um Agrupamento de Fluxo, por favor!");
        }
        FacesMessages.instance().add("Iniciado com sucesso!");
    }
    
    public List<NaturezaCategoriaFluxoItem> getNaturezaCategoriaFluxoItemList() {
        return naturezaCategoriaFluxoItemList;
    }
    
    public List<TipoParte> getTipoParteList() {
        return tipoParteList;
    }

    public IniciarProcessoParticipanteVO getIniciarProcessoParticipanteVO() {
        return iniciarProcessoParticipanteVO;
    }

    public void setIniciarProcessoParticipanteVO(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO) {
        this.iniciarProcessoParticipanteVO = iniciarProcessoParticipanteVO;
    }

    public List<IniciarProcessoParticipanteVO> getParticipanteProcessoList() {
        return participanteProcessoList;
    }

    public NaturezaCategoriaFluxoItem getNaturezaCategoriaFluxoItem() {
        return naturezaCategoriaFluxoItem;
    }

    public void setNaturezaCategoriaFluxoItem(NaturezaCategoriaFluxoItem naturezaCategoriaFluxoItem) {
        this.naturezaCategoriaFluxoItem = naturezaCategoriaFluxoItem;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }
    
}
