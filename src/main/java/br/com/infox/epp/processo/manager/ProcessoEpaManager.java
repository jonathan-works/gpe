package br.com.infox.epp.processo.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.dao.ProcessoEpaDAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.util.time.DateRange;

@Name(ProcessoEpaManager.NAME)
@AutoCreate
public class ProcessoEpaManager extends Manager<ProcessoEpaDAO, Processo> {

    private static final int PORCENTAGEM = 100;

    private static final long serialVersionUID = 168832523707680478L;

    public static final String NAME = "processoEpaManager";

    public List<Processo> listAllNotEnded() {
        return getDao().listAllNotEnded();
    }

    public List<Processo> listNotEnded(Fluxo fluxo) {
        return getDao().listNotEnded(fluxo);
    }

    public Boolean podeInativarPartesDoProcesso(Processo processo) {
        return getDao().podeInativarPartes(getDao().getProcessoEpaByProcesso(processo));
    }
    
    @Deprecated
    public void updateTempoGastoProcessoEpa() throws DAOException {
        List<Processo> listAllNotEnded = listAllNotEnded();
        for (Processo processo : listAllNotEnded) {
            Map<String, Object> result = getDao().getTempoGasto(processo);

            if (result != null) {
                Fluxo f = processo.getNaturezaCategoriaFluxo().getFluxo();
                
                DateRange dateRange;
                final Date dataInicio = processo.getDataInicio();
                final Date dataFim = processo.getDataFim();
                if (dataFim != null){
                    dateRange = new DateRange(dataInicio, dataFim);
                } else {
                    dateRange = new DateRange(dataInicio, new Date());
                }
                
                processo.setTempoGasto(new Long(dateRange.get(DateRange.DAYS)).intValue());

//                if (f.getQtPrazo() != null && f.getQtPrazo() != 0) {
//                    processo.setPorcentagem((processo.getTempoGasto() * PORCENTAGEM)
//                            / f.getQtPrazo());
//                }
                if (processo.getPorcentagem() > PORCENTAGEM) {
                    processo.setSituacaoPrazo(SituacaoPrazoEnum.PAT);
                }
                getDao().update(processo);
            }
        }
    }

    public Item getItemDoProcesso(int idProcesso) {
        return getDao().getItemDoProcesso(idProcesso);
    }

    public boolean hasPartes(Processo processo) {
        return getDao().hasPartes(processo);
    }

    public boolean hasPartes(Long idJbpm) {
        return getDao().hasPartes(idJbpm);
    }

    public List<PessoaFisica> getPessoaFisicaList() {
        return getDao().getPessoaFisicaList();
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
        return getDao().getPessoaJuridicaList();
    }

    public Double getMediaTempoGasto(Fluxo fluxo, SituacaoPrazoEnum prazoEnum) {
        return getDao().getMediaTempoGasto(fluxo, prazoEnum);
    }

    public Processo getProcessoEpaByNumeroProcesso(
            final String numeroProcesso) {
    	Processo processo = null;
        if (numeroProcesso != null) {
            processo = getDao().getProcessoEpaByNumeroProcesso(numeroProcesso);
        }
        return processo;
    }
    
    public Processo persistProcessoComNumero(Processo processo) throws DAOException{
    	return getDao().persistProcessoComNumero(processo);
    }

    public void distribuirProcesso(Processo processo, PessoaFisica relator, UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) throws DAOException {
        distribuirProcesso(processo, relator, unidadeDecisoraMonocratica, null);
    }

    public void distribuirProcesso(Processo processo, UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) throws DAOException {
        distribuirProcesso(processo, null, unidadeDecisoraMonocratica, null);
    }

    @Deprecated
    public void distribuirProcesso(Processo processo, PessoaFisica relator, UnidadeDecisoraMonocratica unidadeDecisoraMonocratica, UnidadeDecisoraColegiada unidadeDecisoraColegiada) throws DAOException {
//    	processo.setDecisoraColegiada(unidadeDecisoraColegiada);
//    	processo.setDecisoraMonocratica(unidadeDecisoraMonocratica);
//    	processo.setRelator(relator);
        getDao().update(processo);
    }

    public void distribuirProcesso(Processo processo, UnidadeDecisoraColegiada unidadeDecisoraColegiada) throws DAOException {
        distribuirProcesso(processo, null, null, unidadeDecisoraColegiada);
    }

    public void distribuirProcesso(Processo processo) throws DAOException {
        distribuirProcesso(processo,null,null,null);
    }
}
