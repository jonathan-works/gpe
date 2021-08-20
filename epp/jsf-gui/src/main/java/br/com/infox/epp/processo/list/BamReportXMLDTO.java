package br.com.infox.epp.processo.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "BAM", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BamReportXMLDTO {

    @XmlElement(name = "item")
    private List<BamReportVO> processos = new ArrayList<>();
}
