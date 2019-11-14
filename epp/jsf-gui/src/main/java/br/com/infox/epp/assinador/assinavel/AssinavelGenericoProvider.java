package br.com.infox.epp.assinador.assinavel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

public class AssinavelGenericoProvider implements AssinavelProvider {
	
	private List<AssinavelSource> assinaveis;
	private List<byte[]> dataList;
	
	public AssinavelGenericoProvider(String... texto) {
		this(Arrays.asList(texto));
	}

	public AssinavelGenericoProvider(byte[]... data) {
		dataList = Arrays.asList(data);
	}
	
	public AssinavelGenericoProvider(List<String> textos) {
		dataList = new ArrayList<>();
		for(String texto : ObjectUtils.defaultIfNull(textos, new ArrayList<String>())) {
			dataList.add(texto.getBytes(StandardCharsets.UTF_8));
		}
	}	
	
	@Override
	public List<AssinavelSource> getAssinaveis() {
		if(assinaveis == null) {
			assinaveis = new ArrayList<>();
			for(byte[] data : dataList) {
				assinaveis.add(new AssinavelGenericoSourceImpl(data));
			}
		}
		return assinaveis;
	}	
}
