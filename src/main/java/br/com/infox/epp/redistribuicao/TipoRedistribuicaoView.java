package br.com.infox.epp.redistribuicao;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.controller.Controller;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class TipoRedistribuicaoView implements Controller {
    private static final long serialVersionUID = 1L;

    @Inject
    private TipoRedistribuicaoList tipoRedistribuicaoList;
    @Inject
    private TipoRedistribuicaoService tipoRedistribuicaoService;

    // General controll
    private String tab;

    // Form controll
    private String codigo;
    private String descricao;
    private boolean ativo;
    private TipoRedistribuicao instance;

    @ExceptionHandled(MethodType.INACTIVE)
    public void inactive(TipoRedistribuicao row) {
    		row.setAtivo(false);
    		tipoRedistribuicaoService.persist(row);
            tipoRedistribuicaoList.refresh();
    }

    public boolean isManaged() {
        return instance != null && instance.getId() != null;
    }
    
    protected void applyValues(TipoRedistribuicao tipoRedistribuicao) {
    	tipoRedistribuicao.setCodigo(codigo);
    	tipoRedistribuicao.setDescricao(descricao);
    	tipoRedistribuicao.setAtivo(ativo);
    }

    @ExceptionHandled(value=MethodType.UNSPECIFIED, successMessage="Registro gravado com sucesso.")
    public void save() {
    	if(instance == null) {
    		instance = new TipoRedistribuicao();
    	}
    	if(tipoRedistribuicaoService.existeTipoRedistribuicao(codigo, instance.getId())) {
    		throw new BusinessException("Já existe um registro com esse código");
    	}
    	
    	applyValues(instance);
    	tipoRedistribuicaoService.persist(instance);
    	tipoRedistribuicaoList.refresh();
    }

    public void newInstance() {
        instance = null;
        codigo = null;
        descricao = null;
        ativo = true;
    }

    public void select(TipoRedistribuicao row) {
        instance = row;
        codigo = row.getCodigo();
        descricao = row.getDescricao();
        ativo = row.isAtivo();
        setTab("form");
    }

    @Override
    public String getTab() {
        return tab;
    }

    @Override
    public void setTab(String tab) {
        this.tab = tab;
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public void setId(Object id) {
    }

    @Override
    public void onClickSearchTab() {
    }

    @Override
    public void onClickFormTab() {
        newInstance();
    }

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
    
    
}
