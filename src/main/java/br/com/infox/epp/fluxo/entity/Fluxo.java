/*
 * Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob
 * os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela Free
 * Software Foundation; versão 2 da Licença. Este programa é distribuído na
 * expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE
 * ESPECÍFICA.
 * 
 * Consulte a GNU GPL para mais detalhes. Você deve ter recebido uma cópia da
 * GNU GPL junto com este programa; se não, veja em http://www.gnu.org/licenses/
 */
package br.com.infox.epp.fluxo.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.constants.LengthConstants.DESCRICAO_PEQUENA;
import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.FluxoQuery.CODIGO_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_FLUXO_BY_CODIGO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_FLUXO_BY_CODIGO_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_FLUXO_BY_DESCRICAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_FLUXO_BY_DESCRICAO_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_PROCESSOS_ATRASADOS;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_PROCESSOS_ATRASADOS_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_PROCESSOS_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_PROCESSOS_BY_FLUXO_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.DATA_FIM_PUBLICACAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.DATA_INICIO_PUBLICACAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.DESCRICAO_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_CODIGO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_CODIGO_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_DESCRICACAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_DESCRICAO_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_NOME;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_NOME_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.ID_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.ID_USUARIO_PUBLICACAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.LIST_ATIVOS;
import static br.com.infox.epp.fluxo.query.FluxoQuery.LIST_ATIVOS_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoQuery.PRAZO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.PUBLICADO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.SEQUENCE_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.TABLE_FLUXO;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.hibernate.util.HibernateUtil;

@Entity
@Table(name = TABLE_FLUXO, uniqueConstraints = {
        @UniqueConstraint(columnNames = { DESCRICAO_FLUXO }),
        @UniqueConstraint(columnNames = { CODIGO_FLUXO }) })
@NamedQueries(value = {
        @NamedQuery(name = LIST_ATIVOS, query = LIST_ATIVOS_QUERY),
        @NamedQuery(name = COUNT_PROCESSOS_ATRASADOS, query = COUNT_PROCESSOS_ATRASADOS_QUERY),
        @NamedQuery(name = FLUXO_BY_DESCRICACAO, query = FLUXO_BY_DESCRICAO_QUERY),
        @NamedQuery(name = COUNT_PROCESSOS_BY_FLUXO, query = COUNT_PROCESSOS_BY_FLUXO_QUERY),
        @NamedQuery(name = FLUXO_BY_NOME, query = FLUXO_BY_NOME_QUERY),
        @NamedQuery(name = COUNT_FLUXO_BY_CODIGO, query = COUNT_FLUXO_BY_CODIGO_QUERY),
        @NamedQuery(name = COUNT_FLUXO_BY_DESCRICAO, query = COUNT_FLUXO_BY_DESCRICAO_QUERY),
        @NamedQuery(name = FLUXO_BY_CODIGO, query = FLUXO_BY_CODIGO_QUERY) })
public class Fluxo implements Serializable {

