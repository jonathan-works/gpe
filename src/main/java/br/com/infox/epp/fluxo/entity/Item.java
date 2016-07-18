package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.ItemQuery.CAMINHO_COMPLETO;
import static br.com.infox.epp.fluxo.query.ItemQuery.CODIGO_ITEM;
import static br.com.infox.epp.fluxo.query.ItemQuery.DESCRICAO_ITEM;
import static br.com.infox.epp.fluxo.query.ItemQuery.ID_ITEM;
import static br.com.infox.epp.fluxo.query.ItemQuery.ID_ITEM_PAI;
import static br.com.infox.epp.fluxo.query.ItemQuery.ITEM_PAI_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.ItemQuery.SEQUENCE_ITEM;
import static br.com.infox.epp.fluxo.query.ItemQuery.TABLE_ITEM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.persistence.Recursive;
import br.com.infox.epp.fluxo.query.ItemQuery;

@Entity
@Table(name = TABLE_ITEM)
@NamedQueries(value = { @NamedQuery(name = ItemQuery.GET_FOLHAS, query = ItemQuery.GET_FOLHAS_QUERY) })
public class Item implements Serializable, Recursive<Item> {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_ITEM)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_ITEM, unique = true, nullable = false)
    private Integer idItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_ITEM_PAI)
    private Item itemPai;
    
    @NotNull
    @Size(min = 1, max = LengthConstants.DESCRICAO_PEQUENA)
    @Column(name = CODIGO_ITEM, length = LengthConstants.DESCRICAO_PEQUENA, nullable = false)
    private String codigoItem;
    
    @NotNull
    @Size(min = 1, max = LengthConstants.DESCRICAO_PADRAO)
    @Column(name = DESCRICAO_ITEM, nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    private String descricaoItem;
    
    @NotNull
    @Column(name = ATIVO, nullable = false)
    private Boolean ativo;
    
    @Column(name = CAMINHO_COMPLETO, unique = true)
    private String caminhoCompleto;
    
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = ITEM_PAI_ATTRIBUTE)
    private List<Item> itemList;

    public Item() {
        this(null, null, null, null);
    }

    public Item(String codigoItem, String descricaoItem, Boolean ativo) {
        this(codigoItem, descricaoItem, null, ativo);
    }

    public Item(String codigoItem, String descricaoItem, Item itemPai, Boolean ativo) {
        this.codigoItem = codigoItem;
        this.descricaoItem = descricaoItem;
        this.itemPai = itemPai;
        this.ativo = ativo;
    }

    public Integer getIdItem() {
        return this.idItem;
    }

    public void setIdItem(final Integer idItem) {
        this.idItem = idItem;
    }

    public Item getItemPai() {
        return this.itemPai;
    }

    public void setItemPai(final Item itemPai) {
        this.itemPai = itemPai;
    }

    public String getCodigoItem() {
        return this.codigoItem;
    }

    public void setCodigoItem(final String codigoItem) {
        this.codigoItem = codigoItem;
    }

    public String getDescricaoItem() {
        return this.descricaoItem;
    }

    public void setDescricaoItem(final String descricaoItem) {
        this.descricaoItem = descricaoItem;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(final Boolean ativo) {
        this.ativo = ativo;
    }

    public List<Item> getItemList() {
        return this.itemList;
    }

    public void setItemList(final List<Item> itemList) {
        this.itemList = itemList;
    }

    public String getCaminhoCompleto() {
        return caminhoCompleto;
    }

    public void setCaminhoCompleto(String caminhoCompleto) {
        this.caminhoCompleto = caminhoCompleto;
    }

    public String caminhoCompletoToString() {
        return caminhoCompleto.replace('|', '/').substring(0, caminhoCompleto.length() - 1);
    }

    @Override
    public String toString() {
        return descricaoItem;
    }

    @Transient
    public List<Item> getListItemAtePai() {
        final List<Item> list = new ArrayList<Item>();
        Item pai = getItemPai();
        while (pai != null) {
            list.add(pai);
            pai = pai.getItemPai();
        }
        return list;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        final Item other = (Item) obj;
        if (getIdItem() != other.getIdItem()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (idItem == null ? 0 : idItem.hashCode());
        return result;
    }

    @Override
    @Transient
    public Item getParent() {
        return this.getItemPai();
    }

    @Override
    public void setParent(final Item parent) {
        this.setItemPai(parent);
    }

    @Override
    @Transient
    public String getHierarchicalPath() {
        return this.getCaminhoCompleto();
    }

    @Override
    public void setHierarchicalPath(final String path) {
        this.setCaminhoCompleto(path);
    }

    @Override
    @Transient
    public String getPathDescriptor() {
        return this.getDescricaoItem();
    }

    @Override
    public void setPathDescriptor(final String pathDescriptor) {
        this.setDescricaoItem(pathDescriptor);
    }

    @Override
    @Transient
    public List<Item> getChildList() {
        List<Item> ret = getItemList();
        if (ret == null) {
            ret = new ArrayList<>();
        }
        return ret;
    }

    @Override
    public void setChildList(final List<Item> childList) {
        this.setItemList(childList);
    }

}
