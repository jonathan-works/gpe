
package br.quartzo.soap.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Sisid" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Usulogin" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ususenha" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Chave" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sisid",
    "usulogin",
    "ususenha",
    "chave"
})
@XmlRootElement(name = "WSControleAcessoGPE.VALIDARLOGINACESSO")
public class WSControleAcessoGPEVALIDARLOGINACESSO {

    @XmlElement(name = "Sisid")
    protected int sisid;
    @XmlElement(name = "Usulogin", required = true)
    protected String usulogin;
    @XmlElement(name = "Ususenha", required = true)
    protected String ususenha;
    @XmlElement(name = "Chave", required = true)
    protected String chave;

    /**
     * Obtém o valor da propriedade sisid.
     * 
     */
    public int getSisid() {
        return sisid;
    }

    /**
     * Define o valor da propriedade sisid.
     * 
     */
    public void setSisid(int value) {
        this.sisid = value;
    }

    /**
     * Obtém o valor da propriedade usulogin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsulogin() {
        return usulogin;
    }

    /**
     * Define o valor da propriedade usulogin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsulogin(String value) {
        this.usulogin = value;
    }

    /**
     * Obtém o valor da propriedade ususenha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsusenha() {
        return ususenha;
    }

    /**
     * Define o valor da propriedade ususenha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsusenha(String value) {
        this.ususenha = value;
    }

    /**
     * Obtém o valor da propriedade chave.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChave() {
        return chave;
    }

    /**
     * Define o valor da propriedade chave.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChave(String value) {
        this.chave = value;
    }

}
