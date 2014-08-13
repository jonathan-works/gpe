package br.com.infox.epp.tce.prestacaocontas.modelo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.tce.prestacaocontas.modelo.dao.GrupoPrestacaoContasDAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.GrupoPrestacaoContas;

@Name(GrupoPrestacaoContasManager.NAME)
@AutoCreate
public class GrupoPrestacaoContasManager extends Manager<GrupoPrestacaoContasDAO, GrupoPrestacaoContas> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "grupoPrestacaoContasManager";
}
