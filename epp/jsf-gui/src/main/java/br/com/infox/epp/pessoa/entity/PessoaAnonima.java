package br.com.infox.epp.pessoa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = PessoaAnonima.TABLE_NAME)
@PrimaryKeyJoinColumn(name = "id_pessoa_anonima", columnDefinition = "integer")
public class PessoaAnonima extends Pessoa {

    public static final String TABLE_NAME = "tb_pessoa_anonima";
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @Size(max = 15)
    @Column(name = "nr_telefone", nullable = true)
    private String telefone;

    public PessoaAnonima() {
        setTipoPessoa(TipoPessoaEnum.A);
    }

    @Override
    public String getCodigo() {
        return null;
    }

}