package br.com.infox.epp.fluxo.definicaovariavel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.fluxo.crud.FluxoController;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos.RecursoVariavel;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.exception.BusinessRollbackException;
import lombok.Getter;

@Named
@ViewScoped
public class DefinicaoVariavelProcessoAction2 implements Serializable {

	private static final long serialVersionUID = 1L;

    @Inject
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    @Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    @Inject
    private DefinicaoVariavelProcessoRecursos definicaoVariavelProcessoRecursos;
    @Inject
    private FluxoController fluxoController;
    @Inject
    private JsfUtil jsfUtil;

    private DefinicaoVariavelProcesso variavel;
    private List<DraggableListItem> funcionalidades;

    private List<DefinicaoVariavelProcesso> variaveis;
    private Fluxo fluxo;

    @Getter
    private TipoPesquisaVariavelProcessoEnum[] tiposPesquisaVariavelProcesso;

    public void init() {
    	fluxo = fluxoController.getFluxo();
    	variaveis = null;
    	novaVariavel();
    	funcionalidades = null;
    	tiposPesquisaVariavelProcesso = TipoPesquisaVariavelProcessoEnum.values();
    }

    public void novaVariavel() {
        variavel = new DefinicaoVariavelProcesso();
        variavel.setFluxo(fluxoController.getFluxo());
    }

