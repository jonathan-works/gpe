package br.com.infox.epp.assinaturaeletronica;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.LocalizacaoAssinaturaEletronicaDocumentoEnum;
import br.com.infox.epp.documento.type.OrientacaoAssinaturaEletronicaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.download.Carimbo;
import br.com.infox.epp.processo.documento.download.CarimboAssinatura;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AssinaturaEletronicaSearch extends PersistenceController {

    @Inject
    private AssinaturaEletronicaBinSearch assinaturaEletronicaBinSearch;

	public List<Carimbo> criarCarimbosAssinaturaEletronica(DocumentoBin documentoBin) {
		List<Carimbo> assinaturas = new ArrayList<>();
		for (AssinaturaDocumento assinatura : documentoBin.getAssinaturas()) {
            assinaturas.add(gerarAssinaturaEletronica(documentoBin, assinatura));
        }
		return assinaturas;
	}

	private CarimboAssinatura gerarAssinaturaEletronica(DocumentoBin documentoBin, AssinaturaDocumento assinatura) {
		AssinaturaEletronica assinaturaEletronica = getAssinaturaEletronicaByIdPessoaFisica(assinatura.getPessoaFisica().getIdPessoa());
		byte[] imgAssinatura = null;
		if(assinaturaEletronica != null) {
		    imgAssinatura = assinaturaEletronicaBinSearch.getImagemByIdAssinaturaEletronicaBin(assinaturaEletronica.getId());
		}

		Optional<ClassificacaoDocumento> optClassificacao = Stream.of(documentoBin)
			.flatMap(docBin->docBin.getDocumentoList().stream())
			.map(Documento::getClassificacaoDocumento)
			.findFirst();
		LocalizacaoAssinaturaEletronicaDocumentoEnum localizacaoAssinatura = optClassificacao
				.map(ClassificacaoDocumento::getLocalizacaoAssinaturaEletronicaDocumentoEnum)
				.orElse(LocalizacaoAssinaturaEletronicaDocumentoEnum.ULTIMA_PAGINA);
		Integer paginaUnica = optClassificacao.map(ClassificacaoDocumento::getPaginaExibicaoAssinaturaEletronica).orElse(0);
		OrientacaoAssinaturaEletronicaDocumentoEnum orientacaoAssinaturaEletronicaDocumentoEnum = optClassificacao.map(ClassificacaoDocumento::getOrientacaoAssinaturaEletronicaDocumentoEnum).orElse(OrientacaoAssinaturaEletronicaDocumentoEnum.RODAPE_HORIZONTAL);
		int orientacao = Carimbo.RODAPE;
		if (OrientacaoAssinaturaEletronicaDocumentoEnum.LATERAL_VERTICAL.equals(orientacaoAssinaturaEletronicaDocumentoEnum)) {
			orientacao = Carimbo.LATERAL;
		}
		return new CarimboAssinatura(imgAssinatura, assinatura.getNomeUsuario(), assinatura.getPapel().getNome(), orientacao, localizacaoAssinatura, paginaUnica);
	}

    public AssinaturaEletronica getAssinaturaEletronicaById(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AssinaturaEletronica> query = cb.createQuery(AssinaturaEletronica.class);
        Root<AssinaturaEletronica> assEletronica = query.from(AssinaturaEletronica.class);

        query.select(assEletronica);
        query.where(cb.equal(assEletronica.get(AssinaturaEletronica_.id), id));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssinaturaEletronica getAssinaturaEletronicaByIdPessoaFisica(Integer idPessoaFisica) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AssinaturaEletronica> query = cb.createQuery(AssinaturaEletronica.class);
        Root<AssinaturaEletronica> assEletronica = query.from(AssinaturaEletronica.class);

        query.select(assEletronica);
        query.where(cb.equal(assEletronica.get(AssinaturaEletronica_.pessoaFisica).get(PessoaFisica_.idPessoa), idPessoaFisica));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }


    public AssinaturaEletronicaDTO getAssinaturaEletronicaDTOByIdPessoaFisica(Integer idPessoaFisica) {
        return getAssinaturaEletronicaDTOByIdPessoaFisica(idPessoaFisica, false);
    }

    public AssinaturaEletronicaDTO getAssinaturaEletronicaDTOByIdPessoaFisica(Integer idPessoaFisica, boolean trazerImagem) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AssinaturaEletronicaDTO> query = cb.createQuery(AssinaturaEletronicaDTO.class);
        Root<AssinaturaEletronica> assEletronica = query.from(AssinaturaEletronica.class);

        query.select(resolveSelect(cb, query, assEletronica));
        query.where(cb.equal(assEletronica.get(AssinaturaEletronica_.pessoaFisica).get(PessoaFisica_.idPessoa), idPessoaFisica));

        try {
            AssinaturaEletronicaDTO result = getEntityManager().createQuery(query).getSingleResult();
            if(trazerImagem) {
                final byte[] imagem = assinaturaEletronicaBinSearch.getImagemByIdAssinaturaEletronicaBin(result.getId());
                result.setImagem(imagem);
            }
            return result;
        } catch (NoResultException e) {
            return null;
        }

    }

    public AssinaturaEletronicaDTO getAssinaturaEletronicaDTOByUUID(UUID uuid) {
        return getAssinaturaEletronicaDTOByUUID(uuid, false);
    }

    public AssinaturaEletronicaDTO getAssinaturaEletronicaDTOByUUID(UUID uuid, boolean trazerImagem) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AssinaturaEletronicaDTO> query = cb.createQuery(AssinaturaEletronicaDTO.class);
        Root<AssinaturaEletronica> assEletronica = query.from(AssinaturaEletronica.class);

        query.select(resolveSelect(cb, query, assEletronica));
        query.where(cb.equal(assEletronica.get(AssinaturaEletronica_.uuid), uuid));

        try {
            AssinaturaEletronicaDTO result = getEntityManager().createQuery(query).getSingleResult();
            if(trazerImagem) {
                final byte[] imagem = assinaturaEletronicaBinSearch.getImagemByIdAssinaturaEletronicaBin(result.getId());
                result.setImagem(imagem);
            }
            return result;
        } catch (NoResultException e) {
            return null;
        }

    }

    private CompoundSelection<AssinaturaEletronicaDTO> resolveSelect(CriteriaBuilder cb,
            CriteriaQuery<AssinaturaEletronicaDTO> query, Root<AssinaturaEletronica> assEletronica) {
        return cb.construct(query.getResultType(), assEletronica.get(AssinaturaEletronica_.id),
                assEletronica.get(AssinaturaEletronica_.pessoaFisica).get(PessoaFisica_.idPessoa),
                assEletronica.get(AssinaturaEletronica_.nomeArquivo), assEletronica.get(AssinaturaEletronica_.extensao),
                assEletronica.get(AssinaturaEletronica_.contentType),
                assEletronica.get(AssinaturaEletronica_.dataInclusao),
                assEletronica.get(AssinaturaEletronica_.uuid));
    }


}
