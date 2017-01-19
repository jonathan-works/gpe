package br.com.infox.epp.fluxo.exportador;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

import br.com.infox.epp.fluxo.entity.Fluxo;

@Stateless
public class FluxoExporterService {

    public static final String FLUXO_XML = "fluxo.xml";
    public static final String FLUXO_BPMN = "diagram.bpmn";

    public byte[] exportarFluxo(Fluxo fluxo) throws IOException, JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        ZipEntry ze = new ZipEntry(FLUXO_XML);
        zos.putNextEntry(ze);
        zos.write(fluxo.getDefinicaoProcesso().getXml().getBytes());
        zos.closeEntry();

        if (fluxo.getDefinicaoProcesso().getBpmn() != null && !fluxo.getDefinicaoProcesso().getBpmn().isEmpty()) {
            ze = new ZipEntry(FLUXO_BPMN);
            zos.putNextEntry(ze);
            zos.write(fluxo.getDefinicaoProcesso().getBpmn().getBytes());
            zos.closeEntry();
        }

        zos.close();
        return baos.toByteArray();
    }
}
