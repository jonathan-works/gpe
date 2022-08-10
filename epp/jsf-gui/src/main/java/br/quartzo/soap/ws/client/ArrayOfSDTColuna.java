
package br.quartzo.soap.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ArrayOfSDTColuna complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSDTColuna"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SDTColuna" type="{br.com.abaco.sisbase}SDTColuna" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSDTColuna", propOrder = {
    "sdtColuna"
})
public class ArrayOfSDTColuna {

    @XmlElement(name = "SDTColuna")
    protected List<SDTColuna> sdtColuna;

    /**
     * Gets the value of the sdtColuna property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sdtColuna property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSDTColuna().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SDTColuna }
     * 
     * 
     */
    public List<SDTColuna> getSDTColuna() {
        if (sdtColuna == null) {
            sdtColuna = new ArrayList<SDTColuna>();
        }
        return this.sdtColuna;
    }

}
