/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
 */
package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.Component;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.component.AbstractHome;

public abstract class AbstractFluxoHome<T> extends AbstractHome<Fluxo> {

    private static final long serialVersionUID = 1L;

    public void setFluxoIdFluxo(Integer id) {
        setId(id);
    }

    public Integer getFluxoIdFluxo() {
        return (Integer) getId();
    }

    @Override
    protected Fluxo createInstance() {
        Fluxo fluxo = new Fluxo();
        UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
                UsuarioHome.NAME, false);
        if (usuarioHome != null) {
            fluxo.setUsuarioPublicacao(usuarioHome.getDefinedInstance());
        }
        return fluxo;
    }

    @Override
    public String remove() {
        UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
                UsuarioHome.NAME, false);
        if (usuarioHome != null) {
            usuarioHome.getInstance().getFluxoList().remove(instance);
        }
        return super.remove();
    }

    @Override
    public String remove(Fluxo obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        refreshGrid("fluxoGrid");
        return ret;
    }

    @Override
    public String persist() {
        String persistMessage = super.persist();
        UsuarioLogin usuarioPublicacao = getInstance().getUsuarioPublicacao();
        if (usuarioPublicacao != null) {
            List<Fluxo> usuarioPublicacaoList = usuarioPublicacao
                    .getFluxoList();
            if (!usuarioPublicacaoList.contains(instance)) {
                getEntityManager().refresh(usuarioPublicacao);
            }
        }
        // newInstance();
        return persistMessage;
    }
}