package br.com.infox.epp.loglab.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.loglab.search.ContribuinteSolicitanteSearch;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.ibpm.sinal.SignalParam;
import br.com.infox.ibpm.sinal.SignalParam.Type;
import br.com.infox.ibpm.sinal.SignalSearch;
import br.com.infox.ibpm.sinal.SignalService;
import br.com.infox.seam.exception.ValidationException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ContribuinteService extends PersistenceController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String CODIGO_SINAL_CADASTRO_CONTRIBUINTE = "cadastroContribuinte";

    private static final String VARIAVEL_TIPO_CONTRIBUINTE = "tipoContribuinte";
    private static final String VARIAVEL_CPF_CONTRIBUINTE = "cpfContribuinte";
    private static final String VARIAVEL_MATRICULA_CONTRIBUINTE = "matriculaContribuinte";
    private static final String VARIAVEL_NOME_COMPLETO_CONTRIBUINTE = "nomeCompletoContribuinte";
    private static final String VARIAVEL_SEXO_CONTRIBUINTE = "sexoContribuinte";
    private static final String VARIAVEL_DATA_NASCIMENTO_CONTRIBUINTE = "dataNascimentoContribuinte";
    private static final String VARIAVEL_NUMERO_RG_CONTRIBUINTE = "numeroRgContribuinte";
    private static final String VARIAVEL_EMISSOR_RG_CONTRIBUINTE = "emissorRgContribuinte";
    private static final String VARIAVEL_UF_RG_CONTRIBUINTE = "ufRgContribuinte";
    private static final String VARIAVEL_NOME_MAE_CONTRIBUINTE = "nomeMaeContribuinte";
    private static final String VARIAVEL_EMAIL_CONTRIBUINTE = "emailContribuinte";
    private static final String VARIAVEL_TELEFONE_CONTRIBUINTE = "telefoneContribuinte";
    private static final String VARIAVEL_CIDADE_ENDERECO_CONTRIBUINTE = "cidadeEnderecoContribuinte";
    private static final String VARIAVEL_LOGRADOURO_ENDERECO_CONTRIBUINTE = "logradouroEnderecoContribuinte";
    private static final String VARIAVEL_BAIRRO_ENDERECO_CONTRIBUINTE = "bairroEnderecoContribuinte";
    private static final String VARIAVEL_COMPLEMENTO_ENDERECO_CONTRIBUINTE = "complementoEnderecoContribuinte";
    private static final String VARIAVEL_NUMERO_ENDERECO_CONTRIBUINTE = "numeroEnderecoContribuinte";
    private static final String VARIAVEL_CEP_ENDERECO_CONTRIBUINTE = "cepEnderecoContribuinte";

    @Inject
    private SignalService signalService;
    @Inject
    private SignalSearch signalSearch;
    @Inject
    private ContribuinteSolicitanteService contribuinteSolicitanteService;
    @Inject
    private ContribuinteSolicitanteSearch contribuinteSolicitanteSearch;

    public void criarSinalContribuinte(ContribuinteSolicitanteVO contribuinteVO) {
        if (contribuinteSolicitanteSearch.isExisteUsuarioContribuinteSolicitante(contribuinteVO.getCpf())) {
            throw new ValidationException("Já existe um usuário cadastrado para este CPF.");
        }

        validarSinal();
        List<SignalParam> params = getParametros(contribuinteVO);
        signalService.startStartStateListening(CODIGO_SINAL_CADASTRO_CONTRIBUINTE, params);
        contribuinteSolicitanteService.gravar(contribuinteVO);
    }

    private void validarSinal() {
        if(!signalSearch.existeSignalByCodigo(CODIGO_SINAL_CADASTRO_CONTRIBUINTE)) {
            throw new ValidationException("Não foi encontrado o sinal com código \"" + CODIGO_SINAL_CADASTRO_CONTRIBUINTE + "\".");
        }
    }

    private List<SignalParam> getParametros(ContribuinteSolicitanteVO contribuinteVO) {
        List<SignalParam> params = new ArrayList<>();

        String dataNascimentoContribuinte = DateUtil.formatarData(contribuinteVO.getDataNascimento());
        Estado estado = getEntityManager().find(Estado.class, contribuinteVO.getIdEstadoRg());
        String ufRgContribuinte = estado.getCodigo();

        params.add(new SignalParam(VARIAVEL_TIPO_CONTRIBUINTE, contribuinteVO.getTipoContribuinte().getLabel(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_CPF_CONTRIBUINTE, contribuinteVO.getCpf(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_MATRICULA_CONTRIBUINTE, contribuinteVO.getMatricula(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_NOME_COMPLETO_CONTRIBUINTE, contribuinteVO.getNomeCompleto(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_SEXO_CONTRIBUINTE, contribuinteVO.getSexo(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_DATA_NASCIMENTO_CONTRIBUINTE, dataNascimentoContribuinte, Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_NUMERO_RG_CONTRIBUINTE, contribuinteVO.getNumeroRg(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_EMISSOR_RG_CONTRIBUINTE, contribuinteVO.getEmissorRg(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_UF_RG_CONTRIBUINTE, ufRgContribuinte, Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_NOME_MAE_CONTRIBUINTE, contribuinteVO.getNomeMae(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_EMAIL_CONTRIBUINTE, contribuinteVO.getEmail(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_TELEFONE_CONTRIBUINTE, contribuinteVO.getTelefone(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_CIDADE_ENDERECO_CONTRIBUINTE, contribuinteVO.getCidade(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_LOGRADOURO_ENDERECO_CONTRIBUINTE, contribuinteVO.getLogradouro(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_BAIRRO_ENDERECO_CONTRIBUINTE, contribuinteVO.getBairro(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_COMPLEMENTO_ENDERECO_CONTRIBUINTE, contribuinteVO.getComplemento(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_NUMERO_ENDERECO_CONTRIBUINTE, contribuinteVO.getNumero(), Type.VARIABLE));
        params.add(new SignalParam(VARIAVEL_CEP_ENDERECO_CONTRIBUINTE, contribuinteVO.getCep(), Type.VARIABLE));

        return params;
    }

}
