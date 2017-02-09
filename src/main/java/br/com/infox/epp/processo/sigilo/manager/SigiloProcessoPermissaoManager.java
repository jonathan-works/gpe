package br.com.infox.epp.processo.sigilo.manager;

import java.util.List;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.dao.SigiloProcessoPermissaoDAO;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao_;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso_;

@AutoCreate
@Name(SigiloProcessoPermissaoManager.NAME)
public class SigiloProcessoPermissaoManager extends Manager<SigiloProcessoPermissaoDAO, SigiloProcessoPermissao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoPermissaoManager";

    public boolean usuarioPossuiPermissao(UsuarioLogin usuario,
            SigiloProcesso sigiloProcesso) {
        return getDao().usuarioPossuiPermissao(usuario, sigiloProcesso);
    }

    public void inativarPermissoes(SigiloProcesso sigiloProcesso) throws DAOException {
        getDao().inativarPermissoes(sigiloProcesso);
    }

    public List<SigiloProcessoPermissao> getPermissoes(SigiloProcesso sigiloProcesso) {
        return getDao().getPermissoes(sigiloProcesso);
    }

    public void gravarPermissoes(List<SigiloProcessoPermissao> permissoes,
            SigiloProcesso sigiloProcesso) throws DAOException {
        inativarPermissoes(sigiloProcesso);
        for (SigiloProcessoPermissao permissao : permissoes) {
            permissao.setSigiloProcesso(sigiloProcesso);
            persist(permissao);
        }
    }

    public static final String getPermissaoConditionFragment() {
        StringBuilder sb = new StringBuilder("(not exists (select 1 from SigiloProcesso sp where sp.processo.idProcesso = o.idProcesso and sp.ativo = true and sp.sigiloso = true) ");
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        if (usuarioLogado != null) {
            sb.append("or exists (select 1 from SigiloProcessoPermissao spp inner join spp.sigiloProcesso sp where spp.usuario.idUsuarioLogin = " + usuarioLogado.getIdUsuarioLogin());
            sb.append(" and spp.ativo = true and sp.processo = o and sp.ativo = true and sp.sigiloso = true))");
        } else {
            sb.append("or 1 = 0)");
        }
        return sb.toString();
    }
    
    public static final Predicate getPermissaoConditionPredicate(CriteriaBuilder cb, Root<Processo> processo, AbstractQuery<?> cq) {
        Subquery<Integer> existeSigilo = cq.subquery(Integer.class);
        Root<SigiloProcesso> sp = existeSigilo.from(SigiloProcesso.class);
        existeSigilo.where(cb.equal(sp.get(SigiloProcesso_.processo), processo),
                cb.isTrue(sp.get(SigiloProcesso_.ativo)),
                cb.isTrue(sp.get(SigiloProcesso_.sigiloso)));
        existeSigilo.select(cb.literal(1));
        Predicate notExisteSigilo = cb.exists(existeSigilo).not();

        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        if (usuarioLogado != null) {
            Subquery<Integer> temPermissao = cq.subquery(Integer.class);
            Root<SigiloProcessoPermissao> spp = temPermissao.from(SigiloProcessoPermissao.class);
            Join<SigiloProcessoPermissao, SigiloProcesso> joinSigilo = spp.join(SigiloProcessoPermissao_.sigiloProcesso);
            temPermissao.where(cb.equal(spp.get(SigiloProcessoPermissao_.usuario), usuarioLogado),
                    cb.isTrue(spp.get(SigiloProcessoPermissao_.ativo)),
                    cb.equal(joinSigilo.get(SigiloProcesso_.processo), processo),
                    cb.isTrue(joinSigilo.get(SigiloProcesso_.ativo)));
            temPermissao.select(cb.literal(1));
            return cb.or(notExisteSigilo, cb.exists(temPermissao));
        } else {
            return notExisteSigilo;
        }
    }
}
