package br.com.infox.modeler;

import org.junit.Test;

import br.com.infox.epp.modeler.converter.BpmnJpdlConverter;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;

public class BpmnJpdlConverterTest {
	@Test
	public void testImport() {
		BpmnJpdlConverter converter = new BpmnJpdlConverter();
		System.out.println(JpdlXmlWriter.toString(converter.convert(getClass().getResourceAsStream("/example.bpmn.xml"))));
	}
}
