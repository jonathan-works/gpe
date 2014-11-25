package br.com.infox.epp.julgamento.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@Entity
@Table(name = "tb_sala")
public class Sala implements Serializable {

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name="SalaGenerator", sequenceName="sq_sala")
    @GeneratedValue(generator = "SalaGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name="id_sala", unique = true, nullable = false)
    private Long idSala;
    @NotNull
    @Size(min=1,max=LengthConstants.DESCRICAO_MEDIA)
    @Column(name = "nm_sala", nullable = false, unique=true, length=LengthConstants.DESCRICAO_MEDIA)
    private String nome;
    @NotNull
    @Size(min=1,max=LengthConstants.DESCRICAO_MEDIA)
    @Column(name = "ds_endereco", nullable = false, unique=true, length=LengthConstants.DESCRICAO_MEDIA)
    private String endereco;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_uni_decisora_colegiada", nullable=false)
    private UnidadeDecisoraColegiada unidadeDecisoraColegiada;
    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;
    @NotNull
    @Column(name = "in_fora_expediente", nullable = false)
    private Boolean foraExpediente;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sala", cascade = {CascadeType.REMOVE})
    private List<SalaTurno> turnos;
    
    public Long getIdSala() {
        return idSala;
    }

    public void setIdSala(Long idSala) {
        this.idSala = idSala;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public UnidadeDecisoraColegiada getUnidadeDecisoraColegiada() {
        return unidadeDecisoraColegiada;
    }

    public void setUnidadeDecisoraColegiada(
            UnidadeDecisoraColegiada unidadeDecisoraColegiada) {
        this.unidadeDecisoraColegiada = unidadeDecisoraColegiada;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getForaExpediente() {
        return foraExpediente;
    }

    public void setForaExpediente(Boolean foraExpediente) {
        this.foraExpediente = foraExpediente;
    }

    public List<SalaTurno> getTurnos() {
        return turnos;
    }
    public void setTurnos(List<SalaTurno> turnos) {
        this.turnos = turnos;
    }
    
    @Transient
    public String getTurnosFormatado(){
        StringBuilder sb = new StringBuilder();
        for (SalaTurno salaTurno : this.getTurnos()) {
            sb.append(salaTurno.toString());
            sb.append(" ; ");
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idSala == null) ? 0 : idSala.hashCode());
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
        if (!(obj instanceof Sala)) {
            return false;
        }
        Sala other = (Sala) obj;
        if (idSala == null) {
            if (other.idSala != null) {
                return false;
            }
        } else if (!idSala.equals(other.idSala)) {
            return false;
        }
        return true;
    }

    private static final long serialVersionUID = 1L;

}
