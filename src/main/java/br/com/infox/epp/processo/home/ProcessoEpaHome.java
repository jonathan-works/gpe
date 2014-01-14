package br.com.infox.epp.processo.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.pessoa.manager.PessoaManager;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;
import br.com.infox.epp.processo.partes.manager.ParteProcessoManager;
import br.com.itx.component.AbstractHome;

@Name(ProcessoEpaHome.NAME)
@Scope(ScopeType.PAGE)
public class ProcessoEpaHome extends AbstractHome<ProcessoEpa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaHome";
	private static final Log LOG = Logging.getLog(ProcessoEpaHome.class);
	
	@In private PessoaManager pessoaManager;
	@In private ParteProcessoManager parteProcessoManager;
	
	public void incluirParteProcesso(Processo processo, String tipoPessoa){
		try {
			parteProcessoManager.incluir(processo, tipoPessoa);
			raiseEvent(ParteProcesso.ALTERACAO_ATIVIDADE_PARTE_PROCESSO);
		} catch (DAOException e){
			LOG.error(".incluirParteProcesso()", e);
			final FacesMessages messagesHandler = FacesMessages.instance();
            messagesHandler.clear();
			messagesHandler.add(Severity.ERROR, e.getLocalizedMessage());
		}
	}
    
    private TipoPessoaEnum convertTipoPessoaEnum(final String tipoPessoa) {
        if ("F".equals(tipoPessoa) || "f".equals(tipoPessoa)) {
            return TipoPessoaEnum.F;
        } else if ("J".equals(tipoPessoa) || "j".equals(tipoPessoa)) {
            return TipoPessoaEnum.J;
        }
        return null;
    }
    
	public void carregaPessoa(final String tipoPessoa, final String codigo){
		final TipoPessoaEnum tipoPessoaEnum = convertTipoPessoaEnum(tipoPessoa);
		if (tipoPessoaEnum != null) {
		    pessoaManager.carregaPessoa(tipoPessoaEnum, codigo);
		}
	}

}
