package br.com.infox.epp.mail.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.mail.query.ListaEmailQuery.ID_ESTRUTURA;
import static br.com.infox.epp.mail.query.ListaEmailQuery.ID_GRUPO_EMAIL;
import static br.com.infox.epp.mail.query.ListaEmailQuery.ID_LISTA_EMAIL;
import static br.com.infox.epp.mail.query.ListaEmailQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.mail.query.ListaEmailQuery.ID_PAPEL;
import static br.com.infox.epp.mail.query.ListaEmailQuery.LISTA_EMAIL_BY_ID_GRUPO;
import static br.com.infox.epp.mail.query.ListaEmailQuery.LISTA_EMAIL_BY_ID_GRUPO_QUERY;
import static br.com.infox.epp.mail.query.ListaEmailQuery.MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL;
import static br.com.infox.epp.mail.query.ListaEmailQuery.MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL_QUERY;
import static br.com.infox.epp.mail.query.ListaEmailQuery.RESOLVE_LISTA_EMAIL_BY_ID_GRUPO;
import static br.com.infox.epp.mail.query.ListaEmailQuery.RESOLVE_LISTA_EMAIL_BY_ID_GRUPO_QUERY;
import static br.com.infox.epp.mail.query.ListaEmailQuery.SEQUENCE_LISTA_EMAIL;
import static br.com.infox.epp.mail.query.ListaEmailQuery.TABLE_LISTA_EMAIL;

import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;

@Entity
@Table(name = TABLE_LISTA_EMAIL)
@NamedQueries({
    @NamedQuery(name = LISTA_EMAIL_BY_ID_GRUPO, query = LISTA_EMAIL_BY_ID_GRUPO_QUERY),
    @NamedQuery(name = RESOLVE_LISTA_EMAIL_BY_ID_GRUPO, query = RESOLVE_LISTA_EMAIL_BY_ID_GRUPO_QUERY),
    @NamedQuery(name = MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL, query = MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL_QUERY) })
public class ListaEmail implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idListaEmail;
    private int idGrupoEmail;
    private Localizacao localizacao;
    private Papel papel;
    private Localizacao estrutura;

    public ListaEmail() {
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_LISTA_EMAIL)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_LISTA_EMAIL, unique = true, nullable = false)
    public int getIdListaEmail() {
        return this.idListaEmail;
    }

    public void setIdListaEmail(int id) {
        this.idListaEmail = id;
    }

    @Column(name = ID_GRUPO_EMAIL, unique = false, nullable = false)
    public int getIdGrupoEmail() {
        return this.idGrupoEmail;
    }

    public void setIdGrupoEmail(int id) {
        this.idGrupoEmail = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_LOCALIZACAO)
    public Localizacao getLocalizacao() {
        return this.localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_PAPEL)
    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_ESTRUTURA)
    public Localizacao getEstrutura() {
        return estrutura;
    }

    public void setEstrutura(Localizacao estrutura) {
        this.estrutura = estrutura;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}:{1}-{2}{3}/{4}", idListaEmail, idGrupoEmail, (estrutura == null ? "" : estrutura
                + "/"), localizacao, papel);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ListaEmail)) {
            return false;
        }
        ListaEmail other = (ListaEmail) obj;
        if (getIdListaEmail() != other.getIdListaEmail()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdListaEmail();
        return result;
    }
}
