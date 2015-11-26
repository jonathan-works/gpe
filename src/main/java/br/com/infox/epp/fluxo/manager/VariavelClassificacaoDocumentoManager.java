package br.com.infox.epp.fluxo.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.fluxo.dao.VariavelClassificacaoDocumentoDAO;
import br.com.infox.epp.fluxo.entity.VariavelClassificacaoDocumento;
import br.com.infox.ibpm.variable.VariableAccessHandler;

@Stateless
@AutoCreate
@Name(VariavelClassificacaoDocumentoManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VariavelClassificacaoDocumentoManager extends Manager<VariavelClassificacaoDocumentoDAO, VariavelClassificacaoDocumento> {
    
	private static final long serialVersionUID = 1L;
    public static final String NAME = "variavelClassificacaoDocumentoManager";
    
    public List<VariavelClassificacaoDocumento> listVariavelClassificacao(String nomeVariavel, Integer idFluxo) {
        return getDao().listVariavelClassificacao(nomeVariavel, idFluxo);
    }
    
    public List<ClassificacaoDocumento> listClassificacoesPublicadasDaVariavel(String nomeVariavel, Integer idFluxo) {
        return getDao().listClassificacoesPublicadasDaVariavel(nomeVariavel, idFluxo);
    }
    
    public void publicarClassificacoesDasVariaveis(Integer idFluxo) throws DAOException {
        getDao().publicarClassificacoesDasVariaveis(idFluxo);
    }
    
    public void removerClassificacoesDeVariaveisObsoletas(Integer idFluxo, List<String> variaveisExistentes) throws DAOException {
        getDao().removerClassificacoesDeVariaveisObsoletas(idFluxo, variaveisExistentes);
    }
    
    /**
     * Copia as associações da variável antiga para a nova, ao alterar o nome
     * @param idFluxo
     * @param variavelAntiga
     * @param novaVariavel
     * @throws DAOException
     */
    @Observer(value = VariableAccessHandler.EVENT_JBPM_VARIABLE_NAME_CHANGED)
    public void copiarClassificacoesDeVariavel(Integer idFluxo, String variavelAntiga, String novaVariavel) throws DAOException {
        List<VariavelClassificacaoDocumento> variaveisClassificacao = listVariavelClassificacao(variavelAntiga, idFluxo);
        for (VariavelClassificacaoDocumento variavelClassificacao : variaveisClassificacao) {
            // Se a pessoa mudou o nome da variável e mudou novamente para algum dos nomes anteriores, antes de publicar, 
            // apenas desmarca a associação da variável antiga, para que não seja removida na publicação 
            if (variavelClassificacao.getVariavel().equals(novaVariavel)) {
                variavelClassificacao.setRemoverNaPublicacao(false);
                update(variavelClassificacao);
            // Senão, apenas copia as associações que não estão marcadas para remoção na publicação
            } else if (!variavelClassificacao.getRemoverNaPublicacao()) {
                VariavelClassificacaoDocumento novaVariavelClassificacao = new VariavelClassificacaoDocumento();
                novaVariavelClassificacao.setFluxo(variavelClassificacao.getFluxo());
                novaVariavelClassificacao.setPublicado(false);
                novaVariavelClassificacao.setVariavel(novaVariavel);
                novaVariavelClassificacao.setClassificacaoDocumento(variavelClassificacao.getClassificacaoDocumento());
                novaVariavelClassificacao.setRemoverNaPublicacao(false);
                persist(novaVariavelClassificacao);
            }
        }
    }
    
    @Override
    public VariavelClassificacaoDocumento persist(VariavelClassificacaoDocumento o) throws DAOException {
        VariavelClassificacaoDocumento variavelClassificacaoDocumento = findVariavelClassificacao(o.getFluxo().getIdFluxo(), o.getVariavel(), o.getClassificacaoDocumento());
        // Se a associação já existe, apenas desmarca para que não seja removida na publicação
        // (a pessoa removeu a associação e associou de novo sem ter publicado)
        if (variavelClassificacaoDocumento != null) {
            variavelClassificacaoDocumento.setRemoverNaPublicacao(false);
            return update(variavelClassificacaoDocumento);
        }
        return super.persist(o);
    }
    
    @Override
    public VariavelClassificacaoDocumento remove(VariavelClassificacaoDocumento o) throws DAOException {
        // Se está publicado, não remove diretamente, marca para remoção na publicação
        if (o.getPublicado()) {
            o.setRemoverNaPublicacao(true);
            return update(o);
        }
        return super.remove(o);
    }
    
    public List<ClassificacaoDocumento> listClassificacoesDisponiveisParaVariavel(Integer idFluxo, String variavel, TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento, int start, int max) {
        return getDao().listClassificacoesDisponiveisParaVariavel(idFluxo, variavel, tipoDocumento, nomeClassificacaoDocumento, start, max);
    }
    
    public Long totalClassificacoesDisponiveisParaVariavel(Integer idFluxo, String variavel, TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento) {
        return getDao().totalClassificacoesDisponiveisParaVariavel(idFluxo, variavel, tipoDocumento, nomeClassificacaoDocumento);
    }
    
    public VariavelClassificacaoDocumento findVariavelClassificacao(Integer idFluxo, String variavel, ClassificacaoDocumento classificacao) {
        return getDao().findVariavelClassificacao(idFluxo, variavel, classificacao);
    }
}
