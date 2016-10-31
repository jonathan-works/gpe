package br.com.infox.epp.twitter.entity;

import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_ID_USUARIO;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_ID_USUARIO_QUERY;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_LOCALIZACAO;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_LOCALIZACAO_QUERY;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_USUARIO;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_USUARIO_QUERY;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.LIST_TWITTER_BY_ID_GRUPO_EMAIL;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.LIST_TWITTER_BY_ID_GRUPO_EMAIL_QUERY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.twitter.type.TipoTwitterEnum;
import twitter4j.auth.AccessToken;

@Entity
@Table(name = ContaTwitter.NAME)
@NamedQueries({
    @NamedQuery(name = CONTA_TWITTER_BY_USUARIO, query = CONTA_TWITTER_BY_USUARIO_QUERY),
    @NamedQuery(name = CONTA_TWITTER_BY_ID_USUARIO, query = CONTA_TWITTER_BY_ID_USUARIO_QUERY),
    @NamedQuery(name = LIST_TWITTER_BY_ID_GRUPO_EMAIL, query = LIST_TWITTER_BY_ID_GRUPO_EMAIL_QUERY),
    @NamedQuery(name = CONTA_TWITTER_BY_LOCALIZACAO, query = CONTA_TWITTER_BY_LOCALIZACAO_QUERY) })
public class ContaTwitter implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tb_conta_twitter";

    private long idTwitter;
    private String screenName;
    private String usuarioToken;
    private String usuarioSecretToken;
    private TipoTwitterEnum tipoTwitter;
    private UsuarioLogin usuario;
    private Localizacao localizacao;

    @Id
    @Column(name = "id_twitter", nullable = false, unique = true)
    public Long getIdTwitter() {
        return idTwitter;
    }

    public void setIdTwitter(Long idTwitter) {
        this.idTwitter = idTwitter;
    }

    @Column(name = "ds_screen_name", nullable = false)
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Column(name = "ds_token")
    public String getUsuarioToken() {
        return usuarioToken;
    }

    public void setUsuarioToken(String usuarioToken) {
        this.usuarioToken = usuarioToken;
    }

    @Column(name = "ds_secret_token")
    public String getUsuarioSecretToken() {
        return usuarioSecretToken;
    }

    public void setUsuarioSecretToken(String usuarioSecretToken) {
        this.usuarioSecretToken = usuarioSecretToken;
    }

    @Column(name = "tp_conta_twitter", nullable = false, columnDefinition = "varchar(1)", length = LengthConstants.FLAG)
    @Enumerated(EnumType.STRING)
    public TipoTwitterEnum getTipoTwitter() {
        return tipoTwitter;
    }

    public void setTipoTwitter(TipoTwitterEnum tipoTwitter) {
        this.tipoTwitter = tipoTwitter;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = true)
    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao", nullable = true)
    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @Transient
    public AccessToken getAccessToken() {
        return new AccessToken(usuarioToken, usuarioSecretToken);
    }

    public void setAccessToken(AccessToken accessToken) {
        idTwitter = accessToken.getUserId();
        screenName = accessToken.getScreenName();
        usuarioToken = accessToken.getToken();
        usuarioSecretToken = accessToken.getTokenSecret();
    }

}
