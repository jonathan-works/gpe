
package br.quartzo.soap.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ArrayOfSDTOrgaoGPE complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSDTOrgaoGPE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SDTOrgaoGPE" type="{br.com.abaco.sisbase}SDTOrgaoGPE" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSDTOrgaoGPE", propOrder = {
    "sdtOrgaoGPE"
})
public class ArrayOfSDTOrgaoGPE {

    @XmlElement(name = "SDTOrgaoGPE")
    protected List<SDTOrgaoGPE> sdtOrgaoGPE;

    /**
     * Gets the value of the sdtOrgaoGPE property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sdtOrgaoGPE property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSDTOrgaoGPE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SDTOrgaoGPE }
     * 
     * 
     */
    public List<SDTOrgaoGPE> getSDTOrgaoGPE() {
        if (sdtOrgaoGPE == null) {
            sdtOrgaoGPE = new ArrayList<SDTOrgaoGPE>();
        }
        return this.sdtOrgaoGPE;
    }

}
