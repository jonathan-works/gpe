package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.RecursoQuery.COUNT_RECURSO_BY_IDENTIFICADOR;
import static br.com.infox.epp.access.query.RecursoQuery.IDENTIFICADOR_PARAM;
import static br.com.infox.epp.access.query.RecursoQuery.LISTA_IDENTIFICADORES_PARAM;
import static br.com.infox.epp.access.query.RecursoQuery.RECURSOS_FROM_IDENTIFICADORES;
import static br.com.infox.epp.access.query.RecursoQuery.RECURSOS_NOT_IN_IDENTIFICADORES;

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
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(LISTA_IDENTIFICADORES_PARAM, identificadores);
        return getNamedResultList(RECURSOS_FROM_IDENTIFICADORES, parameters);
    }
    
    public List<Recurso> getRecursosWithoutPermissoes(List<Permissao> permissoes){
        List<String> identificadores = getListaIdentificadoresFromPermissoes(permissoes);
        if (identificadores == null || identificadores.isEmpty()){
            return Collections.emptyList();
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(LISTA_IDENTIFICADORES_PARAM, identificadores);
        return getNamedResultList(RECURSOS_NOT_IN_IDENTIFICADORES, parameters);
    }

    private List<String> getListaIdentificadoresFromPermissoes(List<Permissao> permissoes) {
        List<String> identificadores = new ArrayList<>();
        for (Permissao permissao : permissoes) {
            identificadores.add(permissao.getAlvo());
        }
        return identificadores;
    }

}
