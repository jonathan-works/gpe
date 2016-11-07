package br.com.infox.epp.processo.search;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.consulta.action.ConsultaController;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(ProcessoSearcher.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class ProcessoSearcher {

    public static final String NAME = "processoSearcher";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoSearcher.class);

    @In
    private ProcessoManager processoManager;
    @In
    private SigiloProcessoService sigiloProcessoService;

    /**
     * Método redireciona para visualização do processo escolhido no paginador
     * 
     * @param processo Processo a ser visualizado no paginador
     */
    public void visualizarProcesso(Processo processo) {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("idProcesso", processo.getIdProcesso());
        flash.put("idJbpm", processo.getIdJbpm());
    }
    
    /**
     * Método realiza busca de processos no sistema
     * 
     * Se retornar um processo, este é chamado na página
     * {@link #visualizarProcesso(Processo)}
     * 
     * @return TRUE se o resultado for um processo, FALSE do contrário
     */
    public boolean searchProcesso(String searchText) {
    	Processo processo = searchByNumero(searchText);
    	if (processo == null) {
    		processo = searchIdProcesso(searchText);
    	}
        if (processo != null && processo.getIdJbpm() != null && processo.getProcessoPai() == null
        		&& sigiloProcessoService.usuarioPossuiPermissao(Authenticator.getUsuarioLogado(), processoManager.find(processo.getIdProcesso()))) {
            visualizarProcesso(processo);
            return true;
        }
        return false;
    }

    /**
     * Busca o processo pelo seu id
     * 
     * @return Processo cuja id seja igual o valor buscado, ou null
     */
    private Processo searchIdProcesso(String searchText) {
        int prc = -1;
        try {
            prc = Integer.parseInt(searchText);
        } catch (NumberFormatException e) {
            LOG.debug(e.getMessage(), e);
        }
        Processo processo = processoManager.find(prc);
        if (processo == null || processo.getNumeroProcesso() != null) {
        	return null;
        } else {
        	return processo;
        }
    }

	public Processo searchByNumero(String searchText) {
		Processo processo = processoManager.getProcessoByNumero(searchText);
    	if (processo != null) {
    		return processo;
    	} else {
    		return null;
    	}
	}

    public String getNumeroProcesso(int idProcesso) {
        return processoManager.getNumeroProcesso(idProcesso);
    }

}
