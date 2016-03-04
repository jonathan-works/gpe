package org.jbpm.loopBehavior.exe;

import org.jbpm.graph.def.node.loop.LoopConfiguration;
import org.jbpm.graph.def.node.loop.LoopConfigurationMultiInstance;
import org.jbpm.graph.def.node.loop.LoopConfigurationStandard;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.GsonBuilder;

public class LoopConfigurationTest {

	private void assertConversion(LoopConfiguration object){
	}
	
	@Test
	public void testConversion() {
		LoopConfigurationMultiInstance multiLoop = new LoopConfigurationMultiInstance();
		multiLoop.setCompletionCondition("#{completionCondition}");
		multiLoop.setInputDataItem("variavelEntrada");
//		multiLoop.setOutputDataItem("variavelSaida");
		multiLoop.setLoopCardinality("#{loopCardinality}");
		multiLoop.setIsSequential(Boolean.FALSE);
		multiLoop.setLoopDataInput("#{loopDataInput}");
//		multiLoop.setLoopDataOutput("#{loopDataOutput}");
		assertConversion(multiLoop);
		LoopConfigurationStandard stdLoop = new LoopConfigurationStandard();
		stdLoop.setLoopCondition("#{loopCondition}");
		stdLoop.setTestBefore(Boolean.TRUE);
		assertConversion(stdLoop);
	}

}
