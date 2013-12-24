package br.com.infox.epp.access.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

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
        String hql = "select count(o) from Recurso o where o.identificador = :identificador";
        Query query = getEntityManager().createQuery(hql).setParameter("identificador", identificador);
        return ((Long) query.getSingleResult()) > 0;
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
