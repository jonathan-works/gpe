package br.com.infox.epp.pessoa.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;

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
            throw new WebApplicationException(409);
        }
    }

    public PessoaJuridicaDTO get(String cnpj) {
        PessoaJuridica pj = pessoaJuridicaDAO.searchByCnpj(cnpj);
        if (pj == null || !pj.getAtivo()) {
            throw new WebApplicationException(404);
        }
        return new PessoaJuridicaDTO(pj.getNome(), pj.getCnpj(), pj.getRazaoSocial());
    }

    public void edit(PessoaJuridicaDTO pjDTO) {
        PessoaJuridica pj = pessoaJuridicaDAO.searchByCnpj(pjDTO.getCnpj());
        if (pj == null || !pj.getAtivo()) {
            throw new WebApplicationException(404);
        }
        pj.setAtivo(true);
        pj.setCnpj(pjDTO.getCnpj());
        pj.setNome(pjDTO.getNomeFantasia());
        pj.setRazaoSocial(pjDTO.getRazaoSocial());
        pj.setTipoPessoa(TipoPessoaEnum.J);
        pessoaJuridicaDAO.update(pj);
    }

    public void delete(String cnpj) {
        PessoaJuridica pj = pessoaJuridicaDAO.searchByCnpj(cnpj);
        if (pj == null || !pj.getAtivo()) {
            throw new WebApplicationException(404);
        }
        pj.setAtivo(false);
        pessoaJuridicaDAO.update(pj);
    }
}
