package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "movimentacoes")
public class MovimentacaoGroup {

    private Movimentacao first;
    private Movimentacao last;
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    public void add(Movimentacao movimentacao) {
        movimentacoes.add(movimentacao);
        
        if(first != null) {
            if(first.getCreate().after(movimentacao.getCreate())) {
                first = movimentacao;
            }
        }else {
            first = movimentacao;
        }
        
        if(last != null) {
            if(last.getEnd() != null && movimentacao.getEnd() != null) {
                if(last.getEnd().before(movimentacao.getEnd())) {
                    last = movimentacao;
                }
            } else if(last.getEnd() == null && movimentacao.getEnd() == null) {
                if(last.getCreate().before(movimentacao.getCreate())) {
                    last = movimentacao;
                }
            } else if(movimentacao.getEnd() == null) {
                last = movimentacao;
            }
        }else {
            last = movimentacao;
        }
    }

	public Date getEnd() {
		return Optional.ofNullable(last).map(Movimentacao::getEnd).orElseGet(Date::new);
	}

	public Integer getIdLocalizacao() {
		return Optional.ofNullable(first).map(Movimentacao::getIdLocalizacao).orElse(null);
	}

	public Date getStart() {
		return Optional.ofNullable(first).map(Movimentacao::getCreate).orElseGet(Date::new);
	}
}