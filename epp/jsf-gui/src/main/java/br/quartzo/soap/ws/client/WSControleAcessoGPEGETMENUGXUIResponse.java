
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
 *         &lt;element name="Jsonout" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "jsonout"
})
@XmlRootElement(name = "WSControleAcessoGPE.GETMENUGXUIResponse")
public class WSControleAcessoGPEGETMENUGXUIResponse {

    @XmlElement(name = "Jsonout", required = true)
    protected String jsonout;

    /**
     * Obtém o valor da propriedade jsonout.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonout() {
        return jsonout;
    }

    /**
     * Define o valor da propriedade jsonout.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonout(String value) {
        this.jsonout = value;
    }

}
