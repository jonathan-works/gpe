package br.com.infox.epp.processo.list;

import javax.faces.model.SelectItem;

import br.com.infox.epp.fluxo.definicaovariavel.TipoPesquisaVariavelProcessoEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class FiltroVariavelProcessoVO extends SelectItem {
    private static final long serialVersionUID = 1L;

    private TipoPesquisaVariavelProcessoEnum tipoFiltro;

    public FiltroVariavelProcessoVO(Long value, String label, TipoPesquisaVariavelProcessoEnum tipoFiltro) {
        super(value, label);
        this.tipoFiltro = tipoFiltro;
    }

    public boolean isFiltroData() {
        return TipoPesquisaVariavelProcessoEnum.D.equals(getTipoFiltro());
    }

    public boolean isFiltroTexto() {
        return TipoPesquisaVariavelProcessoEnum.T.equals(getTipoFiltro());
    }

    public boolean isFiltroInteiro() {
        return TipoPesquisaVariavelProcessoEnum.I.equals(getTipoFiltro());
    }

    public boolean isFiltroDecimal() {
        return TipoPesquisaVariavelProcessoEnum.M.equals(getTipoFiltro());
    }

    public boolean isFiltroNumerico() {
        return TipoPesquisaVariavelProcessoEnum.N.equals(getTipoFiltro());
    }

    public boolean isFiltroBoleano() {
        return TipoPesquisaVariavelProcessoEnum.B.equals(getTipoFiltro());
    }

}
