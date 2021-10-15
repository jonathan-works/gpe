package br.com.infox.epp.processo.list;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BamReportSearch extends PersistenceController {

    @SuppressWarnings("unchecked")
    public List<BamReportVO> getResultReport(Integer idFluxo, Date dataInicio, Date dataFim, SituacaoPrazoEnum situacaoPrazoEnum) {
        StringBuilder querySB = new StringBuilder();
        querySB.append("SELECT p.nr_processo, t.ds_tarefa, f.ds_fluxo, l.ds_localizacao, ul.nm_usuario, pt.dt_inicio, pt.dt_fim ");
        querySB.append(" from tb_processo_tarefa pt ");
        querySB.append(" inner join tb_processo p on (p.id_processo = pt.id_processo) ");
        querySB.append(" inner join tb_tarefa t on (t.id_tarefa = pt.id_tarefa) ");
        querySB.append(" inner join tb_fluxo f on (f.id_fluxo = t.id_fluxo) ");
        querySB.append(" left join tb_usuario_taskinstance uti on (uti.id_taskinstance = pt.id_task_instance) ");
        querySB.append(" left join tb_usuario_login ul on (ul.id_usuario_login = uti.id_usuario_login) ");
        querySB.append(" left join tb_localizacao l on (l.id_localizacao = uti.id_localizacao) ");
        querySB.append(" where p.dt_fim is null ");

        if (idFluxo != null && idFluxo != 0) {
            querySB.append(String.format(" and f.id_fluxo = %s ", idFluxo));
        }
        if (situacaoPrazoEnum != null) {
            querySB.append(String.format(" and p.st_prazo = %s ", idFluxo));
        }
        if (dataInicio != null && dataFim != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDataInicio = dateFormat.format(dataInicio);
            String strDataFim = dateFormat.format(dataFim);
            querySB.append(String.format(" and (pt.dt_inicio between '%s' and '%s') ", strDataInicio, strDataFim));
        }
        querySB.append(" order by p.id_processo, pt.dt_inicio asc, pt.dt_fim asc ");

        Query createNativeQuery = getEntityManager().createNativeQuery(querySB.toString());
        List<Object[]> resultList = createNativeQuery.getResultList();
        List<BamReportVO> listBamReportVO = new ArrayList<>();
        for (Object[] row : resultList) {
            BamReportVO bamReportVO = new BamReportVO((String)row[0], (String)row[1], (String)row[2], (String)row[3], (String)row[4], (Date)row[5], (Date)row[6]);
            listBamReportVO.add(bamReportVO);
        }

        return listBamReportVO;

    }

}
