package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "movimentacoes")
class MovimentacaoGroup {

    private Date start;
    private Date end;
    private Integer idLocalizacao;
    private String dsLocalizacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    public void add(Movimentacao movimentacao) {
        movimentacoes.add(movimentacao);
        start = movimentacoes.stream().map(Movimentacao::getCreate).filter(Objects::nonNull)
                .min(Comparator.comparing(Function.identity())).orElse(null);
        if (movimentacoes.stream().map(Movimentacao::getEnd).anyMatch(Objects::isNull)) {
            end = new Date();
        } else {
            end = movimentacoes.stream().map(Movimentacao::getEnd).filter(Objects::nonNull)
                    .max(Comparator.comparing(Function.identity())).orElse(null);
        }
        idLocalizacao = movimentacao.getIdLocalizacao();
        dsLocalizacao = movimentacao.getDsLocalizacao();
    }
}