package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.access.component.tree.ParticipanteProcessoTreeHandler;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.seam.security.SecurityUtil;

@Named(ParticipantesProcessoController.NAME)
@ViewScoped
public class ParticipantesProcessoController extends AbstractParticipantesController {

	private static final long serialVersionUID = 1L;
	private static final int QUANTIDADE_INFINITA_PARTES = 0;
    private static final int QUANTIDADE_MINIMA_PARTES = 1;
    public static final String NAME = "participantesProcessoController";

    @Inject
    protected TipoParteManager tipoParteManager;
    @Inject
    protected SecurityUtil securityUtil;
    @Inject
    protected UsuarioLoginManager usuarioLoginManager;
    @Inject
    protected ParticipanteProcessoTreeHandler participanteProcessoTree;
    
    protected List<TipoParte> tipoPartes;
        
    @Override
    protected void clearParticipanteProcesso() {
    	super.clearParticipanteProcesso(); 
    	participanteProcessoTree.clearTree();
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
    
    @Override
    public void setProcesso(Processo processo) {
    	super.setProcesso(processo.getProcessoRoot());
    	clearParticipanteProcesso();
    	if (!podeAdicionarPartesFisicas() && podeAdicionarPartesJuridicas()) {
			setTipoPessoa(TipoPessoaEnum.J);
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

    protected List<ParticipanteProcesso> filtrar(List<ParticipanteProcesso> participantes, 
    		TipoPessoaEnum tipoPessoa) {
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
        return getNatureza() != null
                && getNatureza().getHasPartes()
                && !apenasPessoaJuridica()
                && securityUtil.checkPage(RECURSO_ADICIONAR)
                && (getNatureza().getNumeroPartesFisicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(getProcesso().getParticipantes(), TipoPessoaEnum.F)).size() < getNatureza().getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return getNatureza() != null &&
                getNatureza().getHasPartes()
                && !apenasPessoaFisica()
                && securityUtil.checkPage(RECURSO_ADICIONAR)
                && (getNatureza().getNumeroPartesJuridicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(getProcesso().getParticipantes(), TipoPessoaEnum.J)).size() < getNatureza().getNumeroPartesJuridicas());
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

}
