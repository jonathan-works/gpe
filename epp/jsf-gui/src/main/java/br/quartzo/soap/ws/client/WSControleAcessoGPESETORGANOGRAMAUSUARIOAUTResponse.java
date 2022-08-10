
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
 *         &lt;element name="Registroerro" type="{br.com.abaco.sisbase}SDTRegistro"/&gt;
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
    "registroerro"
})
@XmlRootElement(name = "WSControleAcessoGPE.SETORGANOGRAMAUSUARIOAUTResponse")
public class WSControleAcessoGPESETORGANOGRAMAUSUARIOAUTResponse {

    @XmlElement(name = "Registroerro", required = true)
    protected SDTRegistro registroerro;

    /**
     * Obtém o valor da propriedade registroerro.
     * 
     * @return
     *     possible object is
     *     {@link SDTRegistro }
     *     
     */
    public SDTRegistro getRegistroerro() {
        return registroerro;
    }

    /**
     * Define o valor da propriedade registroerro.
     * 
     * @param value
     *     allowed object is
     *     {@link SDTRegistro }
     *     
     */
    public void setRegistroerro(SDTRegistro value) {
        this.registroerro = value;
    }

}
