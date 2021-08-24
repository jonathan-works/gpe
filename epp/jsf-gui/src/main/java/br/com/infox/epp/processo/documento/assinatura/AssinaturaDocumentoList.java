package br.com.infox.epp.processo.documento.assinatura;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.exception.ExcelExportException;
import br.com.infox.core.list.DataList;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.ExcelExportUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.AssinaturaDocumentoVO;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.log.Log;
import br.com.infox.log.Logging;
import br.com.infox.seam.path.PathResolver;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class AssinaturaDocumentoList extends DataList<AssinaturaDocumentoVO> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/Usuario/assinaturaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Assinaturas.xls";
    private static final Integer TAMANHO_PADRAO = 10000;
    private static final Log LOG = Logging.getLog(AssinaturaDocumentoList.class);

    private static final String DEFAULT_EJBQL = "select new br.com.infox.epp.processo.documento.AssinaturaDocumentoVO( " +
                                                "o.idAssinatura, bin.id, bin.extensao, coalesce(bin.nomeArquivo,d.descricao), p.numeroProcesso, o.dataAssinatura " +
                                                ") " +
                                                "from AssinaturaDocumento o " +
                                                "inner join o.documentoBin bin " +
                                                "left join bin.documentoList d " +
                                                "left join d.pasta pt " +
                                                "left join pt.processo p ";
    private static final String DEFAULT_ORDER = "o.dataAssinatura desc";

    @Inject
    private PathResolver pathResolver;
    @Getter @Setter
    private PessoaFisica pessoaFisica;
    @Getter
    private String nomeUsuario;

    private static final Map<String, String> ORDER_MAP;
    static{
        Map<String, String> order = new HashMap<>();
        order.put("dataAssinatura", "o."+AssinaturaDocumento_.dataAssinatura.getName());
        order.put("nomeArquivo", "coalesce(o."+AssinaturaDocumento_.documentoBin.getName()+"."+DocumentoBin_.nomeArquivo.getName()+",d."+Documento_.descricao.getName()+")");
        order.put("numeroProcesso", "p."+Processo_.idProcesso.getName());
        ORDER_MAP = Collections.unmodifiableMap(order);
    }

    @Override
    protected void addRestrictionFields() {
        addRestrictionField("pessoaFisica", "o.pessoaFisica = #{assinaturaDocumentoList.pessoaFisica}");
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return ORDER_MAP;
    }

    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }

    public String getTemplate() {
        return TEMPLATE;
    }

    public void setFiltroPessoaFisica(Integer idPessoaFisica) {
        if (idPessoaFisica != null) {
            pessoaFisica = getEntityManager().getReference(PessoaFisica.class, idPessoaFisica);
            nomeUsuario = pessoaFisica.getUsuarioLogin().getNomeUsuario();
        } else {
            pessoaFisica = null;
            nomeUsuario = null;
        }
    }

    public List<AssinaturaDocumentoVO> exportPDF() {
        refresh();
        return list(TAMANHO_PADRAO);
    }

    public void exportarXLS() {
        List<AssinaturaDocumentoVO> voList = list(TAMANHO_PADRAO);
        try {
            if (voList == null || voList.isEmpty()) {
                FacesMessages.instance().add(Severity.INFO, InfoxMessages.getInstance().get("entity.noDataAvailable"));
            } else {
                exportarXLS(getTemplate(), voList);
            }
        } catch (ExcelExportException e) {
            LOG.error(".exportarXLS()", e);
            FacesMessages.instance().add(Severity.ERROR, "Erro ao exportar arquivo." + e.getMessage());
        }
    }


    private void exportarXLS(String template, List<AssinaturaDocumentoVO> voList) throws ExcelExportException {
        String urlTemplate = pathResolver.getContextRealPath() + template;
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder className = new StringBuilder("assinaturaDocumentoVO");
        map.put("nomeUsuario", nomeUsuario);
        map.put(className.toString(), voList);
        ExcelExportUtil.downloadXLS(urlTemplate, map, getDownloadXlsName());
    }

}
