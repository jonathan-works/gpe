package br.com.infox.epp.processo.documento.action;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoCompartilhamento;
import br.com.infox.epp.processo.documento.entity.DocumentoCompartilhamentoHistorico;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentoCompartilhamentoService {

    @Inject
    private DocumentoCompartilhamentoSearch documentoCompartilhamentoSearch;

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DocumentoCompartilhamento adicionarCompartilhamento(Documento documento, Processo processoAlvo, UsuarioLogin usuario) {
        DocumentoCompartilhamento dc = documentoCompartilhamentoSearch.getByDocumentoProcesso(documento, processoAlvo);
        if (dc == null) {
            dc = new DocumentoCompartilhamento();
            dc.setAtivo(true);
            dc.setDocumento(documento);
            dc.setProcessoAlvo(processoAlvo);
            getEntityManager().persist(dc);
        } else {
            dc.setAtivo(true);
            getEntityManager().merge(dc);
        }
        getEntityManager().persist(createHistorico(dc, true, usuario));
        getEntityManager().flush();
        return dc;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DocumentoCompartilhamento removerCompartilhamento(Documento documento, Processo processoAlvo, UsuarioLogin usuario) {
        DocumentoCompartilhamento dc = documentoCompartilhamentoSearch.getByDocumentoProcesso(documento, processoAlvo);
        if (dc == null) {
            dc = new DocumentoCompartilhamento();
            dc.setAtivo(false);
            dc.setDocumento(documento);
            dc.setProcessoAlvo(processoAlvo);
            getEntityManager().persist(dc);
        } else {
            dc.setAtivo(false);
            getEntityManager().merge(dc);
        }
        getEntityManager().persist(createHistorico(dc, false, usuario));
        getEntityManager().flush();
        return dc;
    }

    private DocumentoCompartilhamentoHistorico createHistorico(DocumentoCompartilhamento dc, boolean acaoAdicao, UsuarioLogin usuario) {
        DocumentoCompartilhamentoHistorico dch = new DocumentoCompartilhamentoHistorico();
        dch.setAcaoAdicao(acaoAdicao);
        dch.setDataAcao(new Date());
        dch.setDocumentoCompartilhamento(dc);
        dch.setUsuarioLogin(usuario);
        return dch;
    }
}
