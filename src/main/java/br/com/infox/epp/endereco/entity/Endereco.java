package br.com.infox.epp.endereco.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.view.municipio.MunicipioDAO;
import br.com.infox.seam.util.ComponentUtil;

@Entity
@Table(name=Endereco.TABLE_NAME, schema="tce")
public class Endereco implements Serializable {
    public static final String TABLE_NAME = "tb_endereco";
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="EnderecoGenerator", sequenceName="tce.sq_endereco", initialValue=1, allocationSize=1)
    @GeneratedValue(generator="EnderecoGenerator", strategy=GenerationType.SEQUENCE)
    @Column(name="id_endereco", insertable = false, updatable = false, unique = true)
    private Integer id;
    
    @Column(name = "ds_logradouro")
    private String logradouro;
    
    @Column(name = "ds_complemento")
    private String complemento;
    
    @Column(name = "ds_numero")
    private String numero;
    
    @Column(name = "ds_bairro")
    private String bairro;
    
    @Column(name = "ds_cep")
    private String cep;
    
    @Column(name = "cd_municipio")
    private String codigoMunicipio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    /*@Transient
    public String getNomeMunicipio() {
        String nomeMunicipio;
        if (codigoMunicipio == null) {
            nomeMunicipio = null;
        } else {
            try {
                MunicipioDAO municipioTceDAO = (MunicipioDAO) ComponentUtil.getComponent(MunicipioDAO.NAME);
                nomeMunicipio = municipioTceDAO.getNomeMunicipio(codigoMunicipio);
            } catch (DAOException e) {
                nomeMunicipio = codigoMunicipio;
            }
        }
        return nomeMunicipio;
    }*/
}