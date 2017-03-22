package br.com.infox.epp.redistribuicao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;

@Named
@ViewScoped
public class HistoricoRedistribuicaoView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private RedistribuicaoSearch redistribuicaoSearch;

    private Processo processo;
    private List<Redistribuicao> redistribuicoes;
    private String relatorAtualProcesso;

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
        setRedistribuicoes(null);
        this.relatorAtualProcesso = null;
    }

    public String getRelatorAtualProcesso() {
        if (StringUtil.isEmpty(relatorAtualProcesso) && getProcesso() != null) {
            MetadadoProcesso metadadoRelator = getProcesso().getMetadado(EppMetadadoProvider.RELATOR);
            if (metadadoRelator != null) {
                PessoaFisica relator = metadadoRelator.getValue();
                this.relatorAtualProcesso = "O processo atual não passou por nenhuma redistribuição. O relator atual do processo é "
                        + relator.getNome() + ".";
            } else {
                this.relatorAtualProcesso = "O processo não possui relator.";
            }
        }
        return relatorAtualProcesso;
    }

    public List<Redistribuicao> getRedistribuicoes() {
        if (redistribuicoes == null && getProcesso() != null) {
            redistribuicoes = redistribuicaoSearch.getRedistribuicoesByIdProcesso(getProcesso().getIdProcesso());
        }
        return redistribuicoes;
    }

    public void setRedistribuicoes(List<Redistribuicao> redistribuicoes) {
        this.redistribuicoes = redistribuicoes;
    }

}
