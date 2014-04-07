package br.com.infox.epp.access.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.NOME_PADRAO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.PapelQuery.IDENTIFICADOR;
import static br.com.infox.epp.access.query.PapelQuery.ID_PAPEL;
import static br.com.infox.epp.access.query.PapelQuery.NOME_PAPEL;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_IDENTIFICADORES;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_IDENTIFICADORES_QUERY;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_LOCALIZACAO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_LOCALIZACAO_QUERY;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO_QUERY;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO_QUERY;
import static br.com.infox.epp.access.query.PapelQuery.PAPEL_BY_IDENTIFICADOR;
import static br.com.infox.epp.access.query.PapelQuery.PAPEL_BY_IDENTIFICADOR_QUERY;
import static br.com.infox.epp.access.query.PapelQuery.PERMISSOES_BY_PAPEL;
import static br.com.infox.epp.access.query.PapelQuery.PERMISSOES_BY_PAPEL_QUERY;
import static br.com.infox.epp.access.query.PapelQuery.SEQUENCE_PAPEL;
import static br.com.infox.epp.access.query.PapelQuery.TABLE_PAPEL;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ForeignKey;
import org.jboss.seam.annotations.security.management.RoleGroups;
import org.jboss.seam.annotations.security.management.RoleName;

import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.seam.util.ComponentUtil;

@Entity
@Table(name = TABLE_PAPEL, uniqueConstraints = @UniqueConstraint(columnNames = IDENTIFICADOR))
@NamedQueries({
    @NamedQuery(name = PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO, query = PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO_QUERY),
    @NamedQuery(name = PAPEL_BY_IDENTIFICADOR, query = PAPEL_BY_IDENTIFICADOR_QUERY),
    @NamedQuery(name = PAPEIS_BY_IDENTIFICADORES, query = PAPEIS_BY_IDENTIFICADORES_QUERY),
    @NamedQuery(name = PAPEIS_BY_LOCALIZACAO, query = PAPEIS_BY_LOCALIZACAO_QUERY),
    @NamedQuery(name = PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO, query = PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO_QUERY) })
@NamedNativeQueries({ @NamedNativeQuery(name = PERMISSOES_BY_PAPEL, query = PERMISSOES_BY_PAPEL_QUERY) })
public class Papel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idPapel;
    private String nome;
    private String identificador;

    private List<Papel> grupos;

    public Papel() {
    }

    public Papel(final String nome, final String identificador) {
        this.nome = nome;
        this.identificador = identificador;
    }

    @SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_PAPEL)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_PAPEL, unique = true, nullable = false)
    public int getIdPapel() {
        return this.idPapel;
    }

    public void setIdPapel(int idPerfil) {
        this.idPapel = idPerfil;
    }

    @Column(name = NOME_PAPEL, length = NOME_PADRAO)
    @Size(max = NOME_PADRAO)
    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = IDENTIFICADOR, length = DESCRICAO_PADRAO)
    @Size(max = DESCRICAO_PADRAO)
    @NotNull
    @RoleName
    public String getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    @RoleGroups
    @ManyToMany
    @JoinTable(name = "tb_papel_grupo", joinColumns = @JoinColumn(name = "id_papel"), inverseJoinColumns = @JoinColumn(name = "membro_do_grupo"))
    @ForeignKey(name = "tb_papel_grupo_papel_fk", inverseName = "tb_papel_grupo_membro_fk")
    @OrderBy("nome")
    public List<Papel> getGrupos() {
        return this.grupos;
    }

    public void setGrupos(List<Papel> grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        if (this.nome == null) {
            return this.identificador;
        }
        return this.nome;
    }

    @Transient
    public boolean getAtivo() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Papel)) {
            return false;
        }
        Papel other = (Papel) obj;
        if (getIdPapel() != other.getIdPapel()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdPapel();
        return result;
    }

    @Transient
    public List<String> getRecursos() {
        PapelDAO pd = ComponentUtil.getComponent(PapelDAO.NAME);
        return pd.getListaPermissoes(this);
    }
}
