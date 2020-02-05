package br.com.infox.epp.certificadoeletronico;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.certificadoeletronico.builder.CertBuilder;
import br.com.infox.epp.certificadoeletronico.builder.CertificadoDTO;
import br.com.infox.epp.certificadoeletronico.builder.RootCertBuilder;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronico;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronicoBin;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CertificadoEletronicoService extends PersistenceController {

    @Inject
    @GenericDao
    private Dao<CertificadoEletronico, Long> certificadoEletronicoDao;

    @Inject
    @GenericDao
    private Dao<PessoaFisica, Integer> pessoaFisicaDao;

    @Inject
    @GenericDao
    private Dao<UsuarioLogin, Integer> usuarioLoginDao;

    @Inject
    private ParametroManager parametroManager;

    @Override
    public EntityManager getEntityManagerBin() {
        return EntityManagerProducer.getEntityManagerBin();
    }

    public boolean podeEmitirCertificado(PessoaFisica pf) {
        return existeCertificadoEletronicoBinRaiz() && pf != null && (
            pf.getCertificadoEletronico() == null // Não possui certificado
            || !pf.getCertificadoEletronico().isAtivo() // Certificado inativo
            || new Date().after(pf.getCertificadoEletronico().getDataFim()) // Certificado expirado
        );
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gerarCertificadoRaiz() {
        Long idCertificadoEletronicoRaiz = persist(new RootCertBuilder(Parametros.INFO_CERT_ELETRONICO_RAIZ.getValue()).gerar());
        Parametro parametro = getParametroCertificadoRaiz();
        parametro.setValorVariavel(idCertificadoEletronicoRaiz.toString());
        parametroManager.persist(parametro);
        for (UsuarioLogin usuarioLogin : usuarioLoginDao.findAll()) {
            if(usuarioLogin.getPessoaFisica() != null) {
                gerarCertificado(usuarioLogin.getPessoaFisica());
            }
        }
        parametroManager.flush();
    }

    private Parametro getParametroCertificadoRaiz() {
        Parametro parametro = parametroManager.getParametro(Parametros.CERT_ELETRONICO_RAIZ.getLabel());
        if(parametro == null) {
            throw new EppConfigurationException(String.format("Parâmetro não encontrado: %s", Parametros.CERT_ELETRONICO_RAIZ.getLabel()));
        }
        return parametro;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gerarCertificado(PessoaFisica pf) {
        CertificadoEletronicoBin certificadoEletronicoBinRaiz = getCertificadoEletronicoBinRaiz();


        CertificadoDTO certificadoDTO = new CertBuilder(new CertBuilder.CertBuilderDTO(
            certificadoEletronicoBinRaiz.getCrt(),
            certificadoEletronicoBinRaiz.getKey(),
            pf.getNome(),
            pf.getCodigo(),
            pf.getDataNascimento(),
            pf.getUsuarioLogin().getSenha()
        )).gerar();

        PessoaFisica pessoaFisica = getPessoaFisicaLogada();
        certificadoDTO.setIdPessoaFisica(pessoaFisica.getIdPessoa());
        certificadoDTO.setIdCertificadoEletronicoPai(certificadoEletronicoBinRaiz.getId());
        persist(certificadoDTO);
    }

    private PessoaFisica getPessoaFisicaLogada() {
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        if(usuario == null || usuario.getPessoaFisica() == null) {
            throw new BusinessRollbackException("Usuário logado não é uma pessoa física.");
        }
        return usuario.getPessoaFisica();
    }

    public CertificadoEletronico getCertificadoEletronicoRaiz() {
        Long idCertificadoEletronicoRaiz = Long.parseLong(getParametroCertificadoRaiz().getValorVariavel());
        if(idCertificadoEletronicoRaiz == null || idCertificadoEletronicoRaiz <= 0) {
            return null;
        }
        return certificadoEletronicoDao.findById(idCertificadoEletronicoRaiz);
    }

    public boolean existeCertificadoEletronicoBinRaiz() {
        Long idCertificadoEletronicoRaiz = Long.parseLong(getParametroCertificadoRaiz().getValorVariavel());
        return idCertificadoEletronicoRaiz != null && idCertificadoEletronicoRaiz > 0;
    }

    public CertificadoEletronicoBin getCertificadoEletronicoBinRaiz() {
        Long idCertificadoEletronicoRaiz = Long.parseLong(getParametroCertificadoRaiz().getValorVariavel());
        if(idCertificadoEletronicoRaiz == null || idCertificadoEletronicoRaiz <= 0) {
            throw new EppConfigurationException(String.format("Parâmetro não configurado: %s", Parametros.CERT_ELETRONICO_RAIZ.getLabel()));
        }
        return getCertificadoEletronicoBin(idCertificadoEletronicoRaiz);
    }

    public CertificadoEletronicoBin getCertificadoEletronicoBin(Long idCertificadoEletronicoRaiz) {
        CertificadoEletronico certificadoEletronicoRaiz = certificadoEletronicoDao.findById(idCertificadoEletronicoRaiz);
        Date now = Calendar.getInstance().getTime();
        if(certificadoEletronicoRaiz == null ) {
            throw new BusinessRollbackException("Certificado raiz não foi gerado");
        } else if(now.before(certificadoEletronicoRaiz.getDataInicio()) ||
                now.after(certificadoEletronicoRaiz.getDataFim())) {
            throw new BusinessRollbackException("Certificado raiz expirado.");
        }
        CertificadoEletronicoBin certificadoEletronicoBin = getEntityManagerBin().find(CertificadoEletronicoBin.class, idCertificadoEletronicoRaiz);
        if(certificadoEletronicoBin == null) {
            throw new BusinessRollbackException("Conteúdo do certificado raiz não foi gerado");
        }
        return certificadoEletronicoBin;
    }

    private Long persist(CertificadoDTO certificadoDTO) {
        CertificadoEletronico certificadoEletronico = new CertificadoEletronico();
        certificadoEletronico.setDataCadastro(Calendar.getInstance().getTime());
        certificadoEletronico.setDataInicio(certificadoDTO.getDataInicio());
        certificadoEletronico.setDataFim(certificadoDTO.getDataFim());
        certificadoEletronico.setSenha(certificadoDTO.getSenha());
        if(certificadoDTO.getIdPessoaFisica() != null) {
            certificadoEletronico.setPessoaFisica(pessoaFisicaDao.findById(certificadoDTO.getIdPessoaFisica()));
            certificadoEletronico.getPessoaFisica().setCertificadoEletronico(certificadoEletronico);
        }
        if(certificadoDTO.getIdCertificadoEletronicoPai() != null) {
            certificadoEletronico.setCertificadoEletronicoPai(certificadoEletronicoDao.findById(certificadoDTO.getIdCertificadoEletronicoPai()));
        }
        CertificadoEletronico novoCertificadoEletronico = certificadoEletronicoDao.persist(certificadoEletronico);
        certificadoEletronicoDao.flush();

//        pessoaFisicaDao.persist(certificadoEletronico.getPessoaFisica());

        if(novoCertificadoEletronico.getId() == null || novoCertificadoEletronico.getId() <= 0) {
            throw new BusinessRollbackException("Certificado eletrônico raiz não encontrado.");
        }

        CertificadoEletronicoBin certificadoEletronicoBin = new CertificadoEletronicoBin();
        certificadoEletronicoBin.setId(novoCertificadoEletronico.getId());
        certificadoEletronicoBin.setCrt(certificadoDTO.getCert());
        certificadoEletronicoBin.setKey(certificadoDTO.getKey());
        getEntityManagerBin().persist(certificadoEletronicoBin);
        getEntityManagerBin().flush();

        return novoCertificadoEletronico.getId();
    }

}