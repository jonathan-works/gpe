package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;

@Stateless
@Name(TipoComunicacaoManager.NAME)
@AutoCreate
@Stateless
public class TipoComunicacaoManager extends Manager<TipoComunicacaoDAO, TipoComunicacao> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoManager";
    
    public List<TipoComunicacao> listTiposComunicacaoAtivos() {
    	return getDao().listTiposComunicacaoAtivos();
    }
}
