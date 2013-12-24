package br.com.infox.epp.access.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tb_recurso", schema=PUBLIC, uniqueConstraints = @UniqueConstraint(columnNames = "ds_identificador"))
public class Recurso implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idRecurso;
    private String nome;
    private String identificador;
    
    public Recurso(){
        
    }
    
    public Recurso(String nome, String identificador){
        this.nome = nome;
        this.identificador = identificador;
        
    }

    @SequenceGenerator(name = GENERATOR, sequenceName = "public.sq_tb_recurso")
    @Id
    @GeneratedValue(generator = GENERATOR)
    @Column(name = "id_recurso", unique = true, nullable = false)
    public int getIdRecurso() {
        return this.idRecurso;
    }

    public void setIdRecurso(int idRecurso) {
        this.idRecurso = idRecurso;
    }

    @Column(name = "ds_nome", length=DESCRICAO_PADRAO)
    @Size(max=DESCRICAO_PADRAO)
    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "ds_identificador", length = DESCRICAO_PADRAO)
    @Size(max=DESCRICAO_PADRAO)
    @NotNull
    public String getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
    
    @Override
    public String toString() {
        if (this.nome == null) {
            return this.identificador;
        }
        return this.nome;
    }

    @Transient
    public boolean getAtivo() {
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Recurso)) {
            return false;
        }
        Recurso other = (Recurso) obj;
        if (getIdRecurso() != other.getIdRecurso()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdRecurso();
        return result;
    }
}