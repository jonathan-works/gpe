package br.com.infox.epp.assinaturaeletronica;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AssinaturaEletronicaBinSearch extends PersistenceController {

    public AssinaturaEletronicaBin getAssinaturaEletronicaBinById(Long id) {
        final EntityManager emBin = getEntityManagerBin();
        CriteriaBuilder cb = emBin.getCriteriaBuilder();
        CriteriaQuery<AssinaturaEletronicaBin> query = cb.createQuery(AssinaturaEletronicaBin.class);
        Root<AssinaturaEletronicaBin> assEletronicaBin = query.from(AssinaturaEletronicaBin.class);

        query.select(assEletronicaBin);
        query.where(cb.equal(assEletronicaBin.get(AssinaturaEletronicaBin_.id), id));

        try {
            return emBin.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public byte[] getImagemByIdAssinaturaEletronicaBin(Long id) {
        final EntityManager emBin = getEntityManagerBin();
        CriteriaBuilder cb = emBin.getCriteriaBuilder();
        CriteriaQuery<byte[]> query = cb.createQuery(byte[].class);
        Root<AssinaturaEletronicaBin> assEletronicaBin = query.from(AssinaturaEletronicaBin.class);

        query.select(assEletronicaBin.get(AssinaturaEletronicaBin_.imagem));
        query.where(cb.equal(assEletronicaBin.get(AssinaturaEletronicaBin_.id), id));

        try {
            return emBin.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

}
