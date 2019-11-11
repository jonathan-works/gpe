package br.com.infox.ibpm.sinal;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;

@Named
@ViewScoped
public class SignalView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SignalService signalService;
    @Inject
    private SignalDao signalDao;
    @Inject
    private SignalList signalList;

    private String tab;

    private Long id;
    private String codigo;
    private String nome;
    private Boolean ativo;
    private Boolean sistema;
    
    @PostConstruct
    private void init() {
        newInstance();
    }

    @ExceptionHandled(value = MethodType.PERSIST)
    public void inserir() {
        Signal signal = new Signal();
        signal.setCodigo(getCodigo());
        signal.setNome(getNome());
        signal.setAtivo(getAtivo());
        signal.setSistema(sistema);
        signalService.persist(signal);
        setId(signal.getId());
    }

    @ExceptionHandled(value = MethodType.UPDATE)
    public void atualizar() {
        Signal signal = signalDao.findById(getId());
        signal.setNome(getNome());
        signal.setAtivo(getAtivo());
        signal.setSistema(getSistema());
        signalService.update(signal);
    }
    
    @ExceptionHandled(value = MethodType.UPDATE, updatedMessage = "Registro Inativado com sucesso!" )
    public void inativar() {
        Signal signal = signalDao.findById(getId());
        signal.setAtivo(Boolean.FALSE);
        signalService.update(signal);
    }

    public void load(Signal signal) {
        setId(signal.getId());
        setCodigo(signal.getCodigo());
        setNome(signal.getNome());
        setAtivo(signal.getAtivo());
        setSistema(signal.getSistema());
    }

    public void onClickFormTab() {
        newInstance();
    }

    public void onClickSearchTab() {
        signalList.refresh();
    }

    public void newInstance() {
        setId(null);
        setCodigo(null);
        setNome(null);
        setAtivo(Boolean.TRUE);
        setSistema(Boolean.FALSE);
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getSistema() {
        return sistema;
    }
    
    public void setSistema(Boolean sistema) {
        this.sistema = sistema;
    }
}
