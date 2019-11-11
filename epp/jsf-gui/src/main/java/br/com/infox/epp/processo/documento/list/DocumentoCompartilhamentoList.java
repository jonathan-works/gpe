package br.com.infox.epp.processo.documento.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.jboss.seam.security.Identity;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Parametros;

@Named
@ViewScoped
public class DocumentoCompartilhamentoList extends DataList<Documento> {
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from Documento o "
            + "inner join o.documentoBin bin "
            + "inner join o.pasta pasta ";

    private static final String DEFAULT_WHERE = "where bin.minuta = false "
            + "and (not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) "
                + "or exists (select 1 from SigiloDocumentoPermissao sp where sp.usuario = #{usuarioLogado} and sp.ativo = true "
                + "and sp.sigiloDocumento = (select s from SigiloDocumento s where s.ativo = true and s.documento = o))"
            + ") "
            + "and (bin.suficientementeAssinado = true or o.localizacao = #{authenticator.getLocalizacaoAtual()}) "
            + "and exists (select 1 from DocumentoCompartilhamento dc where dc.documento = o and dc.ativo = true) "
            + "and pasta in (#{documentoCompartilhamentoList.pastas}) "
            + "and pasta.processo = #{documentoCompartilhamentoList.processo}";
    private static final String DEFAULT_ORDER = "o.dataInclusao desc";

    private static final String MARCADOR_FILTER = " exists (select 1 from DocumentoBin docBin inner join docBin.marcadores marc "
            + "where marc.codigo = '{codigoMarcador}' and docBin.id = bin.id) ";

    private Processo processo;
    private List<Pasta> pastas;
    private ClassificacaoDocumento classificacaoDocumento;
    private Integer numeroDocumento;
    private Boolean excluido;
    private List<String> codigoMarcadores;

    @Override
    protected String getDefaultEjbql() {
        String usuarioExternoPodeVer = Parametros.IS_USUARIO_EXTERNO_VER_DOC_EXCLUIDO.getValue();
        if (Identity.instance().hasRole("usuarioExterno") && "false".equals(usuarioExternoPodeVer)) {
            setExcluido(Boolean.FALSE);
        }
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultWhere() {
        return DEFAULT_WHERE;
    }

    @Override
    protected void addAdditionalClauses(StringBuilder sb) {
        if (codigoMarcadores != null) {
            for (String codigoMarcador : codigoMarcadores) {
                sb.append(" and ").append(MARCADOR_FILTER.replace("{codigoMarcador}", codigoMarcador.toUpperCase()));
            }
        }
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected void addRestrictionFields() {
        addRestrictionField("classificacaoDocumento", RestrictionType.igual);
        addRestrictionField("numeroDocumento", RestrictionType.igual);
        addRestrictionField("excluido", RestrictionType.igual);
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("processoDocumentoBin.sizeFormatado", "o.documentoBin.size");
        map.put("numeroDocumento", "o.numeroDocumento");
        map.put("usuarioInclusao", "o.usuarioInclusao.nomeUsuario");
        map.put("dataInclusao", "o.dataInclusao");
        map.put("descricao", "o.descricao");
        map.put("classificacaoDocumento", "o.classificacaoDocumento.descricao");
        return map;
    }

    public List<Pasta> getPastas() {
        return pastas;
    }

    public void setPastas(List<Pasta> pastas) {
        this.pastas = pastas;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
    }

    public Integer getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Boolean getExcluido() {
        return excluido;
    }

    public void setExcluido(Boolean excluido) {
        this.excluido = excluido;
    }

    public List<String> getCodigoMarcadores() {
        return codigoMarcadores;
    }

    public void setCodigoMarcadores(List<String> codigoMarcadores) {
        this.codigoMarcadores = codigoMarcadores;
    }
}
