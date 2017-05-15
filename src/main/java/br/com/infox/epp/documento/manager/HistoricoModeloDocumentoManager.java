package br.com.infox.epp.documento.manager;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.dao.HistoricoModeloDocumentoDAO;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@Name(HistoricoModeloDocumentoManager.NAME)
@AutoCreate
@Stateless
public class HistoricoModeloDocumentoManager extends Manager<HistoricoModeloDocumentoDAO, HistoricoModeloDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoModeloDocumentoManager";

    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;

    public List<ModeloDocumento> listModelosDoHistorico() {
        return getDao().listModelosDoHistorico();
    }

    public List<UsuarioLogin> listUsuariosQueAlteraramModelo(
            ModeloDocumento modeloDocumento) {
        return getDao().listUsuariosQueAlteraramModelo(modeloDocumento);
    }

    public void restaurar(Integer idModeloDocumento, HistoricoModeloDocumento historicoRestaurar) {
        try {
            ModeloDocumento modelo = modeloDocumentoManager.find(idModeloDocumento);
            ModeloDocumento modeloAtual = (ModeloDocumento) EntityUtil.cloneObject(modelo, false);

            HistoricoModeloDocumento historico = new HistoricoModeloDocumento();
            historico.setModeloDocumento(modelo);
            historico.setDataAlteracao(new Date());
            historico.setUsuarioAlteracao((UsuarioLogin) ComponentUtil.getComponent(Authenticator.USUARIO_LOGADO));
            historico.setAtivo(modelo.getAtivo());
            historico.setTituloModeloDocumento(modelo.getTituloModeloDocumento());
            historico.setDescricaoModeloDocumento(modelo.getModeloDocumento());

            modelo.setTituloModeloDocumento(historicoRestaurar.getTituloModeloDocumento());
            modelo.setAtivo(historicoRestaurar.getAtivo());
            modelo.setModeloDocumento(historicoRestaurar.getDescricaoModeloDocumento());
            modelo.setTipoModeloDocumento(historicoRestaurar.getModeloDocumento().getTipoModeloDocumento());

            if (modeloAtual.hasChanges(modelo)) {
                persist(historico);
            }
            modeloDocumentoManager.update(modelo);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BusinessException("Erro ao restaurar modelo");
        }
    }
}
