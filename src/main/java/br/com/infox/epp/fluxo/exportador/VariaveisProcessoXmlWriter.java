package br.com.infox.epp.fluxo.exportador;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

public class VariaveisProcessoXmlWriter {
	
	private static final LogProvider LOG = Logging.getLogProvider(VariaveisProcessoXmlWriter.class);
	
	public String getVariablesAsXml(List<DefinicaoVariavelProcesso> processVariables) {
		try {
			JAXBContext jc = JAXBContext.newInstance(FluxoConfiguration.class);

			FluxoConfiguration rootElement = new FluxoConfiguration();
			rootElement.populateVariaveisProcessoConfiguration(processVariables);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(rootElement, baos);
			return baos.toString();
		} catch (JAXBException e) {
			LOG.error("Erro ao gerar o xml para as variáveis do processo.", e);
			throw new BusinessException("Erro ao gerar o xml para as variáveis do processo.");
		}
	}
	
}
