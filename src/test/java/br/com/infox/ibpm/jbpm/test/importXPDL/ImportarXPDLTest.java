package br.com.infox.ibpm.jbpm.test.importXPDL;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.com.infox.ibpm.jbpm.xpdl.ImportarXPDLService;
import br.com.infox.ibpm.jbpm.xpdl.ImportarXPDLServiceException;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityNotAllowedXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.activities.IllegalActivityXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.lane.IllegalNumberPoolsXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.transition.IllegalTransitionXPDLException;

public class ImportarXPDLTest {

	private String file3Pools = "./test/3Pools.xpdl";
	private String	fileSubProcess = "./test/SubProcess.xpdl";
	private String	fileSucess = "./test/sucessFluxo.xpdl";
	private ImportarXPDLService importarXPDLService;
	private int MAX = 0x10000;

	@Before
	public void setup() {
		importarXPDLService = new ImportarXPDLService();
	}

	@Test(expected = IllegalNumberPoolsXPDLException.class)
	public void createFluxoXPDLTestWith3Pools() throws IOException, ImportarXPDLServiceException,
			IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException,
			IllegalActivityXPDLException, IllegalTransitionXPDLException {
		byte[] bytes = readFile(file3Pools);
		importarXPDLService.createFluxoXPDL(bytes);
	}

	@Test
	public void importarFluxoXPDLTestWithSubProcess() throws ImportarXPDLServiceException,
			IOException, IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException,
			IllegalActivityXPDLException, IllegalTransitionXPDLException {
		byte[] bytes = readFile(fileSubProcess);
		String xml = importarXPDLService.importarXPDLToJPDL(bytes);
		Assert.assertNotNull(xml);
	}
	
	@Test
	public void importarFluxoXPDLTestWithSucess() throws ImportarXPDLServiceException,
			IOException, IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException,
			IllegalActivityXPDLException, IllegalTransitionXPDLException {
		byte[] bytes = readFile(fileSucess);
		String xml = importarXPDLService.importarXPDLToJPDL(bytes);
		Assert.assertNotNull(xml);
	}

	private byte[] readFile(String name) throws IOException {
		FileInputStream fileStream = new FileInputStream(name);
		byte[] buffered = new byte[MAX];
		int i = 0;
		int temp = fileStream.read();
		while (temp != -1 && i < MAX) {
			buffered[i++] = (byte) temp;
			temp = fileStream.read();
		}
		if (i == MAX)
			throw new OutOfMemoryError("Espaço alocado para leitura do arquivo foi insuficiente.");
		return Arrays.copyOf(buffered, i);
	}

}
