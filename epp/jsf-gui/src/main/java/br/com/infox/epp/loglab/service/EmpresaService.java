package br.com.infox.epp.loglab.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.model.Empresa;
import br.com.infox.epp.loglab.vo.EmpresaVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class EmpresaService extends PersistenceController {

    @GenericDao
    @Inject
    private Dao<Empresa, Long> empresaDAO;
    @Inject
    private PessoaJuridicaManager pessoaJuridicaManager;
    @Inject
    private EstadoSearch estadoSearch;

    public void gravar(EmpresaVO empresaVO) {
        Empresa empresa = empresaFromEmpresaVO(empresaVO);
        if (empresa.getId() == null) {
            empresaDAO.persist(empresa);
        } else {
            empresaDAO.update(empresa);
        }
    }

    private Empresa empresaFromEmpresaVO(EmpresaVO vo) {
        PessoaJuridica pessoaJuridica = pessoaJuridicaManager.getByCnpj(vo.getCnpj());
        if(pessoaJuridica == null) {
            throw new BusinessRollbackException("Falha ao associar a pessoa jurídica: " + vo.toString());
        }
        Estado estado = null;
        Empresa empresa = new Empresa();
        empresa.setId(vo.getId());
        empresa.setBairro(vo.getBairro());
        empresa.setCep(vo.getCep());
        empresa.setCidade(vo.getCidade());
        empresa.setCnpj(vo.getCnpj());
        empresa.setComplemento(vo.getComplemento());
        empresa.setDataAbertura(vo.getDataAbertura());
        empresa.setEmail(vo.getEmail());
        if (!StringUtil.isEmpty(vo.getCodEstado())) {
            estado = estadoSearch.retrieveEstadoByCodigo(vo.getCodEstado());
        }
        empresa.setEstado(estado);
        empresa.setLogradouro(vo.getLogradouro());
        empresa.setNomeFantasia(vo.getNomeFantasia());
        empresa.setNumeroResidencia(vo.getNumero());
        empresa.setPessoaJuridica(pessoaJuridica);
        empresa.setRazaoSocial(vo.getRazaoSocial());
        empresa.setTelefoneCelular(vo.getTelefoneCelular());
        empresa.setTelefoneFixo(vo.getTelefoneFixo());
        empresa.setTipoEmpresa(vo.getTipoEmpresa());
        return empresa;
    }

    public PessoaJuridica convertPessoaJuridicaFromServidorContribuinteVO(EmpresaVO vo) {
        PessoaJuridica pessoaJuridica = pessoaJuridicaManager.getByCnpj(vo.getCnpj());
        if(pessoaJuridica == null) {
            pessoaJuridica = new PessoaJuridica();
        }
        pessoaJuridica.setAtivo(Boolean.TRUE);
        pessoaJuridica.setCnpj(vo.getCnpj());
        pessoaJuridica.setNome(vo.getNomeFantasia());
        pessoaJuridica.setRazaoSocial(vo.getRazaoSocial());
        return pessoaJuridica;
    }

}
