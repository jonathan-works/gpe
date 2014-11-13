package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.ParticipanteProcessoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.seam.util.ComponentUtil;

@Name(ParticipantesController.NAME)
public class ParticipantesController extends AbstractParticipantesController {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "participantesController";
    
    @In
    private TipoParteManager tipoParteManager;

    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private List<TipoParte> tipoPartes;
    private ParticipanteProcessoTreeHandler tree = ComponentUtil.getComponent(ParticipanteProcessoTreeHandler.NAME);
    
    private void createProcessoEpa() {
    	if (getProcesso() != null)  {
    		return;
    	}
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        Processo processo = new Processo();
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setUsuarioCadastro(usuarioLogado);
        processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
        processo.setLocalizacao(localizacao);
        processo.setNumeroProcesso("");
        setProcesso(processo);
    	try {
			processoManager.persist(getProcesso());
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);
		}
	}
    
    @Override
    protected void clearParticipanteProcesso() {
    	super.clearParticipanteProcesso();
    	tree.clearTree();
    }

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}

	public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
		if (naturezaCategoriaFluxo != null ){
			createProcessoEpa();
			if (!podeAdicionarPartesFisicas() && podeAdicionarPartesJuridicas()) {
				setTipoPessoa(TipoPessoaEnum.J);
			}
		}
		clearParticipanteProcesso();
	}
	
	public List<ParticipanteProcesso> getParticipantesAtivos(){
    	return getPartesAtivas(getProcesso().getParticipantes());
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

	@SuppressWarnings("unchecked")
	public List<ParticipanteProcesso> getParticipantesProcesso() {
		return getProcesso() != null ? getProcesso().getParticipantes() : Collections.EMPTY_LIST;
	}

	public List<TipoParte> getTipoPartes() {
    	if (tipoPartes == null){
    		tipoPartes = tipoParteManager.findAll();
    	}
		return tipoPartes;
	}

    public void removeParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
    	try {
    		getProcesso().getParticipantes().remove(participanteProcesso);
			participanteProcessoManager.remove(participanteProcesso);
	    	tree.clearTree();
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);		
		}
    }

    @Override
    public boolean podeAdicionarPartesFisicas() {
        return hasPartes()
                && !apenasPessoaJuridica()
                && (getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesFisicas() == 0 
                		|| filtrar(getParticipantesProcesso(), TipoPessoaEnum.F).size() < getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return hasPartes()
                && !apenasPessoaFisica()
                && (getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesJuridicas() == 0 
                		|| filtrar(getParticipantesProcesso(), TipoPessoaEnum.J).size() < getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesJuridicas());
    }
    
    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNaturezaCategoriaFluxo().getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNaturezaCategoriaFluxo().getNatureza().getTipoPartes());
    }
    
    public boolean podeRemoverParticipante(ParticipanteProcesso participanteProcesso) {
    	for (ParticipanteProcesso participante : getParticipantesProcesso()){
    		if (participanteProcesso.equals(participante.getParticipantePai())){
    			return false;
    		}
    	}
    	return true;
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
    
    public boolean podeIniciarProcesso(){
    	return getParticipantesProcesso() != null && getNaturezaCategoriaFluxo() != null
    			&& (getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesFisicas() == null || filtrar(getParticipantesProcesso(), TipoPessoaEnum.F).size() > 0) 
    			&& (getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesJuridicas() == null || filtrar(getParticipantesProcesso(), TipoPessoaEnum.J).size() > 0);
    }

    private boolean hasPartes() {
        return getNaturezaCategoriaFluxo() != null 
        		&& getNaturezaCategoriaFluxo().getNatureza() != null 
        		&& getNaturezaCategoriaFluxo().getNatureza().getHasPartes();
    }

}
