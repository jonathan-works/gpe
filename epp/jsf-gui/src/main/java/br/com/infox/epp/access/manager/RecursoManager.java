package br.com.infox.epp.access.manager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.entity.Permissao;
import br.com.infox.epp.access.entity.Recurso;

@Stateless
public class RecursoManager extends Manager<RecursoDAO, Recurso> {

    private static final long serialVersionUID = 1L;

    public boolean existsRecurso(String identificador) {
        return getDao().existsRecurso(identificador);
    }

    public Recurso getRecursoByIdentificador(String identificador) {
        return getDao().getRecursoByIdentificador(identificador);
    }

    public List<Recurso> getRecursosFromPermissoes(List<Permissao> permissoes) {
        return getDao().getRecursosFromPermissoes(permissoes);
    }

    public List<Recurso> getRecursosWithoutPermissoes(List<Permissao> permissoes) {
        return getDao().getRecursosWithoutPermissoes(permissoes);
    }

    public List<String> getIdentificadorRecursosFromPermissoes(
            List<Permissao> permissoes) {
        List<Recurso> recursos = getDao().getRecursosFromPermissoes(permissoes);
        List<String> nomes = new ArrayList<>();
        for (Recurso recurso : recursos) {
            nomes.add(recurso.getIdentificador());
        }
        return nomes;
    }

    public List<String> getIdentificadorRecursosWithoutPermissoes(
            List<Permissao> permissoes) {
        List<Recurso> recursos = getDao().getRecursosWithoutPermissoes(permissoes);
        List<String> nomes = new ArrayList<>();
        for (Recurso recurso : recursos) {
            nomes.add(recurso.getIdentificador());
        }
        return nomes;
    }

    public List<String> getPapeisAssociadosARecurso(Recurso recurso) {
        return getDao().getPapeisAssociadosARecurso(recurso);
    }

}
