package br.com.infox.epp.loglab.eturmalina.bean;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DadosServidorResponseBean {

    private String Matricula;
    private String Nome;
    private String Cpf;
    private String DataNomeacaoContratacao;
    private String DataPosse;
    private String DataExercicio;
    private String Situacao;
    private String Status;
    private String Orgao;
    private String LocalTrabalho;
    private String SubFolha;
    private String Jornada;
    private String OcupacaoCarreira;
    private String CargoCarreira;
    private String OcupacaoComissao;
    private String CargoComissao;
    private String ServidorFiliacaoPai;
    private String ServidorFiliacaoMae;
    private String ServidorRG;
    private String ServidorDataNascimento;
    private String ServidorRGEmissao;
    private String ServidorRGOrgao;
    private String ServidorCidadeNome;
    private String ServidorLogrTipoDesc;
    private String ServidorLogradouro;
    private String ServidorBairroNome;
    private String ServidorEndComplemento;
    private String ServidorEndNumero;
    private String ServidorCEP;
    private String ServidorSexo;
    private String ServidorEmail;
    private String ServidorTelefoneCelular;

    public static Type getListType(){
        return new TypeToken<List<DadosServidorResponseBean>>(){}.getType();
    }

}
