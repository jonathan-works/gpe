
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
 *         &lt;element name="Messages" type="{Genexus}Messages"/&gt;
 *         &lt;element name="Sdtusuariogpe" type="{br.com.abaco.sisbase}SDTUsuarioGPE"/&gt;
 *         &lt;element name="Sdtperfilusuariogpe" type="{br.com.abaco.sisbase}ArrayOfSDTPerfilUsuarioGPE"/&gt;
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
    "messages",
    "sdtusuariogpe",
    "sdtperfilusuariogpe"
})
@XmlRootElement(name = "WSControleAcessoGPE.VALIDARLOGINACESSOResponse")
public class WSControleAcessoGPEVALIDARLOGINACESSOResponse {

    @XmlElement(name = "Messages", required = true)
    protected Messages messages;
    @XmlElement(name = "Sdtusuariogpe", required = true)
    protected SDTUsuarioGPE sdtusuariogpe;
    @XmlElement(name = "Sdtperfilusuariogpe", required = true)
    protected ArrayOfSDTPerfilUsuarioGPE sdtperfilusuariogpe;

    /**
     * Obtém o valor da propriedade messages.
     * 
     * @return
     *     possible object is
     *     {@link Messages }
     *     
     */
    public Messages getMessages() {
        return messages;
    }

    /**
     * Define o valor da propriedade messages.
     * 
     * @param value
     *     allowed object is
     *     {@link Messages }
     *     
     */
    public void setMessages(Messages value) {
        this.messages = value;
    }

    /**
     * Obtém o valor da propriedade sdtusuariogpe.
     * 
     * @return
     *     possible object is
     *     {@link SDTUsuarioGPE }
     *     
     */
    public SDTUsuarioGPE getSdtusuariogpe() {
        return sdtusuariogpe;
    }

    /**
     * Define o valor da propriedade sdtusuariogpe.
     * 
     * @param value
     *     allowed object is
     *     {@link SDTUsuarioGPE }
     *     
     */
    public void setSdtusuariogpe(SDTUsuarioGPE value) {
        this.sdtusuariogpe = value;
    }

    /**
     * Obtém o valor da propriedade sdtperfilusuariogpe.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSDTPerfilUsuarioGPE }
     *     
     */
    public ArrayOfSDTPerfilUsuarioGPE getSdtperfilusuariogpe() {
        return sdtperfilusuariogpe;
    }

    /**
     * Define o valor da propriedade sdtperfilusuariogpe.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSDTPerfilUsuarioGPE }
     *     
     */
    public void setSdtperfilusuariogpe(ArrayOfSDTPerfilUsuarioGPE value) {
        this.sdtperfilusuariogpe = value;
    }

}
