
package br.quartzo.soap.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de SDTOrgaoGPE complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="SDTOrgaoGPE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="OrgnId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="OrgnNome" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDTOrgaoGPE", propOrder = {

})
public class SDTOrgaoGPE {

    @XmlElement(name = "OrgnId", required = true)
    protected String orgnId;
    @XmlElement(name = "OrgnNome", required = true)
    protected String orgnNome;

    /**
     * Obtém o valor da propriedade orgnId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnId() {
        return orgnId;
    }

    /**
     * Define o valor da propriedade orgnId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnId(String value) {
        this.orgnId = value;
    }

    /**
     * Obtém o valor da propriedade orgnNome.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnNome() {
        return orgnNome;
    }

    /**
     * Define o valor da propriedade orgnNome.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnNome(String value) {
        this.orgnNome = value;
    }

}
