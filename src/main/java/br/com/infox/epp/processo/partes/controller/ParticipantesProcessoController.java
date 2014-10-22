package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

import br.com.infox.epp.access.component.tree.ParticipanteProcessoTreeHandler;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.seam.util.ComponentUtil;

@Name(ParticipantesProcessoController.NAME)
public class ParticipantesProcessoController extends AbstractParticipantesController {

	private static final long serialVersionUID = 1L;
	private static final int QUANTIDADE_INFINITA_PARTES = 0;
    private static final int QUANTIDADE_MINIMA_PARTES = 1;
    public static final String NAME = "participantesProcessoController";

    @In
    private TipoParteManager tipoParteManager;
    
    private List<TipoParte> tipoPartes;
    private ParticipanteProcessoTreeHandler tree = ComponentUtil.getComponent(ParticipanteProcessoTreeHandler.NAME);
    
    @Override
    protected void clearParticipanteProcesso() {
    	super.clearParticipanteProcesso();
    	tree.clearTree();
    }
    
    @Override
    public void setProcessoEpa(ProcessoEpa processoEpa) {
    	super.setProcessoEpa(processoEpa);
    	clearParticipanteProcesso();
    	if (!podeAdicionarPartesFisicas() && podeAdicionarPartesJuridicas()) {
			setTipoPessoa(TipoPessoaEnum.J);
		}
    }
    
    private Natureza getNatureza() {
        return getProcessoEpa().getNaturezaCategoriaFluxo().getNatureza();
    }
    
    public List<ParticipanteProcesso> getParticipantes() {
    	return getProcessoEpa().getParticipantes();
    }
    
    public List<ParticipanteProcesso> getParticipantesAtivos() {
    	return getPartesAtivas(getProcessoEpa().getParticipantes());
    }

    private List<ParticipanteProcesso> filtrar(List<ParticipanteProcesso> participantes, 
    		TipoPessoaEnum tipoPessoa) {
        List<ParticipanteProcesso> filtrado = new ArrayList<>();
        for (ParticipanteProcesso participante : participantes) {
            if (tipoPessoa.equals(participante.getPessoa().getTipoPessoa())) {
                filtrado.add(participante);
            }
        }
        return filtrado;
    }
    
    private List<ParticipanteProcesso> getPartesAtivas(List<ParticipanteProcesso> participantes) {
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
        return Identity.instance().hasPermission(RECURSO_EXCLUIR, "access")
        		&& getPartesAtivas(filtrar(getProcessoEpa().getParticipantes(), TipoPessoaEnum.F)).size() > QUANTIDADE_MINIMA_PARTES;
    }

    public boolean podeInativarPartesJuridicas() {
        return Identity.instance().hasPermission(RECURSO_EXCLUIR, "access")
        		&& getPartesAtivas(filtrar(getProcessoEpa().getParticipantes(), TipoPessoaEnum.J)).size() > QUANTIDADE_MINIMA_PARTES;
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
        return getNatureza().getHasPartes()
                && !apenasPessoaJuridica()
                && Identity.instance().hasPermission(RECURSO_ADICIONAR, "access")
                && (getNatureza().getNumeroPartesFisicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(getProcessoEpa().getParticipantes(), TipoPessoaEnum.F)).size() < getNatureza().getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return getNatureza().getHasPartes()
                && !apenasPessoaFisica()
                && Identity.instance().hasPermission(RECURSO_ADICIONAR, "access")
                && (getNatureza().getNumeroPartesJuridicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(getProcessoEpa().getParticipantes(), TipoPessoaEnum.J)).size() < getNatureza().getNumeroPartesJuridicas());
    }

    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNatureza().getTipoPartes());
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