    private static final long serialVersionUID = 1L;

    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR, sequenceName = SEQUENCE_FLUXO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_FLUXO, unique = true, nullable = false)
    private Integer idFluxo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_USUARIO_PUBLICACAO)
    private UsuarioLogin usuarioPublicacao;
    
    @Column(name = CODIGO_FLUXO, length = DESCRICAO_PEQUENA, nullable = false)
    @Size(min = FLAG, max = DESCRICAO_PEQUENA)
    @NotNull
    private String codFluxo;
    
    @Column(name = DESCRICAO_FLUXO, nullable = false, length = DESCRICAO_PADRAO, unique = true)
    @Size(min = FLAG, max = DESCRICAO_PADRAO)
    @NotNull
    private String fluxo;
    
    @Column(name = ATIVO, nullable = false)
    @NotNull
    private Boolean ativo;
    
    @Column(name = PRAZO, nullable = true)
    @NotNull
    private Integer qtPrazo;
    
    @Column(name = PUBLICADO, nullable = false)
    @NotNull
    private Boolean publicado;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = DATA_INICIO_PUBLICACAO, nullable = false)
    @NotNull
    private Date dataInicioPublicacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = DATA_FIM_PUBLICACAO)
    private Date dataFimPublicacao;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = FLUXO_ATTRIBUTE)
    private List<FluxoPapel> fluxoPapelList = new ArrayList<FluxoPapel>(0);
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = FLUXO_ATTRIBUTE)
    private List<ModeloPasta> modeloPastaList = new ArrayList<>(0); 
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxo")
    private List<DefinicaoProcesso> definicaoProcesso = new ArrayList<>();

    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "tb_fluxo_status_processo", 
            joinColumns=@JoinColumn(name="id_fluxo", referencedColumnName="id_fluxo"),
            inverseJoinColumns=@JoinColumn(name="id_status_processo", referencedColumnName="id_status_processo"))
    @OrderBy(value = "nm_status_processo")
	private Set<StatusProcesso> statusProcessos = new HashSet<>(1);
    
    public Fluxo() {
    }

    public Fluxo(final String codFluxo, final String fluxo,
            final Integer qtPrazo, final Date dataInicioPublicacao,
            final Boolean publicado, final Boolean ativo) {
        this.codFluxo = codFluxo;
        this.fluxo = fluxo;
        this.ativo = ativo;
        this.qtPrazo = qtPrazo;
        this.publicado = publicado;
        this.dataInicioPublicacao = dataInicioPublicacao;
    }

    public Fluxo(final String codFluxo, final String fluxo,
            final Integer qtPrazo, final Date dataInicioPublicacao,
            final Date dataFimPublicacao, final Boolean publicado,
            final Boolean ativo) {
        this(codFluxo, fluxo, qtPrazo, dataInicioPublicacao, publicado, ativo);
        this.dataFimPublicacao = dataFimPublicacao;
    }

    public Integer getIdFluxo() {
        return this.idFluxo;
    }

    public void setIdFluxo(final Integer idFluxo) {
        this.idFluxo = idFluxo;
    }

    public UsuarioLogin getUsuarioPublicacao() {
        return this.usuarioPublicacao;
    }

    public void setUsuarioPublicacao(final UsuarioLogin usuarioPublicacao) {
        this.usuarioPublicacao = usuarioPublicacao;
    }

    public String getCodFluxo() {
        return this.codFluxo;
    }

    public void setCodFluxo(final String codFluxo) {
        this.codFluxo = codFluxo;
        if (codFluxo != null) {
            this.codFluxo = codFluxo.trim();
        }
    }

    public String getFluxo() {
        return this.fluxo;
    }

    public void setFluxo(final String fluxo) {
        this.fluxo = fluxo;
        if (fluxo != null) {
            this.fluxo = fluxo.trim();
        }
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(final Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getQtPrazo() {
        return this.qtPrazo;
    }

    public void setQtPrazo(final Integer qtPrazo) {
        this.qtPrazo = qtPrazo;
    }

    public Boolean getPublicado() {
        return this.publicado;
    }

    public void setPublicado(final Boolean publicado) {
        this.publicado = publicado;
    }

    public Date getDataInicioPublicacao() {
        return this.dataInicioPublicacao;
    }

    public void setDataInicioPublicacao(final Date dataInicioPublicacao) {
        this.dataInicioPublicacao = dataInicioPublicacao;
    }

    public Date getDataFimPublicacao() {
        return this.dataFimPublicacao;
    }

    public void setDataFimPublicacao(final Date dataFimPublicacao) {
        this.dataFimPublicacao = dataFimPublicacao;
    }

    @Override
    public String toString() {
        return fluxo;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Fluxo)) {
            return false;
        }
        final Fluxo other = (Fluxo) HibernateUtil.removeProxy(obj);
        if (!getIdFluxo().equals(other.getIdFluxo())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (idFluxo == null ? 0 : idFluxo.hashCode());
        return result;
    }

    public void setFluxoPapelList(final List<FluxoPapel> fluxoPapelList) {
        this.fluxoPapelList = fluxoPapelList;
    }

    public List<FluxoPapel> getFluxoPapelList() {
        return fluxoPapelList;
    }

    public void setModeloPastaList(List<ModeloPasta> modeloPastaList) {
        this.modeloPastaList = modeloPastaList;
    }
    
    public List<ModeloPasta> getModeloPastaList() {
        return modeloPastaList;
    }
    
    public DefinicaoProcesso getDefinicaoProcesso() {
        return definicaoProcesso.isEmpty() ? null : definicaoProcesso.get(0);
    }
    
    public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
        this.definicaoProcesso.clear();
        this.definicaoProcesso.add(definicaoProcesso);
    }
    
    @Transient
    public String getDataInicioFormatada() {
        return DateFormat.getDateInstance().format(dataInicioPublicacao);
    }

    @Transient
    public String getDataFimFormatada() {
        if (dataFimPublicacao != null) {
            return DateFormat.getDateInstance().format(dataFimPublicacao);
        } else {
            return "";
        }
    }
    
    public Fluxo makeCopy() {
    	Fluxo fluxo = new Fluxo();
    	fluxo.setAtivo(getAtivo());
    	fluxo.setDataFimPublicacao(getDataFimPublicacao());
    	fluxo.setDataInicioPublicacao(getDataInicioPublicacao());
    	fluxo.setPublicado(false);
    	fluxo.setQtPrazo(getQtPrazo());
    	return fluxo;
    }
}
