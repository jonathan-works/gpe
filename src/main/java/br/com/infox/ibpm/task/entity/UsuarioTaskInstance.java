package br.com.infox.ibpm.task.entity;

import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.LOCALIZACAO_DA_TAREFA;
import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.LOCALIZACAO_DA_TAREFA_QUERY;
import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.USUARIO_DA_TAREFA;
import static br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery.*;

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
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Entity
@Table(name = UsuarioTaskInstance.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = LOCALIZACAO_DA_TAREFA, query = LOCALIZACAO_DA_TAREFA_QUERY),
    @NamedQuery(name = LOCALIZACOES_DO_PROCESSO, query = LOCALIZACOES_DO_PROCESSO_QUERY)
})
@NamedNativeQueries({ 
    @NamedNativeQuery(name = USUARIO_DA_TAREFA, query = USUARIO_DA_TAREFA_QUERY)
})
public class UsuarioTaskInstance implements Serializable {

    public static final String TABLE_NAME = "tb_usuario_taskinstance";
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_taskinstance", unique = true, nullable = false)
    private Long idTaskInstance;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_login", nullable = false)
    private UsuarioLogin usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao_externa", nullable = true, unique=false)
    private Localizacao localizacaoExterna;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_localizacao", nullable = false)
    private Localizacao localizacao;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_papel", nullable = false)
    private Papel papel;

    public UsuarioTaskInstance() {
    }

    public UsuarioTaskInstance(Long idTaskinstance, UsuarioPerfil usuarioPerfil) {
        this.idTaskInstance = idTaskinstance;
        this.usuario = usuarioPerfil.getUsuarioLogin();
        this.localizacaoExterna = usuarioPerfil.getLocalizacao();
        this.localizacao = usuarioPerfil.getPerfilTemplate().getLocalizacao();
        this.papel = usuarioPerfil.getPerfilTemplate().getPapel();
    }
    
    public Long getIdTaskInstance() {
        return idTaskInstance;
    }

    public void setIdTaskInstance(Long idTaskInstance) {
        this.idTaskInstance = idTaskInstance;
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

	public Localizacao getLocalizacaoExterna() {
		return localizacaoExterna;
	}

	public void setLocalizacaoExterna(Localizacao localizacaoExterna) {
		this.localizacaoExterna = localizacaoExterna;
	}
}
