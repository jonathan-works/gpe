package br.com.infox.ibpm.jpdl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

public class VariaveisProcessoXmlWriter {
	
	private static final LogProvider LOG = Logging.getLogProvider(VariaveisProcessoXmlWriter.class);
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int DEFAULT_IDENT_SIZE = 4;
	
	public String getVariablesAsXml(List<DefinicaoVariavelProcesso> processVariables) {
		try {
			StringWriter stringWriter = new StringWriter();
			Document document = createDomTree(processVariables);
			OutputFormat outputFormat = OutputFormat.createPrettyPrint();
			outputFormat.setIndentSize(DEFAULT_IDENT_SIZE);
			outputFormat.setEncoding(DEFAULT_ENCODING);
			
			XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
			xmlWriter.write(document);
			xmlWriter.flush();
			stringWriter.flush();
			return stringWriter.toString();
		} catch (IOException e) {
			LOG.error("Erro ao gerar o xml para as variáveis do processo.", e);
			throw new BusinessException("Erro ao gerar o xml para as variáveis do processo.");
		}
	}

	private Document createDomTree(List<DefinicaoVariavelProcesso> definicaoVariaveisProcesso) {
		Document document = DocumentHelper.createDocument();
        Element root = document.addElement("variables"); //FIXME precisaria colocar alguma coisa pra relacionar ao processDefinition?
        for (DefinicaoVariavelProcesso definicaoVariavelProcesso : definicaoVariaveisProcesso) {
			writeProcessVariable(root, definicaoVariavelProcesso);
		}
		return document;
	}

	private void writeProcessVariable(Element parentElement, DefinicaoVariavelProcesso definicaoVariavelProcesso) {
		Element varElement = parentElement.addElement("process-variable");
		varElement.addAttribute("name", definicaoVariavelProcesso.getNome());
		varElement.addAttribute("label", definicaoVariavelProcesso.getLabel());
		if (definicaoVariavelProcesso.getValorPadrao() != null) {
			varElement.addAttribute("default-value", definicaoVariavelProcesso.getValorPadrao());
		}
		if (definicaoVariavelProcesso.getVisivel() != null) {
			varElement.addAttribute ("visible", definicaoVariavelProcesso.getVisivel().toString());
		}
		if (definicaoVariavelProcesso.getVisivelPainel() != null) {
			varElement.addAttribute ("panel-visible", definicaoVariavelProcesso.getVisivelPainel().toString());
		}
		if (definicaoVariavelProcesso.getOrdem() != null) {
			varElement.addAttribute("order", definicaoVariavelProcesso.getOrdem().toString());
		}
	}
}
