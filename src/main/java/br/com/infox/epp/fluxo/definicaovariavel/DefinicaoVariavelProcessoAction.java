package br.com.infox.epp.fluxo.definicaovariavel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos.RecursoVariavel;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Named
@ViewScoped
public class DefinicaoVariavelProcessoAction implements Serializable {

	private static final long serialVersionUID = 1L;

    @Inject
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    @Inject
    private DefinicaoVariavelProcessoList definicaoVariavelProcessoList;
    @Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    @Inject
    private DefinicaoVariavelProcessoRecursos definicaoVariavelProcessoRecursos;
    
    private Fluxo fluxo;
    private DefinicaoVariavelProcesso variavel;
    private List<DefinicaoVariavelProcessoRecursoBean> recursos;
    private DefinicaoVariavelProcessoRecursoBean recurso;

    public void init(Fluxo fluxo) {
    	this.fluxo = fluxo;
    	definicaoVariavelProcessoList.setFluxo(fluxo);
    	definicaoVariavelProcessoList.refresh();
    	novaVariavel();
    }
    
    public void novaVariavel() {
        variavel = new DefinicaoVariavelProcesso();
        variavel.setFluxo(fluxo);
        recursos = null;
        recurso = null;
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void persist() {
    	definicaoVariavelProcessoManager.persist(variavel);
        clear();
    }
    
    @ExceptionHandled(MethodType.UPDATE)
    public void update() {
    	definicaoVariavelProcessoManager.update(variavel);
        clear();
    }

	private void clear() {
		novaVariavel();
		definicaoVariavelProcessoList.refresh();
	}

    public boolean isPersisted() {
        return variavel != null && variavel.getId() != null;
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void remove(DefinicaoVariavelProcesso obj) {
        definicaoVariavelProcessoManager.remove(obj);
        clear();
    }

    public DefinicaoVariavelProcesso getVariavel() {
        return variavel;
    }

    public void setVariavel(DefinicaoVariavelProcesso variavel) {
    	clear();
        this.variavel = variavel;
    }
    
    public DefinicaoVariavelProcessoRecursoBean getRecurso() {
		return recurso;
	}
    
    public void setRecurso(DefinicaoVariavelProcessoRecursoBean recurso) {
		this.recurso = recurso;
	}
    
    public List<DefinicaoVariavelProcessoRecursoBean> getRecursos() {
    	if (recursos == null) {
    		recursos = new ArrayList<>();
    		for (RecursoVariavel recursoVariavel : definicaoVariavelProcessoRecursos.getRecursosDisponiveis()) {
    			DefinicaoVariavelProcessoRecurso recurso = definicaoVariavelProcessoSearch.getDefinicaoVariavelRecurso(variavel, recursoVariavel.getIdentificador());
    			DefinicaoVariavelProcessoRecursoBean bean;
    			if (recurso != null) {
    				bean = new DefinicaoVariavelProcessoRecursoBean(recurso, recursoVariavel, true, recurso.getVisivelUsuarioExterno(), recurso.getOrdem());
    			} else {
    				bean = new DefinicaoVariavelProcessoRecursoBean(null, recursoVariavel, false, false, null);
    			}
    			recursos.add(bean);
    		}
    	}
		return recursos;
	}
    
    @ExceptionHandled(MethodType.UPDATE)
    public void gravarRecurso() {
    	if (recurso.isVisivel()) {
    		if (recurso.getDefinicaoVariavelProcessoRecurso() == null) {
    			DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso = new DefinicaoVariavelProcessoRecurso();
    			definicaoVariavelProcessoRecurso.setDefinicaoVariavelProcesso(variavel);
    			definicaoVariavelProcessoRecurso.setOrdem(recurso.getOrdem());
    			definicaoVariavelProcessoRecurso.setVisivelUsuarioExterno(recurso.isVisivelUsuarioExterno());
    			definicaoVariavelProcessoRecurso.setRecurso(recurso.getRecursoVariavel().getIdentificador());
    			definicaoVariavelProcessoManager.adicionarRecurso(definicaoVariavelProcessoRecurso);
    		} else {
    			DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso = recurso.getDefinicaoVariavelProcessoRecurso();
    			definicaoVariavelProcessoRecurso.setOrdem(recurso.getOrdem());
    			definicaoVariavelProcessoRecurso.setVisivelUsuarioExterno(recurso.isVisivelUsuarioExterno());
    			definicaoVariavelProcessoRecurso.setRecurso(recurso.getRecursoVariavel().getIdentificador());
    			definicaoVariavelProcessoManager.atualizarRecurso(definicaoVariavelProcessoRecurso);
    		}
    	} else if (recurso.getDefinicaoVariavelProcessoRecurso() != null) {
    		definicaoVariavelProcessoManager.removerRecurso(recurso.getDefinicaoVariavelProcessoRecurso());
    	}
    	recursos = null;
    	recurso = null;
    }
    
    public static class DefinicaoVariavelProcessoRecursoBean {
    	private DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso;
    	private RecursoVariavel recursoVariavel;
    	private boolean visivel;
    	private boolean visivelUsuarioExterno;
    	private Integer ordem;

		public DefinicaoVariavelProcessoRecursoBean(DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso, RecursoVariavel recursoVariavel, 
				boolean visivel, boolean visivelUsuarioExterno, Integer ordem) {
			this.definicaoVariavelProcessoRecurso = definicaoVariavelProcessoRecurso;
			this.recursoVariavel = recursoVariavel;
			this.visivel = visivel;
			this.visivelUsuarioExterno = visivelUsuarioExterno;
			this.ordem = ordem;
		}
		
		public DefinicaoVariavelProcessoRecurso getDefinicaoVariavelProcessoRecurso() {
			return definicaoVariavelProcessoRecurso;
		}
		public void setDefinicaoVariavelProcessoRecurso(DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso) {
			this.definicaoVariavelProcessoRecurso = definicaoVariavelProcessoRecurso;
		}
		public RecursoVariavel getRecursoVariavel() {
			return recursoVariavel;
		}
		public void setRecursoVariavel(RecursoVariavel recursoVariavel) {
			this.recursoVariavel = recursoVariavel;
		}
		public boolean isVisivel() {
			return visivel;
		}
		public void setVisivel(boolean visivel) {
			this.visivel = visivel;
		}
		public boolean isVisivelUsuarioExterno() {
			return visivelUsuarioExterno;
		}
		public void setVisivelUsuarioExterno(boolean visivelUsuarioExterno) {
			this.visivelUsuarioExterno = visivelUsuarioExterno;
		}
		public Integer getOrdem() {
			return ordem;
		}
		public void setOrdem(Integer ordem) {
			this.ordem = ordem;
		}
    }
}
