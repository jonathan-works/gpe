package br.com.infox.epp.processo.documento.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = "tb_pasta_compartilhamento_hist")
public class PastaCompartilhamentoHistorico implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String GENERATOR_NAME = "PastaCompartilhamentoHistoricoGenerator";
    private static final String SEQUENCE_NAME = "sq_pasta_compartilhamento_hist";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_pasta_compartilhamento_hist")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasta_compartilhamento")
    private PastaCompartilhamento pastaCompartilhamento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_login")
    private UsuarioLogin usuarioLogin;

    @NotNull
    @Column(name = "in_acao_adicao")
    private Boolean acaoAdicao;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_acao")
    private Date dataAcao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PastaCompartilhamento getPastaCompartilhamento() {
        return pastaCompartilhamento;
    }

    public void setPastaCompartilhamento(PastaCompartilhamento pastaCompartilhamento) {
        this.pastaCompartilhamento = pastaCompartilhamento;
    }

    public UsuarioLogin getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public Boolean getAcaoAdicao() {
        return acaoAdicao;
    }

    public void setAcaoAdicao(Boolean acaoAdicao) {
        this.acaoAdicao = acaoAdicao;
    }

    public Date getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(Date dataAcao) {
        this.dataAcao = dataAcao;
    }
}
