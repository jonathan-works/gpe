package br.com.infox.epp.processo.status;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;

@Named
@ViewScoped
public class StatusProcessoView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
    private StatusProcessoManager statusProcessoManager;
    @Inject
    private StatusProcessoList statusProcessoList;
	
	private String tab;

	private Integer id;
	private String nome;
	private String descricao;
	private Boolean ativo;

	@PostConstruct
    private void init() {
        newInstance();
    }
	
	@ExceptionHandled(value = MethodType.PERSIST)
    public void inserir() {
        StatusProcesso statusProcesso = new StatusProcesso();
        statusProcesso.setNome(getNome());
        statusProcesso.setDescricao(getDescricao());
        statusProcesso.setAtivo(getAtivo());
        statusProcessoManager.validateBeforePersist(statusProcesso);
        statusProcessoManager.persist(statusProcesso);
        setId(statusProcesso.getIdStatusProcesso());
    }

    @ExceptionHandled(value = MethodType.UPDATE)
    public void atualizar() {
    	StatusProcesso statusProcesso = statusProcessoManager.find(getId());
        statusProcesso.setDescricao(getDescricao());
        statusProcesso.setAtivo(getAtivo());
        statusProcessoManager.update(statusProcesso);
    }
    
    @ExceptionHandled(value = MethodType.UPDATE, updatedMessage = "#{infoxMessages['entity_inactived']}" )
    public void inativar() {
    	StatusProcesso statusProcesso = statusProcessoManager.find(getId());
    	statusProcesso.setAtivo(Boolean.FALSE);
    	statusProcessoManager.update(statusProcesso);
    }
    
    public void load(StatusProcesso statusProcesso) {
        setId(statusProcesso.getIdStatusProcesso());
        setDescricao(statusProcesso.getDescricao());
        setNome(statusProcesso.getNome());
        setAtivo(statusProcesso.getAtivo());
    }

    public void onClickFormTab() {
        newInstance();
    }

    public void onClickSearchTab() {
        statusProcessoList.refresh();
    }
	
	
	public void newInstance() {
		setId(null);
		setDescricao(null);
		setNome(null);
		setAtivo(Boolean.TRUE);
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
