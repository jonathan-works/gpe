package br.com.infox.epp.unidadedecisora.crud;

import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.util.ObjectUtil;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;

@Name(UnidadeDecisoraMonocraticaCrudAction.NAME)
@ContextDependency
public class UnidadeDecisoraMonocraticaCrudAction extends AbstractCrudAction<UnidadeDecisoraMonocratica, UnidadeDecisoraMonocraticaManager>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaCrudAction";
	
	@Inject
	private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
	@Inject
	private UsuarioPerfilManager usuarioPerfilManager;
	
	private List<PessoaFisica> possiveisChefesGabinete;
	
    @Override
	public void newInstance() {
		super.newInstance();
		BeanManager.INSTANCE.getReference(LocalizacaoTreeHandler.class).clearTree();
		possiveisChefesGabinete = null;
	}
    
	@Override
	protected boolean isInstanceValid() {
		boolean existeLoc = unidadeDecisoraColegiadaManager.existeUnidadeColegiadaComLocalizacao(getInstance().getLocalizacao().getIdLocalizacao());
		if(existeLoc){
			FacesMessages.instance().clearGlobalMessages();
			FacesMessages.instance().add("#{infoxMessages['unidadeDecisoraColegiada.jaExisteLocalizacao']}");
		} else {
		    UnidadeDecisoraMonocratica udm = getManager().existeUnidadeMonocraticaComLocalizacao(getInstance().getLocalizacao().getIdLocalizacao());
		    existeLoc = isManaged() ? (!udm.equals(getInstance())) : (udm != null);
		    if (existeLoc){
                FacesMessages.instance().clearGlobalMessages();
                FacesMessages.instance().add("#{infoxMessages['unidadeDecisoraMonocratica.jaExisteLocalizacao']}");
            }
		}
		return super.isInstanceValid() && !existeLoc;
	}
	
	public Localizacao getLocalizacao() {
	    return getInstance().getLocalizacao();
	}
	
	public void setLocalizacao(Localizacao localizacao) {
	    if (localizacao == null || localizacao.getEstruturaFilho() != null) {
	        if (!ObjectUtil.equals(localizacao, getLocalizacao())) {
	            onChangeLocalizacao();
	        }
	        getInstance().setLocalizacao(localizacao);
	    }
	}

    private void onChangeLocalizacao() {
        possiveisChefesGabinete = null;
    }

    public List<PessoaFisica> getPossiveisChefesGabinete() {
        if (possiveisChefesGabinete == null && getInstance().getLocalizacao() != null) {
            possiveisChefesGabinete = usuarioPerfilManager.listByLocalizacaoAtivo(getLocalizacao());
        }
        return possiveisChefesGabinete;
    }
	
}
