package br.com.infox.epp.processo.search;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;

@Name(ProcessoSearcher.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoSearcher {
    
    public static final String NAME = "processoSearcher";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoSearcher.class);
    
    @In ProcessoManager processoManager;
    @In SigiloProcessoService sigiloProcessoService;
    
    /**
     * Método redireciona para visualização do processo escolhido no paginador
     * 
     * @param processo Processo a ser visualizado no paginador
     */
    public void visualizarProcesso(Processo processo) {
        Redirect.instance().setConversationPropagationEnabled(false);
        Redirect.instance().setViewId("/Processo/Consulta/list.xhtml");
        Redirect.instance().setParameter("id", processo.getIdProcesso());
        Redirect.instance().setParameter("idJbpm", processo.getIdJbpm());
        Redirect.instance().execute();
    }
    
    /**
     * Método realiza busca de processos no sistema
     * 
     * Se retornar um processo, este é chamado na
     * página {@link #visualizarProcesso(Processo)}
     * 
     * @return TRUE se o resultado for um processo, FALSE do contrário
     */
    public boolean searchProcesso(String searchText) {
        Processo processo = searchIdProcesso(searchText);
        if (processo != null && sigiloProcessoService.usuarioPossuiPermissao(Authenticator.getUsuarioLogado(), (ProcessoEpa) processo)) {
        	visualizarProcesso(processo);
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
        return processoManager.find(prc);
    }
    
    public String getNumeroProcesso(int idProcesso) {
        return processoManager.getNumeroProcesso(idProcesso);
    }

}
