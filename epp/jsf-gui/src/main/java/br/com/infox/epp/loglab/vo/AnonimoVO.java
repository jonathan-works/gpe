package br.com.infox.epp.loglab.vo;

import javax.validation.constraints.Size;

import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="idPessoa")
public class AnonimoVO {

    private Integer idPessoa;
    @Size(max = 150)
    private String nome;
    @Size(max = 15)
    private String telefone;
    private TipoParticipanteEnum tipoParticipante;

    public String getNomeDefaultIfNull() {
        return getNome() == null || getNome().trim().length() == 0 ? TipoParticipanteEnum.ANON.getLabel() : getNome();
    }

    @Override
    public String toString() {
        return this.getNomeDefaultIfNull();
    }

}