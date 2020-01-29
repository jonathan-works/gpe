package br.com.infox.epp.loglab.eturmalina.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DadosServidorResponseBean {

    private String matricula;
    private String nome;
    private String cpf;
    private String dataNomeacaoContratacao;
    private String dataPosse;
    private String dataExercicio;
    private String situacao;
    private String status;
    private String orgao;
    private String localTrabalho;
    private String subFolha;
    private String jornada;
    private String ocupacaoCarreira;
    private String cargoCarreira;
    private String ocupacaoComissao;
    private String cargoComissao;
    private String servidorFiliacaoPai;
    private String servidorFiliacaoMae;
    private String servidorRG;
    private String servidorDataNascimento;
    private String servidorRGEmissao;
    private String servidorRGOrgao;

    public static Type getListType(){
        return new TypeToken<List<DadosServidorResponseBean>>(){}.getType();
    }
}
