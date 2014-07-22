package br.com.infox.epp.unidadedecisora.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@AutoCreate
@Name(UnidadeDecisoraMonocraticaDAO.NAME)
public class UnidadeDecisoraMonocraticaDAO extends DAO<UnidadeDecisoraMonocratica>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaDAO";

	public boolean existeUnidadeDecisoraMonocraticaComHierarquiaLocalizacao(Localizacao localizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put("caminhoCompleto", localizacao.getCaminhoCompleto());
        return ((Number) getSingleResult
                ("select count(o) from UnidadeDecisoraMonocratica o "
                        + "where o.localizacao.caminhoCompleto like concat(:caminhoCompleto, '%')",
                params)).longValue() > 0;
    }
}
