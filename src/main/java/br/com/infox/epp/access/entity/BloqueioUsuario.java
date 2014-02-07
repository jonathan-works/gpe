package br.com.infox.epp.access.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.BLOQUEIO_MAIS_RECENTE;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.BLOQUEIO_MAIS_RECENTE_QUERY;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.DATA_BLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.DATA_DESBLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.DATA_PREVISAO_DESBLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.ID_BLOQUEIO_USUARIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.ID_USUARIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.MOTIVO_BLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.SAVE_DATA_DESBLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.SAVE_DATA_DESBLOQUEIO_QUERY;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.SEQUENCE_BLOQUEIO_USUARIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.TABLE_BLOQUEIO_USUARIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.UNDO_BLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.UNDO_BLOQUEIO_NATIVE_QUERY;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

/**
 * BloqueioUsuario generated by hbm2java
 */
@Entity
@Table(name = TABLE_BLOQUEIO_USUARIO, schema = PUBLIC)
@NamedQueries(value = {
    @NamedQuery(name = SAVE_DATA_DESBLOQUEIO,
            query = SAVE_DATA_DESBLOQUEIO_QUERY),
    @NamedQuery(name = BLOQUEIO_MAIS_RECENTE,
            query = BLOQUEIO_MAIS_RECENTE_QUERY) })
@NamedNativeQueries(value = { @NamedNativeQuery(name = UNDO_BLOQUEIO,
        query = UNDO_BLOQUEIO_NATIVE_QUERY) })
public class BloqueioUsuario implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idBloqueioUsuario;
    private UsuarioLogin usuario;
    private Date dataBloqueio;
    private Date dataPrevisaoDesbloqueio;
    private String motivoBloqueio;
    private Date dataDesbloqueio;

    public BloqueioUsuario() {
    }

    public BloqueioUsuario(final Date dataPrevisaoDesbloqueio,
            final String motivoBloqueio) {
        this.dataPrevisaoDesbloqueio = dataPrevisaoDesbloqueio;
        this.motivoBloqueio = motivoBloqueio;
    }

    @SequenceGenerator(name = GENERATOR,
            sequenceName = SEQUENCE_BLOQUEIO_USUARIO)
    @Id
    @GeneratedValue(generator = GENERATOR)
    @Column(name = ID_BLOQUEIO_USUARIO, unique = true, nullable = false)
    public Integer getIdBloqueioUsuario() {
        return this.idBloqueioUsuario;
    }

    public void setIdBloqueioUsuario(Integer idBloqueioUsuario) {
        this.idBloqueioUsuario = idBloqueioUsuario;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_USUARIO, nullable = false)
    @NotNull
    public UsuarioLogin getUsuario() {
        return this.usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    @Temporal(TIMESTAMP)
    @Column(name = DATA_BLOQUEIO, nullable = false)
    @NotNull
    public Date getDataBloqueio() {
        return this.dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    @Temporal(TIMESTAMP)
    @Column(name = DATA_PREVISAO_DESBLOQUEIO, nullable = true)
    @Future
    public Date getDataPrevisaoDesbloqueio() {
        return this.dataPrevisaoDesbloqueio;
    }

    public void setDataPrevisaoDesbloqueio(Date dataPrevisaoDesbloqueio) {
        this.dataPrevisaoDesbloqueio = dataPrevisaoDesbloqueio;
    }

    @Column(name = MOTIVO_BLOQUEIO, nullable = false,
            length = LengthConstants.DESCRICAO_ENTIDADE)
    @NotNull
    @Size(min = 1, max = LengthConstants.DESCRICAO_ENTIDADE)
    public String getMotivoBloqueio() {
        return this.motivoBloqueio;
    }

    public void setMotivoBloqueio(String motivoBloqueio) {
        this.motivoBloqueio = motivoBloqueio;
    }

    @Temporal(TIMESTAMP)
    @Column(name = DATA_DESBLOQUEIO)
    public Date getDataDesbloqueio() {
        return this.dataDesbloqueio;
    }

    public void setDataDesbloqueio(Date dataDesbloqueio) {
        this.dataDesbloqueio = dataDesbloqueio;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getIdBloqueioUsuario() == null) {
            return false;
        }
        if (!(obj instanceof BloqueioUsuario)) {
            return false;
        }
        BloqueioUsuario other = (BloqueioUsuario) obj;
        return getIdBloqueioUsuario().equals(other.getIdBloqueioUsuario());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        int id = this.idBloqueioUsuario == null ? 0 : this.idBloqueioUsuario;
        result = prime * result + id;
        return result;
    }
}
