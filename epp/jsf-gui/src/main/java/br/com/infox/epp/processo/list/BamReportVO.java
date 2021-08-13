package br.com.infox.epp.processo.list;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class BamReportVO {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private String nrProcesso;
    private String tarefa;
    @XmlElement(name = "assunto")
    private String fluxo;
    @XmlElement(name = "departamento")
    private String localizacao;
    private String usuario;
    private Date dataInicio;
    private Date dataFim;

    public String getDepartamentoUsuario() {
        if(localizacao == null || usuario == null) {
            return "/";
        }

        return String.format("%s / %s", localizacao, usuario);
    }

    public String getDataInicioFormatada() {
        if(dataInicio == null) {
            return "";
        }

        return getDataFormatada(dataInicio);
    }

    public String getDataFimFormatada() {
        if(dataFim == null) {
            return "";
        }

        return getDataFormatada(dataFim);
    }

    private String getDataFormatada(Date data) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(data);
    }

}
