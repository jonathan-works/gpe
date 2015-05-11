package br.com.infox.epp.processo.documento.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.manager.ParametroManager;

@Name(DocumentoList.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class DocumentoList extends EntityList<Documento> {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select o from Documento o inner join o.documentoBin bin where "
            + "bin.minuta = false and "
            + "(not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) or "
            + "exists (select 1 from SigiloDocumentoPermissao sp where sp.usuario = #{usuarioLogado} and sp.ativo = true and "
            + "sp.sigiloDocumento = (select s from SigiloDocumento s where s.ativo = true and s.documento = o))) and "
            + "(bin.suficientementeAssinado = true or o.localizacao = #{authenticator.getUsuarioPerfilAtual().getLocalizacao()})";

    private static final String DEFAULT_ORDER = "o.dataInclusao desc";

    public static final String NAME = "documentoList";

    @In private ParametroManager parametroManager;
    @In private PastaManager pastaManager;
    @In private ActionMessagesService actionMessagesService;

    @Override
    protected void addSearchFields() {
        addSearchField("pasta", SearchCriteria.IGUAL);
        addSearchField("classificacaoDocumento", SearchCriteria.IGUAL);
        addSearchField("numeroDocumento", SearchCriteria.IGUAL);
        addSearchField("excluido", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        String usuarioExternoPodeVer = (String) Parametros.IS_USUARIO_EXTERNO_VER_DOC_EXCLUIDO.getValue();
        if (Identity.instance().hasRole("usuarioExterno") && "false".equals(usuarioExternoPodeVer)) {
            getEntity().setExcluido(Boolean.FALSE);
        }
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("processoDocumentoBin.sizeFormatado", "o.documentoBin.size");
        map.put("numeroDocumento", "o.numeroDocumento");
        map.put("usuarioInclusao", "o.usuarioInclusao");
        map.put("dataInclusao", "o.dataInclusao");
        map.put("descricao", "o.descricao");
        map.put("classificacaoDocumento", "o.classificacaoDocumento");
        return map;
    }

    public Processo getProcesso() {
        return getEntity().getProcesso();
    }

    public void setProcesso(Processo processo) {
        Documento documento = getEntity();
        documento.setProcesso(processo);
        if (documento.getPasta() == null) {
            try {
                Pasta pasta = pastaManager.getDefaultFolder(documento.getProcesso());
                documento.setPasta(pasta);
            } catch (DAOException e) {
                actionMessagesService.handleDAOException(e);
            }
        }
    }

    public void checkPastaToRemove(Pasta toRemove) {
        Pasta selected = getEntity().getPasta();
        if (selected == toRemove) {
            getEntity().setPasta(pastaManager.getDefault(getProcesso()));
        }
    }

    public void selectPasta(Pasta pasta) {
        getEntity().setPasta(pasta);
    }
}
