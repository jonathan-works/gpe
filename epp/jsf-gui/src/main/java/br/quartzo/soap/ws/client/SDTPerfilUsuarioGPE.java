
package br.quartzo.soap.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de SDTPerfilUsuarioGPE complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="SDTPerfilUsuarioGPE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="PerfId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="PerfNome" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="OrgaoGPE" type="{br.com.abaco.sisbase}ArrayOfSDTOrgaoGPE"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDTPerfilUsuarioGPE", propOrder = {

})
public class SDTPerfilUsuarioGPE {

    @XmlElement(name = "PerfId", required = true)
    protected String perfId;
    @XmlElement(name = "PerfNome", required = true)
    protected String perfNome;
    @XmlElement(name = "OrgaoGPE", required = true)
    protected ArrayOfSDTOrgaoGPE orgaoGPE;

    /**
     * Obtém o valor da propriedade perfId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerfId() {
        return perfId;
    }

    /**
     * Define o valor da propriedade perfId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerfId(String value) {
        this.perfId = value;
    }

    /**
     * Obtém o valor da propriedade perfNome.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerfNome() {
        return perfNome;
    }

    /**
     * Define o valor da propriedade perfNome.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerfNome(String value) {
        this.perfNome = value;
    }

    /**
     * Obtém o valor da propriedade orgaoGPE.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSDTOrgaoGPE }
     *     
     */
    public ArrayOfSDTOrgaoGPE getOrgaoGPE() {
        return orgaoGPE;
    }

    /**
     * Define o valor da propriedade orgaoGPE.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSDTOrgaoGPE }
     *     
     */
    public void setOrgaoGPE(ArrayOfSDTOrgaoGPE value) {
        this.orgaoGPE = value;
    }

}
