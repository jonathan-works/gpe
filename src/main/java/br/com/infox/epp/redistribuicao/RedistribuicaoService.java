package br.com.infox.epp.redistribuicao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.joda.time.DateTime;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.fluxo.definicaosinais.DefinicaoSinais;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.EppMetadadoProcessoService;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.ibpm.sinal.SignalService;

@Stateless
public class RedistribuicaoService extends PersistenceController {

    @Inject
    private SignalService signalService;
    @Inject
    EppMetadadoProcessoService eppMetadadoProcessoService;

    public void redistribuir(Processo processo, UnidadeDecisoraMonocratica udm, PessoaFisica relator,
            TipoRedistribuicao tipoRedistribuicao, String motivo) {
        List<Processo> lista = new ArrayList<Processo>();
        lista.add(processo);
        redistribuir(lista, udm, relator, tipoRedistribuicao, motivo);
    }

    public void redistribuir(List<Processo> processos, UnidadeDecisoraMonocratica udm, PessoaFisica relator,
            TipoRedistribuicao tipoRedistribuicao, String motivo) {

        for (Processo processo : processos) {
            MetadadoProcesso metadadoRelator = processo.getMetadado(EppMetadadoProvider.RELATOR);
            MetadadoProcesso metadadoUdm = processo.getMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA);
            if (metadadoRelator == null) {
                throw new ValidationException("Não é possível redistribuir um processo sem relator");
            }
            if (metadadoUdm == null) {
                throw new ValidationException(
                        "Não é possível redistribuir um processo sem Unidade Decisora Monocrática");
            }
            PessoaFisica relatorAtual = metadadoRelator.getValue();
            UnidadeDecisoraMonocratica udmAnterior = metadadoUdm.getValue();

            Redistribuicao redistribuicao = new Redistribuicao();
            redistribuicao.setProcesso(processo);
            redistribuicao.setRelatorAnterior(relatorAtual);
            redistribuicao.setUdmAnterior(udmAnterior);
            redistribuicao.setRelator(relator);
            redistribuicao.setUdm(udm);
            redistribuicao.setTipo(tipoRedistribuicao);
            redistribuicao.setMotivo(motivo);
            redistribuicao.setData(DateTime.now().toDate());
            getEntityManager().persist(redistribuicao);

            eppMetadadoProcessoService.setRelator(processo, relator);
            eppMetadadoProcessoService.setUnidadeDecisoraMonocratica(processo, udm);

            signalService.dispatch(processo.getIdProcesso(), DefinicaoSinais.REDISTRIBUICAO.getCodigo());
        }

        getEntityManager().flush();
    }

}
