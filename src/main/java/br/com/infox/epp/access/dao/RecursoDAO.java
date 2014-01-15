package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.RecursoQuery.COUNT_RECURSO_BY_IDENTIFICADOR;
import static br.com.infox.epp.access.query.RecursoQuery.IDENTIFICADOR_PARAM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Permissao;
import br.com.infox.epp.access.entity.Recurso;

@Name(RecursoDAO.NAME)
@AutoCreate
public class RecursoDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "recursoDAO";
    
    public boolean existsRecurso(String identificador){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(IDENTIFICADOR_PARAM, identificador);
        return ((Long)getNamedSingleResult(COUNT_RECURSO_BY_IDENTIFICADOR, parameters)) > 0;
    }
    
    public List<Recurso> getRecursosFromPermissoes(List<Permissao> permissoes){
        List<String> identificadores = getListaIdentificadoresFromPermissoes(permissoes);
        if (identificadores == null || identificadores.isEmpty()){
            return Collections.emptyList();
        }
        String hql = "select distinct o from Recurso o where o.identificador in (:identificadores)";
        return getEntityManager().createQuery(hql, Recurso.class)
                .setParameter("identificadores", identificadores).getResultList();
    }
    
    public List<Recurso> getRecursosWithoutPermissoes(List<Permissao> permissoes){
        List<String> identificadores = getListaIdentificadoresFromPermissoes(permissoes);
        if (identificadores == null || identificadores.isEmpty()){
            return Collections.emptyList();
        }
        String hql = "select distinct o from Recurso o where o.identificador not in (:identificadores)";
        return getEntityManager().createQuery(hql, Recurso.class)
                .setParameter("identificadores", identificadores).getResultList();
    }

    private List<String> getListaIdentificadoresFromPermissoes(List<Permissao> permissoes) {
        List<String> identificadores = new ArrayList<>();
        for (Permissao permissao : permissoes) {
            identificadores.add(permissao.getAlvo());
        }
        return identificadores;
    }

}
