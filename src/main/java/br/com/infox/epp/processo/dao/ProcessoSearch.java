package br.com.infox.epp.processo.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoSearch extends PersistenceController {
    
    public List<Processo> getProcessosNaoIniciados(UsuarioLogin usuarioLogin) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Processo> cq = cb.createQuery(Processo.class);
        Root<Processo> processo = cq.from(Processo.class);
        Root<StatusProcesso> statusProcesso = cq.from(StatusProcesso.class);
        Join<Processo, MetadadoProcesso> metaddoProcesso = processo.join(Processo_.metadadoProcessoList, JoinType.INNER);
        cq.select(processo);
        cq.where(
            cb.equal(metaddoProcesso.get(MetadadoProcesso_.metadadoType), cb.literal(EppMetadadoProvider.STATUS_PROCESSO.getMetadadoType())),
            cb.equal(statusProcesso.get(StatusProcesso_.idStatusProcesso).as(String.class), metaddoProcesso.get(MetadadoProcesso_.valor)),
            cb.equal(statusProcesso.get(StatusProcesso_.nome), cb.literal(StatusProcesso.STATUS_NAO_INICIADO)),
            cb.equal(processo.get(Processo_.usuarioCadastro).get(UsuarioLogin_.idUsuarioLogin), cb.literal(usuarioLogin.getIdUsuarioLogin())),
            cb.isNull(processo.get(Processo_.idJbpm)),
            cb.isNull(processo.get(Processo_.dataFim))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    

}
