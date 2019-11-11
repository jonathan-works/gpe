package br.com.infox.epp.processo.documento.action;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaCompartilhamento;
import br.com.infox.epp.processo.documento.entity.PastaCompartilhamentoHistorico;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PastaCompartilhamentoService {

    @Inject
    private PastaCompartilhamentoSearch pastaCompartilhamentoSearch;

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PastaCompartilhamento adicionarCompartilhamento(Pasta pasta, Processo processo, UsuarioLogin usuario) {
        PastaCompartilhamento pc = pastaCompartilhamentoSearch.getByPastaProcesso(pasta, processo);
        if (pc == null) {
            pc = new PastaCompartilhamento();
            pc.setAtivo(true);
            pc.setPasta(pasta);
            pc.setProcessoAlvo(processo);
            getEntityManager().persist(pc);
        } else {
            pc.setAtivo(true);
            getEntityManager().merge(pc);
        }
        getEntityManager().persist(createHistorico(pc, true, usuario));
        getEntityManager().flush();
        return pc;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PastaCompartilhamento removerCompartilhamento(Pasta pasta, Processo processo, UsuarioLogin usuario) {
        PastaCompartilhamento pc = pastaCompartilhamentoSearch.getByPastaProcesso(pasta, processo);
        if (pc == null) {
            pc = new PastaCompartilhamento();
            pc.setAtivo(false);
            pc.setPasta(pasta);
            pc.setProcessoAlvo(processo);
            getEntityManager().persist(pc);
        } else {
            pc.setAtivo(false);
            getEntityManager().merge(pc);
        }
        getEntityManager().persist(createHistorico(pc, false, usuario));
        getEntityManager().flush();
        return pc;
    }

    private PastaCompartilhamentoHistorico createHistorico(PastaCompartilhamento pc, Boolean acaoAdicao, UsuarioLogin usuario) {
        PastaCompartilhamentoHistorico historico = new PastaCompartilhamentoHistorico();
        historico.setAcaoAdicao(acaoAdicao);
        historico.setDataAcao(new Date());
        historico.setPastaCompartilhamento(pc);
        historico.setUsuarioLogin(usuario);
        return historico;
    }
}
