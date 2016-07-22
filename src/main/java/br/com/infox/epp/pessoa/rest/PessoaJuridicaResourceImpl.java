package br.com.infox.epp.pessoa.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class PessoaJuridicaResourceImpl implements PessoaJuridicaResource {

    private String cnpj;

    public PessoaJuridicaResourceImpl() {}

    public PessoaJuridicaResourceImpl(String cnpj) {
        super();
        this.cnpj = cnpj;
    }

    @Inject
    private PessoaJuridicaRestService pessoaJuridicaRestService;

    @Override
    public Response edit(PessoaJuridicaDTO pjDTO) {
        pessoaJuridicaRestService.edit(pjDTO);
        return Response.ok().build();
    }

    @Override
    public PessoaJuridicaDTO get() {
        return pessoaJuridicaRestService.get(cnpj);
    }

    @Override
    public Response delete() {
        pessoaJuridicaRestService.delete(cnpj);
        return Response.ok().build();
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
