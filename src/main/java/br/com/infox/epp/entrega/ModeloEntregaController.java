package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega;

public class ModeloEntregaController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    private List<CategoriaEntregaItem> items;
    private Date dataLimite;
    private Date dataLiberacao;
    private ModeloDocumento modeloCertidao;
    private List<CategoriaEntregaItem> itens;
    private List<TipoResponsavelEntrega> tiposResponsaveis;
    private List<ClassificacaoDocumentoEntrega> documentosEntrega;
    
    @PostConstruct
    public void init() {
        items = new ArrayList<>();
    }
    
    public void iniciarConfiguracao(String[] path) {
        for (String codigoItem : path) {
            items.add(categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItem));
        }
    }
    
    public List<CategoriaEntregaItem> getItems() {
        return items;
    }
    
}
