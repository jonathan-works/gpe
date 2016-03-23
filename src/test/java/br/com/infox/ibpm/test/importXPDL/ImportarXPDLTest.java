package br.com.infox.ibpm.test.importXPDL;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.Problem;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.lane.IllegalNumberPoolsXPDLException;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;

public class ImportarXPDLTest {

	private static final String file3Pools = "/test/3Pools.xpdl";
	private static final String fileSubProcess = "/test/SubProcess.xpdl";
	private static final String fileSucess = "/test/sucessFluxo.xpdl";
	private static final String verificarConformidade = "/test/verificarConformidade.xpdl";
	private static final String CD_FLUXO = "cdFluxo";
	private int MAX = 0x10000;

	@Test(expected = IllegalNumberPoolsXPDLException.class)
	public void createFluxoXPDLTestWith3Pools() throws IOException {
		byte[] bytes = readFile(file3Pools);
		FluxoXPDL.createInstance(bytes);
	}

	@Test
	public void importarFluxoXPDLTestWithSubProcess() throws IOException {
		for (String file : new String[] { fileSubProcess }) {
			byte[] bytes = readFile(file);
			testarImportacaoFluxoSucesso(bytes);
		}
	}

	private void testarImportacaoFluxoSucesso(byte[] bytes) {
		FluxoXPDL fluxoXPDL = FluxoXPDL.createInstance(bytes);
		String xml = fluxoXPDL.toJPDL(CD_FLUXO);
		Assert.assertNotNull(xml);
		try {
			StringReader stringReader = new StringReader(xml);
			InfoxJpdlXmlReader jpdlReader = new InfoxJpdlXmlReader(new InputSource(stringReader));
			jpdlReader.readProcessDefinition();
		} catch (JpdlException e) {
			StringBuilder sb = new StringBuilder();
			for (Problem problem : (List<Problem>) e.getProblems()) {
				sb.append(problem.getException()).append(problem).append(System.lineSeparator());
			}
			Assert.fail(sb.toString());
		}
	}
	
	@Test
	public void importarFluxoXPDLTestWithSucess() throws IOException {
		for (String file : new String[] { fileSucess, verificarConformidade }) {
			byte[] bytes = readFile(file);
			testarImportacaoFluxoSucesso(bytes);
		}
	}

	private byte[] readFile(String name) throws IOException {
		InputStream fileStream = getClass().getResourceAsStream(name);
		byte[] buffered = new byte[MAX];
		int i = 0;
		int temp = fileStream.read();
		while (temp != -1 && i < MAX) {
			buffered[i++] = (byte) temp;
			temp = fileStream.read();
		}
		fileStream.close();
 		if (i == MAX) {
			throw new OutOfMemoryError("Espaço alocado para leitura do arquivo foi insuficiente.");
 		}
		
		return Arrays.copyOf(buffered, i);
	}

}
