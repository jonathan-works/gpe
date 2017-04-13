package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_SESSAO_ANEXAR;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_SESSAO_ANEXAR_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_TASKINSTANCE;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_MINUTA_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_MINUTA_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.NEXT_SEQUENCIAL_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.TOTAL_DOCUMENTOS_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.TOTAL_DOCUMENTOS_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.com.infox.core.manager.AbstractEntityListener;
import br.com.infox.core.manager.EntityListener;
import br.com.infox.core.manager.EntityListenerService;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.domain.Assinatura;
import br.com.infox.epp.documento.domain.Assinavel;
import br.com.infox.epp.documento.domain.RegraAssinatura;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.publicacao.PublicacaoDocumento;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.manager.DocumentoEntityListener;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = Documento.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = NEXT_SEQUENCIAL, query = NEXT_SEQUENCIAL_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_BY_PROCESSO, query = LIST_DOCUMENTO_BY_PROCESSO_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_MINUTA_BY_PROCESSO, query = LIST_DOCUMENTO_MINUTA_BY_PROCESSO_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_BY_TASKINSTANCE, query = lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY),
    @NamedQuery(name = TOTAL_DOCUMENTOS_PROCESSO, query = TOTAL_DOCUMENTOS_PROCESSO_QUERY),
    @NamedQuery(name = DOCUMENTOS_SESSAO_ANEXAR, query = DOCUMENTOS_SESSAO_ANEXAR_QUERY),
    @NamedQuery(name = DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO, query = DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO_QUERY)
})
@Indexed(index="IndexProcessoDocumento")
@EqualsAndHashCode(of = "id")
@ToString(of = "descricao")
@EntityListeners(value = EntityListenerService.class)
public class Documento implements Serializable, Cloneable, EntityListener<Documento>, Assinavel {

