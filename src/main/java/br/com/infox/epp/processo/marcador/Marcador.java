package br.com.infox.epp.processo.marcador;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@Entity
@Table(name = "tb_marcador")
public class Marcador implements Serializable {

    private static final long serialVersionUID = 4127549369863376217L;
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "MarcadorGenerator", sequenceName = "sq_marcador")
    @GeneratedValue(generator = "MarcadorGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_marcador", nullable = false)
    private Long id;
    
    @NotNull
    @Size(max = 255)
    @Column(name = "cd_marcador")   
    private String codigo;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "tb_marcador_documento_bin", 
            joinColumns=@JoinColumn(name="id_marcador", referencedColumnName="id_marcador"),
            inverseJoinColumns=@JoinColumn(name="id_documento_bin", referencedColumnName="id_documento_bin"))
    private Set<DocumentoBin> documentoBinList = new HashSet<>(1);
    
    public Marcador() {
    }
    
    public Marcador(String codigo) {
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public Set<DocumentoBin> getDocumentoBinList() {
        return documentoBinList;
    }

    public void setDocumentoBinList(Set<DocumentoBin> documentoBinList) {
        this.documentoBinList = documentoBinList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Marcador))
            return false;
        Marcador other = (Marcador) obj;
        if (getCodigo() == null) {
            if (other.getCodigo() != null)
                return false;
        } else if (!getCodigo().equals(other.getCodigo()))
            return false;
        return true;
    }

}
