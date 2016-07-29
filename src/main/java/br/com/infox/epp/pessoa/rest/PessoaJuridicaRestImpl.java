package br.com.infox.epp.pessoa.rest;

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
        String stringRetorno = uriInfo.getAbsolutePath().toASCIIString() + pjDTO.getCnpj();
        return Response.ok().header("Location", stringRetorno).build();
    }

    @Override
    public PessoaJuridicaResource getPessoaJuridicaResource(String cnpj) {
        pessoaJuridicaResourceImpl.setCnpj(cnpj);
        return pessoaJuridicaResourceImpl;
    }
}
