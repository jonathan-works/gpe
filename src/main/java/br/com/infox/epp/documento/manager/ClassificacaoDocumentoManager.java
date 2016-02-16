package br.com.infox.epp.documento.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.entity.Processo;

@AutoCreate
@Name(ClassificacaoDocumentoManager.NAME)
@Stateless
public class ClassificacaoDocumentoManager extends Manager<ClassificacaoDocumentoDAO, ClassificacaoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoManager";
    public static final String CODIGO_CLASSIFICACAO_ACESSO_DIRETO = "acessoDireto";

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo, Papel papel) {
        return getDao().getUseableClassificacaoDocumento(isModelo, papel);
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDocumentoAnexarDocumento(TipoDocumentoEnum tipoDocumento) {
    	return getDao().getClassificacoesDocumentoAnexarDocumento(tipoDocumento);
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDocumentoCruds(TipoDocumentoEnum tipoDocumento){
        return getDao().getClassificacoesDocumentoCruds(tipoDocumento);
    }
    
    public boolean existsClassificaoAcessoDireto() {
        return getDao().findByCodigo(CODIGO_CLASSIFICACAO_ACESSO_DIRETO) != null;
    }
    
    public ClassificacaoDocumento getClassificaoParaAcessoDireto() {
        return getDao().findByCodigo(CODIGO_CLASSIFICACAO_ACESSO_DIRETO);
    }
    
    public ClassificacaoDocumento findByDescricao(String descricao) {
    	return getDao().findByDescricao(descricao);
    }

	public ClassificacaoDocumento findByCodigo(String codigo) {
		return getDao().findByCodigo(codigo);
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisRespostaComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao, boolean isModelo, Papel papel) {
		return getDao().getClassificacoesDocumentoDisponiveisRespostaComunicacao(destinatarioModeloComunicacao, isModelo, papel);
	}
	
    public List<ClassificacaoDocumento> getClassificacaoDocumentoListByProcesso(Processo processo) {
    	return getDao().getClassificacaoDocumentoListByProcesso(processo);
    }

}
