package br.com.infox.epp.processo.comunicacao.list;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.query.PastaQuery;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.marcador.MarcadorSearch;

@Named
@ViewScoped
public class DocumentoDisponivelComunicacaoList extends DataList<Documento> implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoDisponivelComunicacaoList.class);

    private static final String DEFAULT_EJBQL = "select o from Documento o inner join o.documentoBin bin ";
    
    private static final String DEFAULT_WHERE = " where bin.minuta = false and "
            + "(not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) or "
            + "exists (select 1 from SigiloDocumentoPermissao sp where sp.usuario = #{usuarioLogado} and sp.ativo = true and "
            + "sp.sigiloDocumento = (select s from SigiloDocumento s where s.ativo = true and s.documento = o))) and "
            + "bin.suficientementeAssinado = true and "
            + "o.excluido = false";

    private static final String DEFAULT_ORDER = "o.dataInclusao desc";
    
    private static final String MARCADOR_FILTER = " exists (select 1 from DocumentoBin docBin inner join docBin.marcadores marc "
            + " where marc.codigo = '{codigoMarcador}' and docBin.id = bin.id) ";

    @Inject
    private PastaManager pastaManager;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private MarcadorSearch marcadorSearch;
    
    //Filtros
    private Pasta pasta;
    protected List<String> codigoMarcadores;

    private Processo processo;
    private Set<Integer> idsDocumentos = new HashSet<>();
    private Map<String, Object> paramsPasta = new HashMap<>();
    private String filterPasta = PastaQuery.FILTER_SUFICIENTEMENTE_ASSINADO + PastaQuery.FILTER_SIGILO + PastaQuery.FILTER_EXCLUIDO;

    public List<String> autoCompleteMarcadores(String query) {
        return marcadorSearch.listCodigoMarcadorFromDocumentoByProcessoAndCodigoAndNotInCodigos(getProcesso().getIdProcesso(), query, codigoMarcadores);
    }
    
    @Override
    protected void addAdditionalClauses(StringBuilder sb) {
        if (codigoMarcadores != null) {
            for (String codigoMarcador : codigoMarcadores) {
                sb.append(" and ").append(MARCADOR_FILTER.replace("{codigoMarcador}", codigoMarcador));
            }
        }
    }
    
    @Override
    protected void addRestrictionFields() {
        addRestrictionField("pasta", RestrictionType.igual);
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }
    
    @Override
    protected String getDefaultWhere() {
        StringBuilder sb = new StringBuilder(DEFAULT_WHERE); 
        if (!idsDocumentos.isEmpty()) {
            sb.append(" and o.id not in (");
            Iterator<Integer> it = idsDocumentos.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }
    
    @Override
    public void newInstance() {
       this.codigoMarcadores = null;
       refresh();
    }

    public void adicionarIdDocumento(Integer id) {
        idsDocumentos.add(id);
        refresh();
        if (!paramsPasta.containsKey(PastaQuery.PARAM_IDS_DOCUMENTOS)) {
            paramsPasta.put(PastaQuery.PARAM_IDS_DOCUMENTOS, idsDocumentos);
            filterPasta += PastaQuery.FILTER_DOCUMENTOS;
        }
    }

    public void removerIdDocumento(Integer id) {
        idsDocumentos.remove(id);
        refresh();
        if (idsDocumentos.isEmpty()) {
            paramsPasta.remove(PastaQuery.PARAM_IDS_DOCUMENTOS);
            filterPasta = PastaQuery.FILTER_SUFICIENTEMENTE_ASSINADO + PastaQuery.FILTER_SIGILO + PastaQuery.FILTER_EXCLUIDO;
        }
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
        if (pasta == null) {
            try {
                Pasta defaultFolder = pastaManager.getDefaultFolder(processo);
                if (defaultFolder != null) {
                    setPasta(defaultFolder);
                } else {
                    Pasta pasta = new Pasta();
                    pasta.setId(-1);
                    setPasta(pasta);
                }
            } catch (DAOException e) {
                LOG.error("", e);
                actionMessagesService.handleDAOException(e);
            }
        }
    }

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        Map<String, Object> attributes = event.getComponent().getAttributes();
        Object o = attributes.get("pastaToSelect");
        if (o instanceof Pasta) {
            setPasta((Pasta) o);
            return;
        }
    }

    public String getTextoPasta(Pasta pasta) {
    	if (!paramsPasta.containsKey(PastaQuery.PARAM_USUARIO_PERMISSAO)) {
    		paramsPasta.put(PastaQuery.PARAM_USUARIO_PERMISSAO, Authenticator.getUsuarioLogado());
    	}
    	String newFilterPasta = filterPasta;
    	if (codigoMarcadores != null) {
    	    for (String codigoMarcador : codigoMarcadores) {
    	        newFilterPasta += PastaQuery.FILTER_MARCADOR_DOCUMENTO.replace("{" + PastaQuery.PARAM_CODIGO_MARCADOR + "}", codigoMarcador);
    	    }
    	}
        int totalDocumentosPasta = pastaManager.getTotalDocumentosPasta(pasta, newFilterPasta, paramsPasta);
        return pastaManager.getNomePasta(pasta, totalDocumentosPasta);
    }

    public Pasta getPasta() {
        return pasta;
    }

    public void setPasta(Pasta pasta) {
        this.pasta = pasta;
    }

    public List<String> getCodigoMarcadores() {
        return codigoMarcadores;
    }

    public void setCodigoMarcadores(List<String> codigoMarcadores) {
        this.codigoMarcadores = codigoMarcadores;
    }
}
