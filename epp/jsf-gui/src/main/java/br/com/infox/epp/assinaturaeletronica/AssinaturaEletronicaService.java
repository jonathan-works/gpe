package br.com.infox.epp.assinaturaeletronica;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.pessoaFisica.PessoaFisicaSearch;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AssinaturaEletronicaService extends PersistenceController {

    @Inject
    private AssinaturaEletronicaSearch assinaturaEletronicaSearch;

    @Inject
    private AssinaturaEletronicaBinSearch assinaturaEletronicaBinSearch;

    @Inject
    @GenericDao
    private Dao<AssinaturaEletronica, Long> assinaturaEletronicaDao;

    @Inject
    private PessoaFisicaSearch pessoaFisicaSearch;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AssinaturaEletronicaDTO salvar(AssinaturaEletronicaDTO assinaturaEletronicaDTO) {

        assinaturaEletronicaDTO.setDataInclusao(new Date());
        AssinaturaEletronica assinaturaEletronica = fromDTO(assinaturaEletronicaDTO);

        try {
            assinaturaEletronica = assinaturaEletronicaDao.persist(assinaturaEletronica);
            assinaturaEletronicaDTO.setId(assinaturaEletronica.getId());

            AssinaturaEletronicaBin assinaturaEletronicaBin = new AssinaturaEletronicaBin();
            assinaturaEletronicaBin.setId(assinaturaEletronicaDTO.getId());
            assinaturaEletronicaBin.setImagem(assinaturaEletronicaDTO.getImagem());
            getEntityManagerBin().persist(assinaturaEletronicaBin);
            getEntityManagerBin().flush();

        } catch (DAOException e) {
            throw new BusinessRollbackException(InfoxMessages.getInstance().get("assinaturaEletronica.salvar.erro"), e);
        }

        return assinaturaEletronicaDTO;
    }

    private AssinaturaEletronica fromDTO(AssinaturaEletronicaDTO assinaturaEletronicaDTO) {
        if(assinaturaEletronicaDTO.getDataInclusao() == null) {
            assinaturaEletronicaDTO.setDataInclusao(new Date());
        }
        AssinaturaEletronica assinaturaEletronica = new AssinaturaEletronica();
        assinaturaEletronica.setNomeArquivo(assinaturaEletronicaDTO.getNomeArquivo());
        assinaturaEletronica.setUuid(assinaturaEletronicaDTO.getUuid());
        assinaturaEletronica.setContentType(assinaturaEletronicaDTO.getContentType());
        assinaturaEletronica.setExtensao(assinaturaEletronicaDTO.getExtensao());
        if(assinaturaEletronicaDTO.getIdPessoa() != null) {
            assinaturaEletronica.setPessoaFisica(pessoaFisicaSearch.find(assinaturaEletronicaDTO.getIdPessoa()));
        }
        assinaturaEletronica.setDataInclusao(assinaturaEletronicaDTO.getDataInclusao());
        return assinaturaEletronica;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(AssinaturaEletronicaDTO assinaturaEletronicaDTO) {
        AssinaturaEletronica assinaturaEletronica = assinaturaEletronicaSearch.getAssinaturaEletronicaByIdPessoaFisica(assinaturaEletronicaDTO.getIdPessoa());
        remove(assinaturaEletronica);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(AssinaturaEletronica assinaturaEletronica) {
        if(assinaturaEletronica != null) {
            AssinaturaEletronicaBin assinaturaEletronicaBin = assinaturaEletronicaBinSearch.getAssinaturaEletronicaBinById(assinaturaEletronica.getId());
            if(assinaturaEletronicaBin != null) {
                getEntityManagerBin().remove(assinaturaEletronicaBin);
                getEntityManagerBin().flush();
            }
            assinaturaEletronicaDao.remove(assinaturaEletronica);
            assinaturaEletronicaDao.flush();
        }
    }

}
