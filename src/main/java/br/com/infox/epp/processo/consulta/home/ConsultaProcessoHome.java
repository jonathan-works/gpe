package br.com.infox.epp.processo.consulta.home;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;

@Scope(ScopeType.CONVERSATION)
@Name(ConsultaProcessoHome.NAME)
public class ConsultaProcessoHome implements Serializable {

    public static final String NAME = "consultaProcessoHome";
    private static final long serialVersionUID = 1L;

    private List<Natureza> naturezaList;
    private List<Categoria> categoriaList;

    @In
    private GenericManager genericManager;

    @Create
    public void init() {
        naturezaList = genericManager.findAll(Natureza.class);
        categoriaList = genericManager.findAll(Categoria.class);
    }

    public List<Natureza> getNaturezaList() {
        return naturezaList;
    }

    public List<Categoria> getCategoriaList() {
        return categoriaList;
    }

}
