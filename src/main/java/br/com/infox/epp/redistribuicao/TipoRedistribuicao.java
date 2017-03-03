package br.com.infox.epp.redistribuicao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = "tb_tipo_redistribuicao")
public class TipoRedistribuicao implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String GENERATOR_NAME = "TipoRedistribuicaoGenerator";
    private static final String SEQUENCE_NAME = "sq_tipo_redistribuicao";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_redistribuicao", updatable = false)
    private Long id;

    @NotNull
    @Size(max = LengthConstants.CODIGO_DOCUMENTO)
    @Column(name = "cd_tipo_redistribuicao", unique = true)
    private String codigo;

    @NotNull
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    @Column(name = "ds_tipo_redistribuicao", unique = true)
    private String descricao;

    @Column(name = "in_ativo")
    private boolean ativo = true;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
