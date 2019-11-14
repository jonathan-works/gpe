package br.com.infox.epp.pessoa.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.TokenAuthentication.TipoExcecao;

@TokenAuthentication(tipoExcecao=TipoExcecao.JSON)
public class PessoaJuridicaRestImpl implements PessoaJuridicaRest {

    @Inject
    private PessoaJuridicaResourceImpl pessoaJuridicaResourceImpl;
    @Inject
    private PessoaJuridicaRestService pessoaJuridicaRestService;

    @Override
    public Response add(UriInfo uriInfo, PessoaJuridicaDTO pjDTO) {
        pessoaJuridicaRestService.add(pjDTO);
        URI location = uriInfo.getAbsolutePathBuilder().path(pjDTO.getCnpj()).build();
        return Response.created(location).build();
    }

    @Override
    public PessoaJuridicaResource getPessoaJuridicaResource(String cnpj) {
        pessoaJuridicaResourceImpl.setCnpj(cnpj);
        return pessoaJuridicaResourceImpl;
    }
}
