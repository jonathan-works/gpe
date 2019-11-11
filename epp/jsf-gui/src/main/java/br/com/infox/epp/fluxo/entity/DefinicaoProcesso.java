package br.com.infox.epp.fluxo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_definicao_processo")
public class DefinicaoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DefinicaoProcessoGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_definicao_processo")
    @GeneratedValue(generator = "DefinicaoProcessoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_definicao_processo")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fluxo", nullable = false, unique = true)
    private Fluxo fluxo;
    
    @Column(name = "ds_xml")
    private String xml;
    
    @Column(name = "ds_xml_exec")
    private String xmlExecucao;
    
    @Column(name = "ds_bpmn")
    private String bpmn;
    
    @Column(name = "ds_svg")
    private String svg;

    @Column(name = "ds_svg_exec")
    private String svgExecucao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getXmlExecucao() {
        return xmlExecucao;
    }

    public void setXmlExecucao(String xmlExecucao) {
        this.xmlExecucao = xmlExecucao;
    }

    public String getBpmn() {
        return bpmn;
    }

    public void setBpmn(String bpmn) {
        this.bpmn = bpmn;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public String getSvgExecucao() {
        return svgExecucao;
    }

    public void setSvgExecucao(String svgExecucao) {
        this.svgExecucao = svgExecucao;
    }
}
