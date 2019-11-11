package br.com.infox.epp.calendario.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.calendario.TipoSerie;

@Entity
@Table(name = "tb_serie_eventos")
public class SerieEventos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "SerieEventosGenerator", sequenceName = "sq_serie_eventos")
    @GeneratedValue(generator = "SerieEventosGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_serie_eventos", unique = true, nullable = false)
    private Integer id;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "tp_serie", nullable = false)
    private TipoSerie tipo;

    public SerieEventos() {
    }
    public SerieEventos(SerieEventos serie) {
        this.setTipo(serie.getTipo());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoSerie getTipo() {
        return tipo;
    }

    public void setTipo(TipoSerie tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SerieEventos)) {
            return false;
        }
        SerieEventos other = (SerieEventos) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
