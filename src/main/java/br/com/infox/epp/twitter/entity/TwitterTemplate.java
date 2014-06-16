package br.com.infox.epp.twitter.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = TwitterTemplate.TABLE_NAME)
public class TwitterTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_twitter_template";
    private static final int TAMANHO_MAXIMO = 123;

    private Integer idTwitterTemplate;
    private String titulo;
    private String mensagem;
    private Boolean ativo;

    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_twitter_template")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_twitter_template")
    public Integer getIdTwitterTemplate() {
        return idTwitterTemplate;
    }

    public void setIdTwitterTemplate(Integer id) {
        this.idTwitterTemplate = id;
    }

    @Column(name = "ds_titulo", nullable = false, length = LengthConstants.DESCRICAO_PADRAO_METADE)
    @Size(max = LengthConstants.DESCRICAO_PADRAO_METADE)
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Column(name = "ds_mensagem", nullable = false, length = TAMANHO_MAXIMO)
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Column(name = "in_ativo", nullable = false)
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return this.titulo;
    }
}
