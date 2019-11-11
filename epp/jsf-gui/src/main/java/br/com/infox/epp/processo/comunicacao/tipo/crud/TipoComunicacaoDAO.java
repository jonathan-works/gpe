package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;

@Stateless
@AutoCreate
@Name(TipoComunicacaoDAO.NAME)
public class TipoComunicacaoDAO extends DAO<TipoComunicacao> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoDAO";
    
    public List<TipoComunicacao> listTiposComunicacaoAtivos() {
    	return getNamedResultList(TipoComunicacaoQuery.LIST_TIPO_COMUNICACAO_ATIVOS);
    }
}
