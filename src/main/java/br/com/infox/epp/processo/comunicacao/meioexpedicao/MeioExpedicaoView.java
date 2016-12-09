package br.com.infox.epp.processo.comunicacao.meioexpedicao;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;

@Named
@ViewScoped
public class MeioExpedicaoView implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject
    private MeioExpedicaoService meioExpedicaoService;
    @Inject
    private MeioExpedicaoList meioExpedicaoList;
    
    private String tab = "search";
    private MeioExpedicao meioExpedicao;
    
    @PostConstruct
    public void init() {
        meioExpedicao = new MeioExpedicao();
    }
    
    public void onClickSearchTab() {
        init();
        meioExpedicaoList.refresh();
    }
    
    public void onClickFormTab() {
    }
    
    @ExceptionHandled(MethodType.PERSIST)
    public void persist() {
        meioExpedicaoService.persist(meioExpedicao);
    }
    
    @ExceptionHandled(MethodType.UPDATE)
    public void update() {
        meioExpedicao = meioExpedicaoService.update(meioExpedicao);
    }
    
    @ExceptionHandled(MethodType.INACTIVE)
    public void inativar(MeioExpedicao meioExpedicao) {
        meioExpedicao.setAtivo(false);
        meioExpedicaoService.update(meioExpedicao);
    }
    
    public void load(MeioExpedicao meioExpedicao) {
        this.meioExpedicao = meioExpedicao;
    }
    
    public String getTab() {
        return tab;
    }
    
    public void setTab(String tab) {
        this.tab = tab;
    }
    
    public MeioExpedicao getMeioExpedicao() {
        return meioExpedicao;
    }
}