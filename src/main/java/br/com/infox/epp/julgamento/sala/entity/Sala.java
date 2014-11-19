package br.com.infox.epp.julgamento.sala.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="tb_sala")
public class Sala implements Serializable {

    public Long idSala;
    
    
    
    
    private static final long serialVersionUID = 1L;

}
