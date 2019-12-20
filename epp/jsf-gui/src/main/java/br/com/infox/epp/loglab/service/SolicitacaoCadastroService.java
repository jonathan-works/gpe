package br.com.infox.epp.loglab.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.epp.loglab.vo.SolicitacaoCadastroVO;
import br.com.infox.ibpm.sinal.SignalParam;
import br.com.infox.ibpm.sinal.SignalParam.Type;
import br.com.infox.ibpm.sinal.SignalSearch;
import br.com.infox.ibpm.sinal.SignalService;
import br.com.infox.seam.exception.ValidationException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SolicitacaoCadastroService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static final String CODIGO_SINAL_SOLICITACAO_CADASTRO = "solicitacaoCadastro";
    
    private static final String VARIAVEL_NOME_COMPLETO_SOLICITANTE = "nomeCompletoSolicitante";
    private static final String VARIAVEL_CPF_SOLICITANTE = "cpfSolicitante";
    private static final String VARIAVEL_NUMERO_RG_SOLICITANTE = "numeroRgSolicitante";
    private static final String VARIAVEL_EMISSOR_RG_SOLICITANTE = "emissorRgSolicitante";
    private static final String VARIAVEL_UF_RG_SOLICITANTE = "ufRgSolicitante";
    private static final String VARIAVEL_TELEFONE_SOLICITANTE = "telefoneSolicitante";
    private static final String VARIAVEL_CARGO_SOLICITANTE = "cargoSolicitante";
    private static final String VARIAVEL_DEPARTAMENTO_SOLICITANTE = "departamentoSolicitante";

    @Inject
    private SignalService signalService;

    @Inject
    private SignalSearch signalSearch;
    
    public void criarSinalSolicitacaoCadastro(SolicitacaoCadastroVO solicitacaoCadastro) {
        validarSinal();
        List<SignalParam> params = getParametros(solicitacaoCadastro);
        signalService.startStartStateListening(CODIGO_SINAL_SOLICITACAO_CADASTRO, params);
    }
    
    private void validarSinal() {
        if(!signalSearch.existeSignalByCodigo(CODIGO_SINAL_SOLICITACAO_CADASTRO)) {
            throw new ValidationException("Não foi encontrado o sinal com código \"" + CODIGO_SINAL_SOLICITACAO_CADASTRO + "\".");
        }
    }
    
    private List<SignalParam> getParametros(SolicitacaoCadastroVO solicitacaoCadastro) {
        List<SignalParam> params = new ArrayList<>();
        params.add(new SignalParam(VARIAVEL_NOME_COMPLETO_SOLICITANTE, solicitacaoCadastro.getNomeCompleto(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_CPF_SOLICITANTE, solicitacaoCadastro.getCpf(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_NUMERO_RG_SOLICITANTE, solicitacaoCadastro.getNumeroRg(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_EMISSOR_RG_SOLICITANTE, solicitacaoCadastro.getEmissorRg(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_UF_RG_SOLICITANTE, solicitacaoCadastro.getUfRg(), Type.VARIABLE));

        params.add(new SignalParam(VARIAVEL_TELEFONE_SOLICITANTE, solicitacaoCadastro.getTelefone(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_CARGO_SOLICITANTE, solicitacaoCadastro.getCargo(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_DEPARTAMENTO_SOLICITANTE, solicitacaoCadastro.getDepartamento(), Type.VARIABLE));
        
        return params;
    }
}
