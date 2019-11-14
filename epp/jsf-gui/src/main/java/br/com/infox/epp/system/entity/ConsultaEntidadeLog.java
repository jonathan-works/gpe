package br.com.infox.epp.system.entity;

import java.io.Serializable;
import java.util.Date;

import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.system.type.TipoOperacaoLogEnum;

public class ConsultaEntidadeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;
    private String nomeEntidade;
    private Date dataInicio;
    private Date dataFim;
    private UsuarioLogin usuario;
    private TipoOperacaoLogEnum tipoOperacaoLogEnum = null;
    private Boolean inPesquisa = false;

    public ConsultaEntidadeLog() {
        setDataFim(new Date());
        setDataInicio(this.dataFim);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNomeEntidade() {
        return nomeEntidade;
    }

    public void setNomeEntidade(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    public void setTipoOperacaoLogEnum(TipoOperacaoLogEnum tipoOperacaoLogEnum) {
        this.tipoOperacaoLogEnum = tipoOperacaoLogEnum;
    }

    public TipoOperacaoLogEnum getTipoOperacaoLogEnum() {
        return tipoOperacaoLogEnum;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = DateUtil.getBeginningOfDay(dataInicio);
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = DateUtil.getEndOfDay(dataFim);
    }

    public Boolean getInPesquisa() {
        return inPesquisa;
    }

    public void setInPesquisa(Boolean inPesquisa) {
        this.inPesquisa = inPesquisa;
    }

    @Override
    public String toString() {
        return nomeEntidade;
    }
}
