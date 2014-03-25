package br.com.infox.epp.processo.dao;

import java.util.HashMap;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;

@AutoCreate
@Name(RelacionamentoProcessoDAO.NAME)
public class RelacionamentoProcessoDAO extends DAO<RelacionamentoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "relacionamentoProcessoDAO";

    public boolean existeRelacionamento(String processo1, String processo2) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("processo1", processo1);
        parameters.put("processo2", processo2);
        final String query = "select r from RelacionamentoProcesso rp inner join rp.relacionamento r where rp.numeroProcesso=:processo1 or rp.numeroProcesso=:processo2 group by r having count(r)>1";
        final List<Relacionamento> resultList = getResultList(query, parameters);
        return resultList.size() > 0;
    }
    
}
