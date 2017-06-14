package br.com.infox.epp.redistribuicao;

import java.util.concurrent.Future;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.jboss.seam.contexts.Lifecycle;
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

    

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Asynchronous
    public Future<Void> redistribuir(Processo processo, UnidadeDecisoraMonocratica udm, PessoaFisica relator,
            TipoRedistribuicao tipoRedistribuicao, String motivo) {
    	try {
    		Lifecycle.beginCall();
			
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

            getEntityManager().flush();
		} finally {
			Lifecycle.endCall();
		}

		return null;
    }

 
}
