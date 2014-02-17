package br.com.infox.epp.processo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.ID_RELACIONAMENTO;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.TABLE_NAME;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name=TABLE_NAME, schema=PUBLIC)
public class Relacionamento implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer idRelacionamento;
    
    @Id
    @GeneratedValue(generator=GENERATOR)
    @Column(name=ID_RELACIONAMENTO, unique=true, nullable=false)
    @SequenceGenerator(name=GENERATOR, sequenceName=SEQUENCE_NAME)
    public Integer getIdRelacionamento() {
        return this.idRelacionamento;
    }
    public void setIdRelacionamento(final Integer idRelacionamento) {
        this.idRelacionamento = idRelacionamento;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((idRelacionamento == null) ? 0 : idRelacionamento.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Relacionamento other = (Relacionamento) obj;
        if (idRelacionamento == null) {
            if (other.idRelacionamento != null) {
                return false;
            }
        } else if (!idRelacionamento.equals(other.idRelacionamento)) {
            return false;
        }
        return true;
    }

}