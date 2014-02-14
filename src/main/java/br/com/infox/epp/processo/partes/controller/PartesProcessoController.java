package br.com.infox.epp.processo.partes.controller;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;
import br.com.infox.epp.processo.partes.manager.ParteProcessoManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Name(PartesProcessoController.NAME)
public class PartesProcessoController extends AbstractPartesController {

    public static final String NAME = "partesProcessoController";
    private static final LogProvider LOG = Logging.getLogProvider(PartesProcessoController.class);

    private ProcessoEpa processoEpa;
    
    @In private ActionMessagesService actionMessagesService;
    @In private ParteProcessoManager parteProcessoManager;
    @In private PessoaFisicaManager pessoaFisicaManager;
    @In private PessoaJuridicaManager pessoaJuridicaManager;
    @In private ProcessoEpaManager processoEpaManager;

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
        setPessoaFisica(new PessoaFisica());
        setPessoaJuridica(new PessoaJuridica());
    }

    private Natureza getNatureza() {
        return processoEpa.getNaturezaCategoriaFluxo().getNatureza();
    }
    
    public List<ParteProcesso> getPartes(){
        return processoEpa.getPartes();
    }

    @Override
    public void includePessoaFisica() {
        try {
            pessoaFisicaManager.persist(getPessoaFisica());
            parteProcessoManager.incluir(processoEpa, getPessoaFisica());
            processoEpaManager.refresh(processoEpa);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error("Não foi possível inserir a pessoa " + getPessoaFisica(), e);
        } finally {
            setPessoaFisica(new PessoaFisica());
        }
    }

    @Override
    public void includePessoaJuridica() {
        try {
            pessoaJuridicaManager.persist(getPessoaJuridica());
            parteProcessoManager.incluir(processoEpa, getPessoaJuridica());
            processoEpaManager.refresh(processoEpa);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error("Não foi possível inserir a pessoa " + getPessoaJuridica(), e);
        } finally {
            setPessoaJuridica(new PessoaJuridica());
        }
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
    public boolean podeAdicionarPartes() {
        return getNatureza().getHasPartes()
                && (getNatureza().getNumeroPartes() == 0 || countPartesAtivas() < getNatureza().getNumeroPartes());
    }
    
    private int countPartesAtivas(){
        int count = 0;
        for (ParteProcesso pp : processoEpa.getPartes()){
            if (pp.getAtivo()) {
                count++;
            }
        }
        return count;
    }

}
