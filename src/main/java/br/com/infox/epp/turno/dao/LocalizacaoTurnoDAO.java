package br.com.infox.epp.turno.dao;

import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.COUNT_BY_HORA_INICIO_FIM;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.DELETE_TURNOS_ANTERIORES;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LIST_BY_HORA_INICIO_FIM;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LIST_BY_LOCALIZACAO;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_HORARIO;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.QUERY_PARAM_DATA;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.QUERY_PARAM_DIA_SEMANA;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.QUERY_PARAM_HORA_FIM;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.QUERY_PARAM_HORA_INICIO;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.QUERY_PARAM_IDPROCESSO;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.QUERY_PARAM_ID_TASK_INSTANCE;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.QUERY_PARAM_LOCALIZACAO;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.turno.type.DiaSemanaEnum;

@Stateless
@AutoCreate
@Name(LocalizacaoTurnoDAO.NAME)
public class LocalizacaoTurnoDAO extends DAO<LocalizacaoTurno> {

    private static final long serialVersionUID = 4917008814431859631L;
    public static final String NAME = "localizacaoTurnoDAO";

    public LocalizacaoTurno getTurnoTarefa(Integer idProcesso,
            Date dataAnterior, Date dataAtual, DiaSemanaEnum diaSemana) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_IDPROCESSO, idProcesso);
        parameters.put(QUERY_PARAM_HORA_INICIO, new Time(dataAnterior.getTime()));
        parameters.put(QUERY_PARAM_HORA_FIM, new Time(dataAtual.getTime()));
        parameters.put(QUERY_PARAM_DIA_SEMANA, diaSemana);

        parameters.put(QUERY_PARAM_DATA, dataAtual);
        return getNamedSingleResult(LOCALIZACAO_TURNO_BY_TAREFA_HORARIO, parameters);
    }

    public List<LocalizacaoTurno> getTurnosTarefa(final long idTaskInstance,
            final DiaSemanaEnum diaSemana, final Date dataAtual) {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_ID_TASK_INSTANCE, idTaskInstance);
        parameters.put(QUERY_PARAM_DIA_SEMANA, diaSemana);

        parameters.put(QUERY_PARAM_DATA, dataAtual);
        return getNamedResultList(LOCALIZACAO_TURNO_BY_TAREFA, parameters);
    }

    public Long countTurnoTarefaDia(ProcessoTarefa pt, Date data,
            DiaSemanaEnum diaSemana) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_ID_TASK_INSTANCE, pt.getTaskInstance());
        parameters.put(QUERY_PARAM_DIA_SEMANA, diaSemana);

        parameters.put(QUERY_PARAM_DATA, data);
        return getNamedSingleResult(COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA, parameters);
    }

    public List<LocalizacaoTurno> listByLocalizacao(Localizacao localizacao) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_LOCALIZACAO, localizacao);
        return getNamedResultList(LIST_BY_LOCALIZACAO, parameters);
    }

    public List<LocalizacaoTurno> listByHoraInicioFim(Localizacao l,
            Time horaInicio, Time horaFim) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_LOCALIZACAO, l);
        parameters.put(QUERY_PARAM_HORA_INICIO, horaInicio);
        parameters.put(QUERY_PARAM_HORA_FIM, horaFim);
        return getNamedResultList(LIST_BY_HORA_INICIO_FIM, parameters);
    }

    public Integer countByHoraInicioFim(Localizacao l, Time horaInicio,
            Time horaFim) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_LOCALIZACAO, l);
        parameters.put(QUERY_PARAM_HORA_INICIO, horaInicio);
        parameters.put(QUERY_PARAM_HORA_FIM, horaFim);
        return getNamedSingleResult(COUNT_BY_HORA_INICIO_FIM, parameters);
    }

    public void removerTurnosAnteriores(Localizacao localizacao) throws DAOException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_LOCALIZACAO, localizacao);
        executeNamedQueryUpdate(DELETE_TURNOS_ANTERIORES, parameters);
    }

}
