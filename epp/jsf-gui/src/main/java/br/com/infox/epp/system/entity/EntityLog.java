package br.com.infox.epp.system.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.system.annotation.Ignore;
import br.com.infox.epp.system.type.TipoOperacaoLogEnum;

@Ignore
@Entity
@Table(name = "tb_log")
public class EntityLog implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idLog;
    private UsuarioLogin usuario;
    private String urlRequisicao;
    private String ip;
    private String nomeEntidade;
    private String nomePackage;
    private String idEntidade;
    private TipoOperacaoLogEnum tipoOperacao;
    private Date dataLog;
    private List<EntityLogDetail> entityLogDetailList = new ArrayList<EntityLogDetail>(0);

    public EntityLog() {
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_log")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_log", unique = true, nullable = false)
    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    public UsuarioLogin getUsuario() {
        return this.usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    @Column(name = "id_pagina", length = LengthConstants.ID_PAGINA)
    @Size(max = LengthConstants.ID_PAGINA)
    public String getUrlRequisicao() {
        return urlRequisicao;
    }

    public void setUrlRequisicao(String urlRequisicao) {
        this.urlRequisicao = urlRequisicao;
    }

    @Column(name = "ds_ip", length = LengthConstants.DESCRICAO_MINIMA)
    @Size(max = LengthConstants.DESCRICAO_MINIMA)
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column(name = "ds_entidade", length = LengthConstants.DESCRICAO_ENTIDADE)
    @Size(max = LengthConstants.DESCRICAO_ENTIDADE)
    public String getNomeEntidade() {
        return nomeEntidade;
    }

    public void setNomeEntidade(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    @Column(name = "ds_package", length = LengthConstants.DESCRICAO_PACOTE)
    @Size(max = LengthConstants.DESCRICAO_PACOTE)
    public String getNomePackage() {
        return nomePackage;
    }

    public void setNomePackage(String nomePackage) {
        this.nomePackage = nomePackage;
    }

    @Column(name = "ds_id_entidade", length = LengthConstants.ID_ENTIDADE)
    @Size(max = LengthConstants.ID_ENTIDADE)
    public String getIdEntidade() {
        return idEntidade;
    }

    public void setIdEntidade(String idEntidade) {
        this.idEntidade = idEntidade;
    }

    @Column(name = "tp_operacao")
    @Enumerated(EnumType.STRING)
    public TipoOperacaoLogEnum getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoLogEnum tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_log", nullable = false)
    @NotNull
    public Date getDataLog() {
        return dataLog;
    }

    public void setDataLog(Date dataLog) {
        this.dataLog = dataLog;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "entityLog")
    public List<EntityLogDetail> getLogDetalheList() {
        return entityLogDetailList;
    }

    public void setLogDetalheList(List<EntityLogDetail> logDetalheList) {
        this.entityLogDetailList = logDetalheList;
    }

    @Override
    public String toString() {
        return nomeEntidade;
    }

}
