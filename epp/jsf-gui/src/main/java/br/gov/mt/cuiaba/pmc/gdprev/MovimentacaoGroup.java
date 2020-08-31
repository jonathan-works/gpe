package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.ArrayList;
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
        
        //Verificação da movimentação com menor data de criação
        if(first != null) {
            if(first.getCreate().after(movimentacao.getCreate())) {
                //O parâmetro movimentação possui a data de criação menor a a variavel first
                first = movimentacao;
            }
        }else {
            //O parâmetro movimentação é o primeiro item da lista
            first = movimentacao;
        }

        //Verificação da movimentação com maior data final
        if(last != null) {
            if(last.getEnd() != null && movimentacao.getEnd() != null) {
                if(last.getEnd().before(movimentacao.getEnd())) {
                    //O parâmetro movimentação possui a data final maior que a a variavel last
                    last = movimentacao;
                }
            } else if(last.getEnd() == null && movimentacao.getEnd() == null) {
                if(last.getCreate().before(movimentacao.getCreate())) {
                    //O parâmetro movimentação e a variavel last não possuem data final, mas movimentação possui a data de criação maior
                    last = movimentacao;
                }
            } else if(movimentacao.getEnd() == null) {
                //O parâmetro movimentação é o único que não possui data final
                last = movimentacao;
            }
        }else {
            //O parâmetro movimentação é o primeiro item da lista
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