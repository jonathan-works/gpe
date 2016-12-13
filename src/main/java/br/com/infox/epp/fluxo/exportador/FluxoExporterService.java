package br.com.infox.epp.fluxo.exportador;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoSearch;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Stateless
public class FluxoExporterService {
	
	@Inject
	private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
	
	public byte[] exportarFluxo(Fluxo fluxo) throws IOException, JAXBException {
		VariaveisProcessoXmlWriter variablesWriter = new VariaveisProcessoXmlWriter();
		String xmlVariables = variablesWriter.getVariablesAsXml(definicaoVariavelProcessoSearch.listVariaveisByFluxo(fluxo));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		
		ZipEntry ze = new ZipEntry(FluxoConfiguration.PROCESS_VARIABLES_XML);
		zos.putNextEntry(ze);
		zos.write(xmlVariables.getBytes());
		zos.closeEntry();
		
		ze = new ZipEntry(FluxoConfiguration.FLUXO_XML);
		zos.putNextEntry(ze);
		zos.write(fluxo.getXml().getBytes());
		zos.closeEntry();
		
		zos.close();
		return baos.toByteArray();
	}
}
