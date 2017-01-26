package br.com.infox.epp.endereco;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.epp.municipio.Municipio;

//TODO corrigir para integrar epp 2.14
//@Entity
//@Table(name=Endereco.TABLE_NAME)
public class Endereco implements Serializable {
    public static final String TABLE_NAME = "tb_endereco";
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="EnderecoGenerator", sequenceName="sq_endereco", initialValue=1, allocationSize=1)
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
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_municipio", nullable = false)
    private Municipio municipio;

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
    
    public Municipio getMunicipio() {
		return municipio;
	}
    
    public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}
}