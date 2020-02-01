package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.Holder;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.component.tree.ParticipanteProcessoTreeHandler;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import br.com.infox.epp.loglab.search.EmpresaSearch;
import br.com.infox.epp.loglab.search.ServidorContribuinteSearch;
import br.com.infox.epp.loglab.vo.EmpresaVO;
import br.com.infox.epp.loglab.vo.PesquisaParticipanteVO;
import br.com.infox.epp.loglab.vo.ServidorContribuinteVO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.dao.ParticipanteProcessoService;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.security.SecurityUtil;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ParticipantesProcessoController extends AbstractParticipantesController {

    private static final long serialVersionUID = 1L;
    private static final int QUANTIDADE_MINIMA_PARTES = 1;

    @Inject
    private TipoParteManager tipoParteManager;
    @Inject
    private SecurityUtil securityUtil;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    @Inject
    private ParticipanteProcessoTreeHandler participanteProcessoTree;
    @Inject
    private ParticipanteProcessoService participanteProcessoService;
    @Inject
    private ServidorContribuinteSearch servidorContribuinteSearch;
    @Inject
    private EmpresaSearch empresaSearch;

    protected List<TipoParte> tipoPartes;

    @Getter
    @Setter
    private EmpresaVO empresaVO;

    @Getter
    private List<EmpresaVO> empresaList;

    @Getter
    @Setter
    private ServidorContribuinteVO servidorContribuinteVO;

    @Getter
    private List<ServidorContribuinteVO> servidorContribuinteList;

    @Getter
    @Setter
    private PesquisaParticipanteVO pesquisaParticipanteVO;

    @Override
    public void init(Holder<Processo> processoHolder) {
        super.init(processoHolder);
        clearParticipanteProcesso();
        if (!podeAdicionarPartesFisicas() && podeAdicionarPartesJuridicas()) {
            setTipoPessoa(TipoPessoaEnum.J);
        }
        limparDadosParticipante();
    }

    public void limparDadosParticipante() {
        empresaVO = null;
        empresaList = null;
        pesquisaParticipanteVO = new PesquisaParticipanteVO();
        pesquisaParticipanteVO.setTipoParticipante(TipoParticipanteEnum.CO);
        limparServidorContribuinte();
    }

    public void limparServidorContribuinte() {
        servidorContribuinteVO = null;
        servidorContribuinteList = null;
        limparCamposPesquisa();
    }

    public void limparCamposPesquisa() {
        pesquisaParticipanteVO.setCpf(null);
        pesquisaParticipanteVO.setMatricula(null);
        pesquisaParticipanteVO.setNomeCompleto(null);
        pesquisaParticipanteVO.setNomeFantasia(null);
        pesquisaParticipanteVO.setRazaoSocial(null);
        pesquisaParticipanteVO.setCnpj(null);
    }

    public void onChangeTipoPessoa() {
        limparDadosParticipante();
    }

    @Override
    protected void clearParticipanteProcesso() {
        super.clearParticipanteProcesso();
        participanteProcessoTree.clearTree();
        limparDadosParticipante();
    }

    @Override
    protected void initEmailParticipante() {
        UsuarioLogin usuario = usuarioLoginManager.getUsuarioLoginByPessoaFisica((PessoaFisica) getParticipanteProcesso().getPessoa());
        if (usuario != null) {
            email = usuario.getEmail();
        } else {
            super.initEmailParticipante();
        }
    }

    protected Natureza getNatureza() {
        return getProcesso().getNaturezaCategoriaFluxo().getNatureza();
    }

    public List<ParticipanteProcesso> getParticipantes() {
        if (getProcesso() != null) {
            return getProcesso().getParticipantes();
        }
        return null;
    }

    public List<ParticipanteProcesso> getParticipantesAtivos() {
        return getPartesAtivas(getProcesso().getParticipantes());
    }

    protected List<ParticipanteProcesso> filtrar(List<ParticipanteProcesso> participantes, TipoPessoaEnum tipoPessoa) {
        List<ParticipanteProcesso> filtrado = new ArrayList<>();
        for (ParticipanteProcesso participante : participantes) {
            if (tipoPessoa.equals(participante.getPessoa().getTipoPessoa())) {
                filtrado.add(participante);
            }
        }
        return filtrado;
    }

    protected List<ParticipanteProcesso> getPartesAtivas(List<ParticipanteProcesso> participantes) {
        List<ParticipanteProcesso> participantesAtivas = new ArrayList<>();
        for (ParticipanteProcesso participante : participantes) {
            if (participante.getAtivo()) {
                participantesAtivas.add(participante);
            }
        }
        return participantesAtivas;
    }

    public boolean podeInativarPartes(String tipoPessoa){
        if (TipoPessoaEnum.F.name().equals(tipoPessoa)) {
            return podeInativarPartesFisicas();
        } else if (TipoPessoaEnum.J.name().equals(tipoPessoa)) {
            return podeInativarPartesJuridicas();
        } else {
            return false;
        }
    }

    public boolean podeInativarPartesFisicas() {
        return securityUtil.checkPage(RECURSO_EXCLUIR)
                && getPartesAtivas(filtrar(getProcesso().getParticipantes(), TipoPessoaEnum.F)).size() > QUANTIDADE_MINIMA_PARTES;
    }

    public boolean podeInativarPartesJuridicas() {
        return securityUtil.checkPage(RECURSO_EXCLUIR)
                && getPartesAtivas(filtrar(getProcesso().getParticipantes(), TipoPessoaEnum.J)).size() > QUANTIDADE_MINIMA_PARTES;
    }

    public boolean podeVisualizarDetalhesParticipante(ParticipanteProcesso participanteProcesso) {
        return participanteProcesso.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.F) && securityUtil.checkPage(RECURSO_VISUALIZAR);
    }

    public List<TipoParte> getTipoPartes() {
        if (tipoPartes == null){
            tipoPartes = tipoParteManager.findAll();
        }
        return tipoPartes;
    }

    public boolean podeAdicionarPartes(String tipoPessoa){
        if (TipoPessoaEnum.F.name().equals(tipoPessoa)) {
            return podeAdicionarPartesFisicas();
        } else if (TipoPessoaEnum.J.name().equals(tipoPessoa)) {
            return podeAdicionarPartesJuridicas();
        } else {
            return false;
        }
    }

    @Override
    public boolean podeAdicionarPartesFisicas() {
        return securityUtil.checkPage(RECURSO_ADICIONAR) && participanteProcessoService.podeAdicionarPartesFisicas(getProcesso());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return securityUtil.checkPage(RECURSO_ADICIONAR) && participanteProcessoService.podeAdicionarPartesJuridicas(getProcesso());
    }

    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNatureza().getTipoPartes());
    }

    @Override
    public void includeParticipanteProcesso() {
        super.includeParticipanteProcesso();
        participanteProcessoTree.clearTree();
    }

    public ParticipanteProcesso getParticipantePai() {
        return getParticipanteProcesso().getParticipantePai();
    }

    public void setParticipantePai(ParticipanteProcesso participantePai) {
        if (participantePai != null && participantePai.getAtivo()){
            getParticipanteProcesso().setParticipantePai(participantePai);
        }
    }

    public void buscarServidorContribuinte() {
        if (StringUtil.isEmpty(pesquisaParticipanteVO.getCpf())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getNomeCompleto())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getMatricula())) {
            FacesMessages.instance().add("Por favor, preencha pelo menos um campo de busca.");
        } else {
            servidorContribuinteList = servidorContribuinteSearch.pesquisaServidorContribuinte(pesquisaParticipanteVO);

            if(servidorContribuinteList != null && servidorContribuinteList.size() > 0) {
                if(servidorContribuinteList.size() == 1) {
                    servidorContribuinteVO = servidorContribuinteList.get(0);
//                    onChangeParticipanteCpf();
                    servidorContribuinteList = null;
                } else {
                    JsfUtil.instance().execute("PF('servidorContribuinteDialog').show();");
                }
            } else if(pesquisaParticipanteVO.getTipoParticipante().equals(TipoParticipanteEnum.CO)) {
                servidorContribuinteVO = new ServidorContribuinteVO();
                servidorContribuinteVO.setTipoParticipante(pesquisaParticipanteVO.getTipoParticipante());
                servidorContribuinteVO.setCpf(pesquisaParticipanteVO.getCpf());
                FacesMessages.instance().add("Contribuinte não encontrado. Preencha os dados para adicionar um novo.");
            } else {
                FacesMessages.instance().add("Nenhum registro foi encontrado com os dados da busca.");
            }
        }
    }

    public void selecionarServidorContribuinte(ServidorContribuinteVO row) {
        servidorContribuinteVO = row;
//        onChangeParticipanteCpf();
        servidorContribuinteList = null;
        JsfUtil.instance().execute("PF('servidorContribuinteDialog').hide();");
    }

    public void buscarEmpresa() {
        if (StringUtil.isEmpty(pesquisaParticipanteVO.getCnpj())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getNomeFantasia())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getRazaoSocial())) {
            FacesMessages.instance().add("Por favor, preencha pelo menos um campo de busca.");
        } else {
            empresaList = empresaSearch.pesquisaEmpresaVO(pesquisaParticipanteVO);

            if(empresaList != null && empresaList.size() > 0) {
                if(empresaList.size() == 1) {
                    empresaVO = empresaList.get(0);
//                    onChangeParticipanteCnpj();
                    empresaList = null;
                } else {
                    JsfUtil.instance().execute("PF('empresaDialog').show();");
                }
            } else {
                empresaVO = new EmpresaVO();
                empresaVO.setCnpj(pesquisaParticipanteVO.getCnpj());
                FacesMessages.instance().add("Registro não encontrado. Preencha os dados para adicionar um novo.");
            }
        }
    }

    public void selecionarEmpresa(EmpresaVO row) {
        empresaVO = row;
//        onChangeParticipanteCnpj();
        empresaList = null;
        JsfUtil.instance().execute("PF('empresaDialog').hide();");
    }
}