    @ExceptionHandled
    private void checkFiltroPesquisa() {
        if(getVariavel().getTipoPesquisaProcesso() != null && (
            StringUtil.isEmpty(getVariavel().getValorPadrao()) ||
            getVariavel().getValorPadrao().contains(".")
        )){
            RequestContext.getCurrentInstance().addCallbackParam("erro", true);
            throw new BusinessRollbackException("Impossível filtrar colunas que utilizam EL");
        }
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void persist() {
        checkFiltroPesquisa();
    	definicaoVariavelProcessoManager.persist(variavel);
        clear();
        addMessage("Variável cadastrada com sucessos");
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void update() {
        checkFiltroPesquisa();
    	definicaoVariavelProcessoManager.update(variavel);
        clear();
        addMessage("Variável alterada com sucessos");
    }

	private void clear() {
		novaVariavel();
		variaveis = null;
	}

    public boolean isPersisted() {
        return variavel != null && variavel.getId() != null;
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void remove(DefinicaoVariavelProcesso obj) {
    	int cont = 0;
		for(DraggableListItem item : funcionalidades) {
			for(DefinicaoVariavelProcessoRecursoBean2 var : item.getVariaveis()) {
				if(var.getDefinicao().getId() == obj.getId()) {
					continue; //ignora o que será excluído
				}
				DefinicaoVariavelProcessoRecurso reordena = definicaoVariavelProcessoManager.getRecursoById(var.getId());
				if (reordena != null) {
					reordena.setOrdem(cont);
					definicaoVariavelProcessoManager.atualizarRecurso(reordena);
					cont++;
				}
			}
			cont = 0;
		}
        definicaoVariavelProcessoManager.remove(obj);
        clear();
        funcionalidades = null;
        addMessage("Variável excluída e removida de todos os recursos");
    }

    public DefinicaoVariavelProcesso getVariavel() {
        return variavel;
    }

    public void setVariavel(DefinicaoVariavelProcesso variavel) {
    	clear();
        this.variavel = variavel;
    }

	public List<DraggableListItem> getFuncionalidades() {
		if (funcionalidades == null) {
			funcionalidades = new ArrayList<DraggableListItem>();

			for (RecursoVariavel recursoVariavel : definicaoVariavelProcessoRecursos.getRecursosDisponiveis()) {
				DraggableListItem item = new DraggableListItem();
				item.setRecurso(recursoVariavel);
				item.setVariaveis(new ArrayList<DefinicaoVariavelProcessoRecursoBean2>());
				for (DefinicaoVariavelProcessoRecurso dvpr : definicaoVariavelProcessoSearch.getDefinicoesVariaveis(fluxo, recursoVariavel.getIdentificador())) {
					DefinicaoVariavelProcessoRecursoBean2 bean = new DefinicaoVariavelProcessoRecursoBean2();
					bean.setId(dvpr.getId());
					bean.setOrdem(dvpr.getOrdem());
					bean.setRecurso(dvpr.getRecurso());
					bean.setVersion(dvpr.getVersion());
					bean.setVisivelUsuarioExterno(dvpr.getVisivelUsuarioExterno());

					DefinicaoVariavelProcessoBean2 dvpBean = new DefinicaoVariavelProcessoBean2();
					dvpBean.setId(dvpr.getDefinicaoVariavelProcesso().getId());
					dvpBean.setFluxo(fluxo);
					dvpBean.setLabel(dvpr.getDefinicaoVariavelProcesso().getLabel());
					dvpBean.setNome(dvpr.getDefinicaoVariavelProcesso().getNome());
					dvpBean.setValorPadrao(dvpr.getDefinicaoVariavelProcesso().getValorPadrao());
					dvpBean.setVersion(dvpr.getDefinicaoVariavelProcesso().getVersion());

					bean.setDefinicao(dvpBean);
					item.getVariaveis().add(bean);
				}
				funcionalidades.add(item);
			}
		}
		return funcionalidades;
	}

	public void setFuncionalidades(List<DraggableListItem> funcionalidades) {
		this.funcionalidades = funcionalidades;
	}

	public void alteraVisivel() {
		String idDvpr = jsfUtil.getRequestParameter("idDvpr");
		DefinicaoVariavelProcessoRecurso recurso = definicaoVariavelProcessoManager.getRecursoById(Long.parseLong(idDvpr));
		recurso.setVisivelUsuarioExterno(!recurso.getVisivelUsuarioExterno());
		definicaoVariavelProcessoManager.atualizarRecurso(recurso);
		funcionalidades = null;
		addMessage("A visibilidade foi alterada com sucesso");
	}

	public void remover(String idDvpr) {
		idDvpr = jsfUtil.getRequestParameter("idDvpr");
		DefinicaoVariavelProcessoRecurso recurso = definicaoVariavelProcessoManager.getRecursoById(Long.parseLong(idDvpr));
		for(DraggableListItem item : funcionalidades) {
    		if(item.getRecurso().getIdentificador().equalsIgnoreCase(recurso.getRecurso())) {
    			int cont = 0;
	    		for(DefinicaoVariavelProcessoRecursoBean2 var : item.getVariaveis()) {
	    			if(var.getId() == recurso.getId()) {
	    				continue; //ignora o que será excluído
	    			}
	    			DefinicaoVariavelProcessoRecurso reordena = definicaoVariavelProcessoManager.getRecursoById(var.getId());
					if (reordena != null) {
						reordena.setOrdem(cont);
						definicaoVariavelProcessoManager.atualizarRecurso(reordena);
						cont++;
					}
	    		}
    		}
		}
		definicaoVariavelProcessoManager.removerRecurso(recurso);
		funcionalidades = null;
		addMessage("Variável removida com sucesso");
	}

	/**
	 * O p:orderList depois que reordena transforma o objeto da lista em String com o valor que está atribuído no itemValue da tag
	 */
    public void onDropRecurso(DragDropEvent ddEvent) {
    	DefinicaoVariavelProcesso dvp = ((DefinicaoVariavelProcesso) ddEvent.getData());
        String recurso = jsfUtil.getRequestParameter("recurso");
        int ordem = 0;
        boolean possui = false;
    	for(DraggableListItem item : funcionalidades) {
    		if(item.getRecurso().getIdentificador().equalsIgnoreCase(recurso)) {
	    		for(DefinicaoVariavelProcessoRecursoBean2 var : item.getVariaveis()) {
	    			ordem++; //a nova variável será adicionada ao fim da lista
	    			if(var.getDefinicao().getId() == dvp.getId()) {
	    				possui = true;
	    				break;
	    			}
	    		}
    		}
    	}

    	if(possui) {
    		addMessage("Este recurso já possui esta variável");
    	}else {
    		DefinicaoVariavelProcessoRecurso dvpr = new DefinicaoVariavelProcessoRecurso();
			dvpr.setOrdem(ordem);
			dvpr.setRecurso(recurso);
			dvpr.setVisivelUsuarioExterno(true);
			dvpr.setDefinicaoVariavelProcesso(dvp);
			dvpr.setVersion(0L);
			definicaoVariavelProcessoManager.adicionarRecurso(dvpr);
			funcionalidades = null;
			addMessage("Variável adicionada com sucesso");
    	}
    }

	public void onReorder() {
		for (DraggableListItem item : funcionalidades) {
			int cont = 0;
			for (int i = 0; i < item.getVariaveis().size(); i++) {
				Object var = item.getVariaveis().get(i);
				if (var instanceof String) {
					System.out.println(var + ":" + cont);
					DefinicaoVariavelProcessoRecurso recurso = definicaoVariavelProcessoManager.getRecursoById(Long.parseLong(var.toString()));
					if (recurso != null) {
						recurso.setOrdem(cont);
						definicaoVariavelProcessoManager.atualizarRecurso(recurso);
						cont++;
					}
				}
			}
		}
		addMessage("Lista reordenada com sucesso");
        funcionalidades = null;
	}

	private void addMessage(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "mensagem"));
	}

	public List<DefinicaoVariavelProcesso> getVariaveis() {
		if(variaveis == null)
			variaveis = definicaoVariavelProcessoSearch.listVariaveisByFluxo(getFluxo());
		return variaveis;
	}

	public void setVariaveis(List<DefinicaoVariavelProcesso> variaveis) {
		this.variaveis = variaveis;
	}

	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}

}
