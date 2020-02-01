package br.com.infox.epp.loglab.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.contribuinte.type.ContribuinteEnum;
import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante;
import br.com.infox.epp.loglab.model.Servidor;
import br.com.infox.epp.loglab.vo.ServidorContribuinteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoaFisica.PessoaFisicaSearch;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServidorContribuinteService extends PersistenceController {

    @Inject
    @GenericDao
    private Dao<Servidor, Long> servidorDAO;
    @Inject
    @GenericDao
    private Dao<ContribuinteSolicitante, Long> contribuinteDAO;
    @Inject
    private PessoaFisicaSearch pessoaFisicaSearch;
    @Inject
    private EstadoSearch estadoSearch;

    public void gravar(ServidorContribuinteVO servidorContribuinteVO) {
        if (TipoParticipanteEnum.SE.equals(servidorContribuinteVO.getTipoParticipante())) {
            gravarServidor(servidorContribuinteVO);
        } else if (TipoParticipanteEnum.CO.equals(servidorContribuinteVO.getTipoParticipante())) {
            gravarContribuinte(servidorContribuinteVO);
        }
    }

    private void gravarServidor(ServidorContribuinteVO servidorContribuinteVO) {
        Servidor servidor = servidorFromServidorContribuinteVO(servidorContribuinteVO);
        if (servidor.getId() == null) {
            servidorDAO.persist(servidor);
        } else {
            servidorDAO.update(servidor);
        }
    }

    private void gravarContribuinte(ServidorContribuinteVO servidorContribuinteVO) {
        ContribuinteSolicitante contribuinte = contribuinteFromServidorContribuinteVO(servidorContribuinteVO);
        if (contribuinte == null) {
            contribuinteDAO.persist(contribuinte);
        } else {
            contribuinteDAO.update(contribuinte);
        }
    }

    private Servidor servidorFromServidorContribuinteVO(ServidorContribuinteVO vo) {
        PessoaFisica pessoaFisica = recuperarPessoaFisica(vo);
        Servidor servidor = new Servidor();
        servidor.setId(vo.getId());
        servidor.setCargoCarreira(vo.getCargoCarreira());
        servidor.setCargoComissao(vo.getCargoComissao());
        servidor.setCelular(vo.getCelular());
        servidor.setCpf(vo.getCpf());
        servidor.setDataEmissaoRg(vo.getDataEmissaoRg());
        servidor.setDataExercicio(vo.getDataExercicio());
        servidor.setDataNascimento(vo.getDataNascimento());
        servidor.setDataNomeacaoContratacao(vo.getDataNomeacao());
        servidor.setDataPosse(vo.getDataPosse());
        servidor.setDepartamento(vo.getLocalTrabalho());
        servidor.setEmail(vo.getEmail());
        servidor.setJornada(vo.getJornada());
        servidor.setMae(vo.getNomeMae());
        servidor.setMatricula(vo.getMatricula());
        servidor.setNomeCompleto(vo.getNomeCompleto());
        servidor.setNumeroRg(vo.getNumeroRg());
        servidor.setOcupacaoCarreira(vo.getOcupacaoCarreira());
        servidor.setOcupacaoComissao(vo.getOcupacaoComissao());
        servidor.setOrgaoEmissorRG(vo.getOrgaoEmissorRG());
        servidor.setPai(vo.getNomePai());
        servidor.setPessoaFisica(pessoaFisica);
        servidor.setSecretaria(vo.getOrgao());
        servidor.setSituacao(vo.getSituacao());
        servidor.setSubFolha(vo.getSubFolha());
        return servidor;
    }

    private ContribuinteSolicitante contribuinteFromServidorContribuinteVO(ServidorContribuinteVO vo) {
        PessoaFisica pessoaFisica = recuperarPessoaFisica(vo);
        Estado estado = null;
        ContribuinteSolicitante contribuinte = new ContribuinteSolicitante();
        contribuinte.setId(vo.getId());
        contribuinte.setPessoaFisica(pessoaFisica);
        contribuinte.setBairro(vo.getBairro());
        contribuinte.setCep(vo.getCep());
        contribuinte.setCidade(vo.getCidade());
        contribuinte.setComplemento(vo.getComplemento());
        contribuinte.setCpf(vo.getCpf());
        contribuinte.setDataNascimento(vo.getDataNascimento());
        contribuinte.setEmail(vo.getEmail());
        if (!StringUtil.isEmpty(vo.getCodEstado())) {
            estado = estadoSearch.retrieveEstadoByCodigo(vo.getCodEstado());
        }
        contribuinte.setEstado(estado);
        contribuinte.setLogradouro(vo.getLogradouro());
        contribuinte.setNomeCompleto(vo.getNomeCompleto());
        contribuinte.setNomeMae(vo.getNomeMae());
        contribuinte.setNumero(vo.getNumero());
        contribuinte.setSexo(vo.getSexo());
        contribuinte.setTelefone(vo.getCelular());
        contribuinte.setTipoContribuinte(ContribuinteEnum.CO);
        return contribuinte;
    }

    private PessoaFisica recuperarPessoaFisica(ServidorContribuinteVO vo) {
        PessoaFisica pessoaFisica = pessoaFisicaSearch.getByCpf(vo.getCpf());
        if(pessoaFisica == null) {
            throw new BusinessRollbackException("Falha ao associar a pessoa física: " + vo.toString());
        }
        return pessoaFisica;
    }
}
