package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.tipoParte.TipoParteSearch;

public class TipoResponsavelEntregaController implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @Inject 
    private TipoParteSearch tipoParteSearch;
    
    private TipoParte tipoResponsavel;
    private Boolean obrigatorio;
    private TipoResponsavelEntrega tipoResponsavelEntrega;
    private List<TipoResponsavelEntrega> tiposResponsaveis;

    public void clear() {
        tiposResponsaveis = new ArrayList<>();
        tipoResponsavel=null;
        obrigatorio=null;
        tipoResponsavelEntrega=null;
    }

    public List<TipoResponsavelEntrega> getTiposResponsaveis() {
        return tiposResponsaveis;
    }

    public void setTiposResponsaveis(List<TipoResponsavelEntrega> tiposResponsaveis) {
        this.tiposResponsaveis = tiposResponsaveis;
    }

    public List<TipoParte> completeTipoResponsavel(String pattern) {
        List<TipoParte> result = tipoParteSearch.findTipoParteWithDescricaoLike(pattern);
        for (TipoResponsavelEntrega tipoResponsavelEntrega : getTiposResponsaveis()) {
            result.remove(tipoResponsavelEntrega.getTipoParte());
        }
        return result;
    }

    public TipoParte getTipoResponsavel() {
        return tipoResponsavel;
    }

    public void setTipoResponsavel(TipoParte tipoResponsavel) {
        this.tipoResponsavel = tipoResponsavel;
    }

    public TipoResponsavelEntrega getTipoResponsavelEntrega() {
        return tipoResponsavelEntrega;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean tipoResponsavelObrigatorio) {
        this.obrigatorio = tipoResponsavelObrigatorio;
    }

    public boolean emEdicao(TipoResponsavelEntrega tipoResponsavelEntrega){
        return Objects.equals(this.tipoResponsavelEntrega, tipoResponsavelEntrega);
    }
    
    @ExceptionHandled
    public void adicionar() {
        if (getTipoResponsavel() == null)
            return;
        TipoResponsavelEntrega tipoResponsavelEntrega = new TipoResponsavelEntrega();
        tipoResponsavelEntrega.setObrigatorio(Boolean.TRUE.equals(getObrigatorio()));
        tipoResponsavelEntrega.setTipoParte(getTipoResponsavel());
        getTiposResponsaveis().add(tipoResponsavelEntrega);
        setObrigatorio(null);
        setTipoResponsavel(null);
    }

    @ExceptionHandled
    public void remover(TipoResponsavelEntrega tipoResponsavelEntrega) {
        getTiposResponsaveis().remove(tipoResponsavelEntrega);
    }

    @ExceptionHandled
    public void editar(TipoResponsavelEntrega tipoResponsavelEntrega) {
        this.tipoResponsavelEntrega = tipoResponsavelEntrega;
    }
    
    @ExceptionHandled
    public void finalizarEdicao(){
        this.tipoResponsavelEntrega = null;
    }
}
