package br.com.infox.epp.processo.comunicacao.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;

@Name(TipoComunicacaoCrudAction.NAME)
public class TipoComunicacaoCrudAction extends AbstractCrudAction<TipoComunicacao, TipoComunicacaoManager> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoCrudAction";
    
}
