package br.com.infox.epp.ajuda.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.ajuda.query.HistoricoAjudaQuery.DATA_REGISTRO;
import static br.com.infox.epp.ajuda.query.HistoricoAjudaQuery.ID_HISTORICO_AJUDA;
import static br.com.infox.epp.ajuda.query.HistoricoAjudaQuery.ID_PAGINA;
import static br.com.infox.epp.ajuda.query.HistoricoAjudaQuery.ID_USUARIO;
import static br.com.infox.epp.ajuda.query.HistoricoAjudaQuery.SEQUENCE_HISTORICO_AJUDA;
import static br.com.infox.epp.ajuda.query.HistoricoAjudaQuery.TABLE_HISTORICO_AJUDA;
import static br.com.infox.epp.ajuda.query.HistoricoAjudaQuery.TEXTO;

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

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.search.annotations.Analyzer;

import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = TABLE_HISTORICO_AJUDA)
@Analyzer(impl = BrazilianAnalyzer.class)
public class HistoricoAjuda implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idHistoricoAjuda;
    private Date dataRegistro;
    private String texto;
    private Pagina pagina;
    private UsuarioLogin usuario;

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_HISTORICO_AJUDA)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_HISTORICO_AJUDA, unique = true, nullable = false)
    public Integer getIdHistoricoAjuda() {
        return idHistoricoAjuda;
    }

    public void setIdHistoricoAjuda(Integer idHistoricoAjuda) {
        this.idHistoricoAjuda = idHistoricoAjuda;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = DATA_REGISTRO, nullable = false, length = 0)
    @NotNull
    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Column(name = TEXTO)
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_PAGINA, nullable = false)
    @NotNull
    public Pagina getPagina() {
        return pagina;
    }

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_USUARIO, nullable = false)
    @NotNull
    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

}
