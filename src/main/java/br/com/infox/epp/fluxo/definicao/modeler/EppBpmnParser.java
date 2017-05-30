package br.com.infox.epp.fluxo.definicao.modeler;

import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_20_SCHEMA_LOCATION;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.camunda.bpm.model.bpmn.impl.BpmnParser;
import org.camunda.bpm.model.xml.ModelValidationException;
import org.camunda.bpm.model.xml.impl.util.ReflectUtil;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.xml.sax.SAXException;

public class EppBpmnParser extends BpmnParser {
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private static final String[] SCHEMAS = {
        ReflectUtil.getResource(BPMN_20_SCHEMA_LOCATION, BpmnParser.class.getClassLoader()).toString(),
        ReflectUtil.getResource("xsd/InfoxBpmn.xsd", EppBpmnParser.class.getClassLoader()).toString()    
    };
    
    public EppBpmnParser() {
        addSchema(ModeladorConstants.INFOX_BPMN_NAMESPACE, createSchema("xsd/InfoxBpmn.xsd", EppBpmnParser.class.getClassLoader()));
    }
    
    @Override
    protected void configureFactory(DocumentBuilderFactory dbf) {
        super.configureFactory(dbf);
        dbf.setAttribute(JAXP_SCHEMA_SOURCE, SCHEMAS);
    }
    
    @Override
    public void validateModel(DomDocument document) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema infoxBpmn = schemaFactory.newSchema(new Source[] {
                new StreamSource(SCHEMAS[0]),
                new StreamSource(SCHEMAS[1])
            });
            
            Validator validator = infoxBpmn.newValidator();
            
            synchronized (document) {
                validator.validate(document.getDomSource());
            }
        } catch (IOException e) {
            throw new ModelValidationException("Error during DOM document validation", e);
        } catch (SAXException e) {
            throw new ModelValidationException("DOM document is not valid", e);
        }
    }
}
