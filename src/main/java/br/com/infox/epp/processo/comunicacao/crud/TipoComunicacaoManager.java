package br.com.infox.epp.processo.comunicacao.crud;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;

@Name(TipoComunicacaoManager.NAME)
@AutoCreate
public class TipoComunicacaoManager extends Manager<TipoComunicacaoDAO, TipoComunicacao> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoManager";
}
