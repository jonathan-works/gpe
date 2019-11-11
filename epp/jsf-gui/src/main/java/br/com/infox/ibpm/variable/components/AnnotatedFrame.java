package br.com.infox.ibpm.variable.components;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.core.util.StringUtil;

public class AnnotatedFrame implements FrameDefinition {
	
	private Frame frame;
	
	public AnnotatedFrame(Frame frame) {
		this.frame = frame;
	}

	@Override
	public String getId() {
		return frame.id();
	}

	@Override
	public String getName() {
		return frame.name();
	}

	@Override
	public String getDescription() {
		return StringUtil.isEmpty(frame.description()) ? null : frame.description(); 
	}

	@Override
	public String getXhtmlPath() {
		return frame.xhtmlPath();
	}

	@Override
	public List<ParameterDefinition> getParameters() {
		List<ParameterDefinition> definitions = new ArrayList<>();
		for(ParameterVariable parameter : frame.parameters()) {
			definitions.add(new AnnotatedParameter(parameter));
		}
		return definitions;
	}

	@Override
	public boolean isDisabled() {
		return frame.disabled();
	}

}
