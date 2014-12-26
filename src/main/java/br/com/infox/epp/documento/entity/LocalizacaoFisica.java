package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.documento.query.LocalizacaoFisicaQuery.CAMINHO_COMPLETO;
import static br.com.infox.epp.documento.query.LocalizacaoFisicaQuery.DESCRICAO_LOCALIZACAO_FISICA;
import static br.com.infox.epp.documento.query.LocalizacaoFisicaQuery.ID_LOCALIZACAO_FISICA;
import static br.com.infox.epp.documento.query.LocalizacaoFisicaQuery.ID_LOCALIZACAO_FISICA_PAI;
import static br.com.infox.epp.documento.query.LocalizacaoFisicaQuery.LOCALIZACAO_FISICA_PAI_ATTRIBUTE;
import static br.com.infox.epp.documento.query.LocalizacaoFisicaQuery.SEQUENCE_LOCALIZACAO_FISICA;
import static br.com.infox.epp.documento.query.LocalizacaoFisicaQuery.TABLE_LOCALIZACAO_FISICA;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.persistence.Recursive;

@Entity
@Table(name = TABLE_LOCALIZACAO_FISICA)
public class LocalizacaoFisica implements Serializable, Recursive<LocalizacaoFisica> {

    private static final long serialVersionUID = 1L;

    private int idLocalizacaoFisica;
    private LocalizacaoFisica localizacaoFisicaPai;
    private String descricao;
    private String caminhoCompleto;
    private Boolean ativo;
    private List<LocalizacaoFisica> localizacaoFisicaList = new ArrayList<>(0);

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_LOCALIZACAO_FISICA)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_LOCALIZACAO_FISICA, unique = true, nullable = false)
    public int getIdLocalizacaoFisica() {
        return idLocalizacaoFisica;
    }

    public void setIdLocalizacaoFisica(int idLocalizacaoFisica) {
        this.idLocalizacaoFisica = idLocalizacaoFisica;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_LOCALIZACAO_FISICA_PAI)
    public LocalizacaoFisica getLocalizacaoFisicaPai() {
        return localizacaoFisicaPai;
    }

    public void setLocalizacaoFisicaPai(LocalizacaoFisica localizacaoFisicaPai) {
        this.localizacaoFisicaPai = localizacaoFisicaPai;
    }

    @Column(name = DESCRICAO_LOCALIZACAO_FISICA, nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    @Size(max = LengthConstants.NOME_PADRAO)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = CAMINHO_COMPLETO, unique = true)
    public String getCaminhoCompleto() {
        return caminhoCompleto;
    }

    public void setCaminhoCompleto(String caminhoCompleto) {
        this.caminhoCompleto = caminhoCompleto;
    }

    @Column(name = ATIVO, nullable = false)
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String caminhoCompletoToString() {
        return caminhoCompleto.replace('|', '/').substring(0, caminhoCompleto.length() - 1);
    }

    @Transient
    public List<LocalizacaoFisica> getListLocalizacaoFisicaAtePai() {
        List<LocalizacaoFisica> list = new ArrayList<LocalizacaoFisica>();
        LocalizacaoFisica pai = getLocalizacaoFisicaPai();
        while (pai != null) {
            list.add(pai);
            pai = pai.getLocalizacaoFisicaPai();
        }
        return list;
    }

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
        CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = LOCALIZACAO_FISICA_PAI_ATTRIBUTE)
    public List<LocalizacaoFisica> getLocalizacaoFisicaList() {
        return this.localizacaoFisicaList;
    }

    public void setLocalizacaoFisicaList(
            List<LocalizacaoFisica> localizacaoFisicaList) {
        this.localizacaoFisicaList = localizacaoFisicaList;
    }

    @Override
    @Transient
    public LocalizacaoFisica getParent() {
        return this.getLocalizacaoFisicaPai();
    }

    @Override
    public void setParent(LocalizacaoFisica parent) {
        this.setLocalizacaoFisicaPai(parent);
    }

    @Override
    @Transient
    public String getHierarchicalPath() {
        return this.getCaminhoCompleto();
    }

    @Override
    public void setHierarchicalPath(String path) {
        this.setCaminhoCompleto(path);
    }

    @Override
    @Transient
    public String getPathDescriptor() {
        return this.getDescricao();
    }

    @Override
    public void setPathDescriptor(String pathDescriptor) {
        this.setDescricao(pathDescriptor);
    }

    @Override
    @Transient
    public List<LocalizacaoFisica> getChildList() {
        return this.getLocalizacaoFisicaList();
    }

    @Override
    public void setChildList(List<LocalizacaoFisica> childList) {
        this.setLocalizacaoFisicaList(childList);
    }

}
