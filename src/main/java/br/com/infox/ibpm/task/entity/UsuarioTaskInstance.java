package br.com.infox.ibpm.task.entity;

import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.LOCALIZACAO_DA_TAREFA;
import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.LOCALIZACAO_DA_TAREFA_QUERY;
import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.USUARIO_DA_TAREFA;
import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.USUARIO_DA_TAREFA_QUERY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = UsuarioTaskInstance.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = LOCALIZACAO_DA_TAREFA, query = LOCALIZACAO_DA_TAREFA_QUERY)
})
@NamedNativeQueries({ 
    @NamedNativeQuery(name = USUARIO_DA_TAREFA, query = USUARIO_DA_TAREFA_QUERY)
})
public class UsuarioTaskInstance implements Serializable {

    public static final String TABLE_NAME = "tb_usuario_taskinstance";
    private static final long serialVersionUID = 1L;

    private Long idTaskInstance;
    private UsuarioLogin usuario;
    private Localizacao localizacao;
    private Papel papel;

    public UsuarioTaskInstance() {
    }

    public UsuarioTaskInstance(final Long idTaskinstance,
            final UsuarioLogin usuario, final Localizacao localizacao,
            final Papel papel) {
        this.idTaskInstance = idTaskinstance;
        this.usuario = usuario;
        this.localizacao = localizacao;
        this.papel = papel;
    }

    @Id
    @Column(name = "id_taskinstance", unique = true, nullable = false)
    public Long getIdTaskInstance() {
        return idTaskInstance;
    }

    public void setIdTaskInstance(Long idTaskInstance) {
        this.idTaskInstance = idTaskInstance;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_login", nullable = false)
    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_papel", nullable = false)
    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_localizacao", nullable = false)
    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
}
