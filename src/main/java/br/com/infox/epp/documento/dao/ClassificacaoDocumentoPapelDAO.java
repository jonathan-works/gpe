package br.com.infox.epp.documento.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.query.ClassificacaoDocumentoPapelQuery;

@AutoCreate
@Name(ClassificacaoDocumentoPapelDAO.NAME)
public class ClassificacaoDocumentoPapelDAO extends DAO<ClassificacaoDocumentoPapel> {

    public static final String NAME = "classificacaoDocumentoPapelDAO";
    private static final long serialVersionUID = 1L;
    
    public boolean papelPodeAssinarClassificacao(Papel papel, ClassificacaoDocumento classificacao) {
    	Map<String, Object> params = new HashMap<>();
    	params.put(ClassificacaoDocumentoPapelQuery.PARAM_CLASSIFICACAO_DOCUMENTO, classificacao);
    	params.put(ClassificacaoDocumentoPapelQuery.PARAM_PAPEL, papel);
    	return getNamedSingleResult(ClassificacaoDocumentoPapelQuery.PAPEL_PODE_ASSINAR_CLASSIFICACAO, params) != null;
    }

	public boolean classificacaoExigeAssinatura(ClassificacaoDocumento classificacaoDocumento) {
		Map<String, Object> params = new HashMap<>();
		params.put(ClassificacaoDocumentoPapelQuery.PARAM_CLASSIFICACAO_DOCUMENTO, classificacaoDocumento);
		return getNamedSingleResult(ClassificacaoDocumentoPapelQuery.CLASSIFICACAO_EXIGE_ASSINATURA, params) != null;
	}
}
