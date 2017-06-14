package br.com.infox.epp.fluxo.definicao.modeler;

import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.ModelException;
import org.camunda.bpm.model.xml.ModelParseException;
import org.camunda.bpm.model.xml.ModelValidationException;

public class EppBpmn extends Bpmn {
    public static EppBpmn INSTANCE = new EppBpmn();

    private EppBpmnParser bpmnParser = new EppBpmnParser();

    public static BpmnModelInstance readModelFromFile(File file) {
        return INSTANCE.doReadModelFromFile(file);
    }

    /**
     * Allows reading a {@link BpmnModelInstance} from an {@link InputStream}
     *
     * @param stream
     *            the {@link InputStream} to read the {@link BpmnModelInstance}
     *            from
     * @return the model read
     * @throws ModelParseException
     *             if the model cannot be read
     */
    public static BpmnModelInstance readModelFromStream(InputStream stream) {
        return INSTANCE.doReadModelFromInputStream(stream);
    }

    /**
     * Allows writing a {@link BpmnModelInstance} to a File. It will be
     * validated before writing.
     *
     * @param file
     *            the {@link File} to write the {@link BpmnModelInstance} to
     * @param modelInstance
     *            the {@link BpmnModelInstance} to write
     * @throws BpmnModelException
     *             if the model cannot be written
     * @throws ModelValidationException
     *             if the model is not valid
     */
    public static void writeModelToFile(File file, BpmnModelInstance modelInstance) {
        INSTANCE.doWriteModelToFile(file, modelInstance);
    }

    /**
     * Allows writing a {@link BpmnModelInstance} to an {@link OutputStream}. It
     * will be validated before writing.
     *
     * @param stream
     *            the {@link OutputStream} to write the
     *            {@link BpmnModelInstance} to
     * @param modelInstance
     *            the {@link BpmnModelInstance} to write
     * @throws ModelException
     *             if the model cannot be written
     * @throws ModelValidationException
     *             if the model is not valid
     */
    public static void writeModelToStream(OutputStream stream, BpmnModelInstance modelInstance) {
        INSTANCE.doWriteModelToOutputStream(stream, modelInstance);
    }

    /**
     * Allows the conversion of a {@link BpmnModelInstance} to an
     * {@link String}. It will be validated before conversion.
     *
     * @param modelInstance
     *            the model instance to convert
     * @return the XML string representation of the model instance
     */
    public static String convertToString(BpmnModelInstance modelInstance) {
        return INSTANCE.doConvertToString(modelInstance);
    }

    /**
     * Validate model DOM document
     *
     * @param modelInstance
     *            the {@link BpmnModelInstance} to validate
     * @throws ModelValidationException
     *             if the model is not valid
     */
    public static void validateModel(BpmnModelInstance modelInstance) {
        INSTANCE.doValidateModel(modelInstance);
    }

    /**
     * Allows creating an new, empty {@link BpmnModelInstance}.
     *
     * @return the empty model.
     */
    public static BpmnModelInstance createEmptyModel() {
        return INSTANCE.doCreateEmptyModel();
    }

    public static ProcessBuilder createProcess() {
        BpmnModelInstance modelInstance = INSTANCE.doCreateEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace(BPMN20_NS);
        definitions.getDomElement().registerNamespace("camunda", CAMUNDA_NS);
        definitions.getDomElement().registerNamespace(ModeladorConstants.INFOX_BPMN_NAMESPACE_ALIAS,
                ModeladorConstants.INFOX_BPMN_NAMESPACE);
        modelInstance.setDefinitions(definitions);
        Process process = modelInstance.newInstance(Process.class);
        definitions.addChildElement(process);
        return process.builder();
    }

    public static ProcessBuilder createProcess(String processId) {
        return createProcess().id(processId);
    }

    public static ProcessBuilder createExecutableProcess() {
        return createProcess().executable();
    }

    public static ProcessBuilder createExecutableProcess(String processId) {
        return createProcess(processId).executable();
    }

    @Override
    protected BpmnModelInstance doReadModelFromInputStream(InputStream is) {
        return bpmnParser.parseModelFromStream(is);
    }

    @Override
    protected void doValidateModel(BpmnModelInstance modelInstance) {
        bpmnParser.validateModel(modelInstance.getDocument());
    }

    @Override
    protected BpmnModelInstance doCreateEmptyModel() {
        return bpmnParser.getEmptyModel();
    }
}