    private static final long serialVersionUID = 1L;
    public static final int TAMANHO_MAX_DESCRICAO_DOCUMENTO = 260;
    public static final String TABLE_NAME = "tb_documento";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "DocumentoGenerator", sequenceName = "sq_documento")
    @GeneratedValue(generator = "DocumentoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_documento", unique = true, nullable = false)
    @Getter @Setter
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_classificacao_documento", nullable = false)
    @Getter @Setter
    private ClassificacaoDocumento classificacaoDocumento;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento_bin", nullable = false)
    @Getter @Setter
    private DocumentoBin documentoBin;
    
    @NotNull
    @Size(max = TAMANHO_MAX_DESCRICAO_DOCUMENTO)
    @Column(name = "ds_documento", nullable = false, length = TAMANHO_MAX_DESCRICAO_DOCUMENTO)
    @Getter @Setter
    private String descricao;
    
    @Column(name = "nr_documento", nullable = true)
    @Getter @Setter
    private Integer numeroDocumento;
    
    @NotNull
    @Column(name = "in_documento_sigiloso", nullable = false)
    @Getter @Setter
    private Boolean documentoSigiloso = Boolean.FALSE;
    
    @NotNull
    @Column(name = "in_anexo", nullable = false)
    @Getter @Setter
    private Boolean anexo = Boolean.FALSE;
    
    @Column(name = "id_jbpm_task")
    @Getter @Setter
    private Long idJbpmTask;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_template", nullable = true)
    @Getter @Setter
    private PerfilTemplate perfilTemplate;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inclusao", nullable = false)
    @Getter @Setter
    private Date dataInclusao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_inclusao")
    @Getter @Setter
    private UsuarioLogin usuarioInclusao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_alteracao", nullable = false)
    @Getter @Setter
    private Date dataAlteracao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_alteracao")
    @Getter @Setter
    private UsuarioLogin usuarioAlteracao;
    
    @NotNull
    @Column(name="in_excluido", nullable = false)
    @Getter @Setter
    private Boolean excluido;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name="id_pasta", nullable = false)
    @Getter @Setter
    private Pasta pasta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao")
    @Getter @Setter
    private Localizacao localizacao;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documento", cascade = CascadeType.REMOVE)
    @OrderBy(value="dataAlteracao DESC")
    @Getter @Setter
    private List<HistoricoStatusDocumento> historicoStatusDocumentoList = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documento")
    @OrderBy(value="dataPublicacao DESC")
    @Getter @Setter
    private List<PublicacaoDocumento> publicacoes = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documento")
    @Getter @Setter
    private List<SigiloDocumento> sigiloDocumento = new ArrayList<>();
    
    public boolean isDocumentoAssinavel(Papel papel){
		if (getDocumentoBin() == null || getClassificacaoDocumento() == null) {
			return false;
		}
		if(getPasta().getProcesso() != null && getPasta().getProcesso().isFinalizado()) {
    		return false;
    	}
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
		for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : papeis){
			if (tipoProcessoDocumentoPapel.getPapel().equals(papel) 
					&& tipoProcessoDocumentoPapel.getTipoAssinatura() != TipoAssinaturaEnum.P){
				return true;
			}
		}
		return false;
    }
    
    public boolean isDocumentoAssinavel(){
    	if (getDocumentoBin() == null || getDocumentoBin().isMinuta()) {
			return false;
		}
    	if(getPasta().getProcesso() != null && getPasta().getProcesso().isFinalizado()) {
    		return false;
    	}
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
		for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : papeis){
			if (tipoProcessoDocumentoPapel.getTipoAssinatura() != TipoAssinaturaEnum.P){
				return true;
			}
		}
		return false;
    }
    
    public boolean isDocumentoAssinado(Papel papel){
    	for(AssinaturaDocumento assinaturaDocumento : getDocumentoBin().getAssinaturas()){
    		if (assinaturaDocumento.getPapel().equals(papel)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isDocumentoAssinado(UsuarioLogin usuarioLogin){
    	return isDocumentoAssinado(usuarioLogin.getPessoaFisica());
    }
    
    public boolean isDocumentoAssinado(PessoaFisica pessoa){
        for(AssinaturaDocumento assinaturaDocumento : getDocumentoBin().getAssinaturas()){
            if (assinaturaDocumento.getPessoaFisica().equals(pessoa)){
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAssinaturaSuficiente() {
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
    	for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : papeis) {
    		if (classificacaoDocumentoPapel.getTipoAssinatura() == TipoAssinaturaEnum.S
    				&& isDocumentoAssinado(classificacaoDocumentoPapel.getPapel())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean hasAssinatura(){
    	return getDocumentoBin() != null 
    				&& getDocumentoBin().getAssinaturas() != null 
    				&& getDocumentoBin().getAssinaturas().size() > 0;
    }
    
    public boolean isAssinaturaObrigatoria(Papel papel) {
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
    	boolean existeObrigatoria=false;
    	TipoAssinaturaEnum tipoAssinaturaEncontrado=null;
        for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : papeis) {
            switch (classificacaoDocumentoPapel.getTipoAssinatura()) {
            case O:
                existeObrigatoria |= true;
                break;
            default:
                break;
            }
            if (classificacaoDocumentoPapel.getPapel().equals(papel)) {
                tipoAssinaturaEncontrado = classificacaoDocumentoPapel.getTipoAssinatura();
            }
        }
    	return TipoAssinaturaEnum.O.equals(tipoAssinaturaEncontrado) || (TipoAssinaturaEnum.S.equals(tipoAssinaturaEncontrado) && !existeObrigatoria);
    }
    
    public boolean isDocumentoAssinavel(PessoaFisica pessoaFisica, Papel papel) {
        if (getClassificacaoDocumento() == null || !isDocumentoAssinavel(papel)) {
            return false;
        }

        boolean pessoaAssinouDocumento = isDocumentoAssinado(pessoaFisica);
        boolean papelAssinouDocumento = isDocumentoAssinado(papel);
        boolean permiteAssinaturaMultipla = papelPermiteAssinaturaMultipla(papel);
        
        return !pessoaAssinouDocumento && (permiteAssinaturaMultipla || !papelAssinouDocumento);
    }
    
    public boolean papelPermiteAssinaturaMultipla(Papel papel) {
        if (getClassificacaoDocumento() == null) {
            return false;
        }
        
        for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : getClassificacaoDocumento().getClassificacaoDocumentoPapelList()) {
            if (classificacaoDocumentoPapel.getPapel().equals(papel)) {
                return classificacaoDocumentoPapel.getAssinaturasMultiplas();
            }
        }
        
        return false;
    }
    
    @Transient
    @Field(index = Index.YES, store = Store.NO, name = "texto")
    public String getTextoIndexavel() {
        return getDocumentoBin().getModeloDocumento();
    }

    @Transient
    @Field(index = Index.YES, store = Store.NO, name = "nome")
    public String getNomeIndexavel() {
        return getDescricao();
    }
	
	public Documento makeCopy() throws CloneNotSupportedException {
		Documento cDocumento = (Documento) clone();
		cDocumento.setId(null);
		List<HistoricoStatusDocumento> cList = new ArrayList<>();
		for (HistoricoStatusDocumento hsd : cDocumento.getHistoricoStatusDocumentoList()) {
			HistoricoStatusDocumento cHistorico = hsd.makeCopy();
			cHistorico.setDocumento(cDocumento);
			cList.add(cHistorico);
		}
		cDocumento.setHistoricoStatusDocumentoList(cList);
		
		List<PublicacaoDocumento> listPub = new ArrayList<>();
		for (PublicacaoDocumento pd : cDocumento.getPublicacoes()) {
			PublicacaoDocumento cPub = pd.makeCopy();
			cPub.setDocumento(cDocumento);
			listPub.add(cPub);
		}
		cDocumento.setPublicacoes(listPub);
		return cDocumento;
	}

    @Override
    public Class<? extends AbstractEntityListener<Documento>> getServiceClass() {
        return DocumentoEntityListener.class;
    }

    @Override
    public List<? extends RegraAssinatura> getRegrasAssinatura() {
        return getClassificacaoDocumento()==null ? Collections.<RegraAssinatura>emptyList() : getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
    }

    @Override
    public List<? extends Assinatura> getAssinaturas() {
        return getDocumentoBin()==null? Collections.<Assinatura>emptyList() : getDocumentoBin().getAssinaturas();
    }

}
