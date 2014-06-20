package br.com.infox.ibpm.variable.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.COLUMN_DOMINIO;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.COLUMN_ID;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.COLUMN_NOME;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.SEQUENCE_NAME;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.TABLE_NAME;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = TABLE_NAME)
public class DominioVariavelTarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = COLUMN_ID)
    private Integer id;

    @Column(name = COLUMN_DOMINIO, nullable = false, columnDefinition = "TEXT")
    private String dominio;

    @Column(name = COLUMN_NOME, nullable = false, length = LengthConstants.NOME_MEDIO)
    private String nome;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
