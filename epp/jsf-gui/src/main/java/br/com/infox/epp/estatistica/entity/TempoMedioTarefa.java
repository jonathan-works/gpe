package br.com.infox.epp.estatistica.entity;

import static java.text.MessageFormat.format;

import java.io.Serializable;

import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.type.PrazoEnum;

public class TempoMedioTarefa implements Serializable {
    private static final long serialVersionUID = 4365418442082711208L;
    private Tarefa tarefa;
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private long instancias;
    private double mediaTempoGasto;
    private Double tempoMedioProcesso;

    public TempoMedioTarefa() {
    }

    public TempoMedioTarefa(final Tarefa tarefa,
            final NaturezaCategoriaFluxo naturezaCategoriaFluxo,
            final Long instancias, final Double mediaTempoGasto) {
        this.tarefa = tarefa;
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
        this.instancias = instancias != null ? instancias : 0L;
        this.mediaTempoGasto = mediaTempoGasto != null ? mediaTempoGasto : 0.0;
    }

    public Double getTempoMedioProcesso() {
        return tempoMedioProcesso;
    }

    public void setTempoMedioProcesso(final Double tempoMedioProcesso) {
        this.tempoMedioProcesso = tempoMedioProcesso;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(final Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return naturezaCategoriaFluxo;
    }

    public void setNaturezaCategoriaFluxo(
            final NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
    }

    public long getInstancias() {
        return instancias;
    }

    public void setInstancias(final long instancias) {
        this.instancias = instancias;
    }

    public double getMediaTempoGasto() {
        return mediaTempoGasto;
    }

    public void setMediaTempoGasto(final double mediaTempoGasto) {
        this.mediaTempoGasto = mediaTempoGasto;
    }

    public String getTempoMedioFormatado() {
        final StringBuilder sb = new StringBuilder();
        final PrazoEnum tipoPrazo = tarefa.getTipoPrazo();
        if (PrazoEnum.H.equals(tipoPrazo)) {
            int hours = (int) (this.mediaTempoGasto / 60);
            int minutes = (int) (this.mediaTempoGasto % 60);
            if (hours < 10) {
                sb.append("0");
            }
            sb.append(hours).append(":");
            if (minutes < 10) {
                sb.append("0");
            }
            sb.append(minutes).append(" ").append(tipoPrazo.getLabel());
        } else if (PrazoEnum.D.equals(tipoPrazo)) {
            sb.append(format("{0,number,##} {1}", mediaTempoGasto, tipoPrazo.getLabel()));
        } else {
            sb.append(this.mediaTempoGasto);
        }
        return sb.toString();
    }

}
