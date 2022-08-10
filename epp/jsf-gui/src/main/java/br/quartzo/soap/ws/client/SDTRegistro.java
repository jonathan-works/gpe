
package br.quartzo.soap.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de SDTRegistro complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="SDTRegistro"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="Colunas" type="{br.com.abaco.sisbase}ArrayOfSDTColuna"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDTRegistro", propOrder = {

})
public class SDTRegistro {

    @XmlElement(name = "Colunas", required = true)
    protected ArrayOfSDTColuna colunas;

    /**
     * Obtém o valor da propriedade colunas.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSDTColuna }
     *     
     */
    public ArrayOfSDTColuna getColunas() {
        return colunas;
    }

    /**
     * Define o valor da propriedade colunas.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSDTColuna }
     *     
     */
    public void setColunas(ArrayOfSDTColuna value) {
        this.colunas = value;
    }

}
