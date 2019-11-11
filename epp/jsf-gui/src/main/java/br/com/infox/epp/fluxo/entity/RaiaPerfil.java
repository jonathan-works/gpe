package br.com.infox.epp.fluxo.entity;

import java.io.Serializable;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.infox.core.persistence.ORConstants;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.fluxo.query.RaiaPerfilQuery;

@Entity
@Table(name = RaiaPerfilQuery.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        RaiaPerfilQuery.COLUMN_RAIA, RaiaPerfilQuery.COLUMN_FLUXO, RaiaPerfilQuery.COLUMN_PERFIL
    })
})
@NamedQueries({
    @NamedQuery(name = RaiaPerfilQuery.LIST_BY_PERFIL, query = RaiaPerfilQuery.LIST_BY_PERFIL_QUERY),
    @NamedQuery(name = RaiaPerfilQuery.LIST_BY_FLUXO, query = RaiaPerfilQuery.LIST_BY_FLUXO_QUERY),
    @NamedQuery(name = RaiaPerfilQuery.LIST_BY_LOCALIZACAO, query = RaiaPerfilQuery.LIST_BY_LOCALIZACAO_QUERY),
    @NamedQuery(name = RaiaPerfilQuery.REMOVER_RAIAS_PERFIS_POR_FLUXO, query = RaiaPerfilQuery.REMOVER_RAIAS_PERFIS_POR_FLUXO_QUERY)
})
public class RaiaPerfil implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name = ORConstants.GENERATOR, sequenceName = RaiaPerfilQuery.SEQUENCE_NAME, allocationSize = 1)
    @GeneratedValue(generator = ORConstants.GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = RaiaPerfilQuery.COLUMN_ID)
    private Integer id;
    
    @Column(name = RaiaPerfilQuery.COLUMN_RAIA, nullable = false)
    private String nomeRaia;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = RaiaPerfilQuery.COLUMN_FLUXO, nullable = false)
    private Fluxo fluxo;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = RaiaPerfilQuery.COLUMN_PERFIL, nullable = false)
    private PerfilTemplate perfilTemplate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeRaia() {
        return nomeRaia;
    }
    
    public void setNomeRaia(String nomeRaia) {
        this.nomeRaia = nomeRaia;
    }
    
    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public PerfilTemplate getPerfilTemplate() {
        return perfilTemplate;
    }

    public void setPerfilTemplate(PerfilTemplate perfilTemplate) {
        this.perfilTemplate = perfilTemplate;
    }
}
