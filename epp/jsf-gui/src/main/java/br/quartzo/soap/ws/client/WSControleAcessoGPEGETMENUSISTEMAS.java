
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
 *         &lt;element name="Usuid" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
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
    "usuid"
})
@XmlRootElement(name = "WSControleAcessoGPE.GETMENUSISTEMAS")
public class WSControleAcessoGPEGETMENUSISTEMAS {

    @XmlElement(name = "Usuid")
    protected long usuid;

    /**
     * Obtém o valor da propriedade usuid.
     * 
     */
    public long getUsuid() {
        return usuid;
    }

    /**
     * Define o valor da propriedade usuid.
     * 
     */
    public void setUsuid(long value) {
        this.usuid = value;
    }

}
