package br.com.infox.epp.log;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import br.com.infox.epp.system.annotation.Ignore;

@Ignore
@Entity
@Table(name = "tb_log_erro")
public class LogErro {
    
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "LogErroGenerator", sequenceName = "sq_log_erro")
    @GeneratedValue(generator = "LogErroGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_log_erro", nullable = false, unique = true)
    private Long id;
    
    @NotNull
    @Column(name = "cd_log_erro", nullable = false)
    private String codigo;
    
    @NotNull
    @Column(name = "nm_instancia_servidor", nullable = false)
    private String instancia;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_erro_log", nullable = false)
    private Date data;
    
    @NotNull
    @Column(name = "ds_stacktrace", nullable = false)
    private String stacktrace;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "st_envio_log", nullable = false)
    private StatusLog status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_envio")
    private Date dataEnvio;
    
    @Column(name = "ds_erro_envio")
    private String erroEnvio;
    
    @PrePersist
    private void prePersist() {
        if (data == null) {
            data = DateTime.now().toDate();
        }
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

    public String getInstancia() {
        return instancia;
    }

    public void setInstancia(String instancia) {
        this.instancia = instancia;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }
    
    public StatusLog getStatus() {
        return status;
    }

    public void setStatus(StatusLog status) {
        this.status = status;
    }
    
    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getErroEnvio() {
        return erroEnvio;
    }

    public void setErroEnvio(String erroEnvio) {
        this.erroEnvio = erroEnvio;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof LogErro))
            return false;
        LogErro other = (LogErro) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }
    

}
