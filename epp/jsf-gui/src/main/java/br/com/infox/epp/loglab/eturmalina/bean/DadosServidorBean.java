package br.com.infox.epp.loglab.eturmalina.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DadosServidorBean {
    private String cpf;
    private String matricula;

    public DadosServidorBean(String cpf) {
        this.cpf = cpf;
    }

}
