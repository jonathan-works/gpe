package br.gov.mt.cuiaba.pmc.gdprev;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService(serviceName="ProcessoEndpoint")
@SOAPBinding(style = Style.RPC)
public interface ProcessoEndpoint {
	@WebMethod(action="ConsultarProcessos")
	@WebResult(name="Processos")
	Processos consultarProcessos(
		@WebParam(header = true, mode = Mode.IN, name = "usuario") String username,
		@WebParam(header = true, mode = Mode.IN, name = "senha") String password,
		@WebParam(header = false, mode = Mode.IN, name = "dataAlteracao") String dataAlteracao) throws WebServiceException;

	
    @WebMethod(action="RecuperarProcessoEmDocumento")
    @WebResult(name="Documento")
    Documento recuperarProcessoEmDocumento(
			@WebParam(header = true, mode = Mode.IN, name = "usuario") String username,
			@WebParam(header = true, mode = Mode.IN, name = "senha") String password,
			@WebParam(header = false, mode = Mode.IN, name = "numeroDoProcesso") String numeroDoProcesso) throws WebServiceException;
	
}
