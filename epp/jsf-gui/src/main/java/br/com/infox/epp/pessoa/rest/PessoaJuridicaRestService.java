package br.com.infox.epp.pessoa.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.ws.exception.ConflictWSException;
import br.com.infox.epp.ws.exception.NotFoundWSException;

@Stateless
public class PessoaJuridicaRestService {

    @Inject
    private PessoaJuridicaDAO pessoaJuridicaDAO;

    public void add(PessoaJuridicaDTO pjDTO) {
        PessoaJuridica pj = pessoaJuridicaDAO.searchByCnpj(pjDTO.getCnpj());
        if (pj == null) {
            pj = pjDTO.toPJ();
            pessoaJuridicaDAO.persist(pj);
        } else if (!pj.getAtivo()) {
            pj.setAtivo(true);
            pj.setNome(pjDTO.getNomeFantasia());
            pj.setRazaoSocial(pjDTO.getRazaoSocial());
            pessoaJuridicaDAO.update(pj);
        } else {
            throw new ConflictWSException("Já existe uma pessoa jurídica cadastrada com o CNPJ " + pjDTO.getCnpj());
        }
    }

    public PessoaJuridicaDTO get(String cnpj) {
        PessoaJuridica pj = pessoaJuridicaDAO.searchByCnpj(cnpj);
        if (pj == null || !pj.getAtivo()) {
            throw new NotFoundWSException("Não foi encontrada pessoa jurídica cadastra com o CNPJ " + cnpj);
        }
        return new PessoaJuridicaDTO(pj.getNome(), pj.getCnpj(), pj.getRazaoSocial());
    }

    public void edit(String cnpj, PessoaJuridicaDTO pjDTO) {
        PessoaJuridica pj = pessoaJuridicaDAO.searchByCnpj(cnpj);
        if (pj == null || !pj.getAtivo()) {
            throw new NotFoundWSException("Não foi encontrada pessoa jurídica cadastra com o CNPJ " + cnpj);
        }
        pj.setNome(pjDTO.getNomeFantasia());
        pj.setRazaoSocial(pjDTO.getRazaoSocial());
        pessoaJuridicaDAO.update(pj);
    }

    public void delete(String cnpj) {
        PessoaJuridica pj = pessoaJuridicaDAO.searchByCnpj(cnpj);
        if (pj == null || !pj.getAtivo()) {
            throw new NotFoundWSException("Não foi encontrada pessoa jurídica cadastra com o CNPJ " + cnpj);
        }
        pj.setAtivo(false);
        pessoaJuridicaDAO.update(pj);
    }
}
