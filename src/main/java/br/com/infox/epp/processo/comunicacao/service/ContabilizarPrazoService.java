package br.com.infox.epp.processo.comunicacao.service;

import java.util.Calendar;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.processo.comunicacao.Comunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;

@Name(ContabilizarPrazoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ContabilizarPrazoService {
    public static final String NAME = "contabilizarPrazoService";
    
    @In
    private CalendarioEventosManager calendarioEventosManager;
    @In
    private ComunicacaoManager comunicacaoManager;
    
    public void contabilizarPrazoCiencia(Comunicacao comunicacao) throws DAOException {
        TipoComunicacao tipoComunicacao = comunicacao.getModeloComunicacao().getTipoComunicacao();
        Integer qtdDias = tipoComunicacao.getQuantidadeDiasCiencia();
        Date hoje = new Date();
        Date prazo = getPrimeiroDiaUtil(hoje, qtdDias);
        comunicacaoManager.setDataFimPrazoCiencia(comunicacao, prazo);
    }
    
    public void contabilizarPrazoCumprimento(Comunicacao comunicacao) {
        Integer qtdDias = comunicacaoManager.getQtdDiasCumprimento(comunicacao);
        Date hoje = new Date();
        Date prazo = getPrimeiroDiaUtil(hoje, qtdDias);
        comunicacaoManager.setDataFimPrazoCumprimento(comunicacao, prazo);
    }
    
    /**
     * Retorna o primeiro dia útil de forma recursiva
     * @param dia Data base a considerar
     * @param qtdDias quantidade de dias 
     * @return
     */
    private Date getPrimeiroDiaUtil(Date dia, int qtdDias) {
        Date prazo = new Date();
        prazo.setTime(dia.getTime() + getDiaInMilis(qtdDias));
        if (isDiaUtil(prazo))
            return prazo;
        else 
            return getPrimeiroDiaUtil(prazo, 1);
    }
    
    /**
     * Transforma uma quantidade de dias em milisegundos.
     * @param dias
     * @return dias em milisegundos
     */
    private Integer getDiaInMilis(Integer dias) {
        if (dias == null) dias = 10;
        return dias * 24 * 60 * 60 * 1000;
    }

    private Boolean isDiaUtil(Date dia) {
        return !(isWeekend(dia) || hasEventAt(dia));
    }

    private Boolean isWeekend(Date dia) {
        Calendar c = Calendar.getInstance();
        c.setTime(dia);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
    
    private boolean hasEventAt(Date dia) {
        return calendarioEventosManager.getByDate(dia) != null;
    }
}
