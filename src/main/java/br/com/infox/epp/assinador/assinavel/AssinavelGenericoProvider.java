package br.com.infox.epp.assinador.assinavel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AssinavelGenericoProvider implements AssinavelProvider {
	
	private List<AssinavelSource> assinaveis;
	private List<byte[]> dataList;
	
	public AssinavelGenericoProvider(String... texto) {
		this(Arrays.asList(texto));
	}

	public AssinavelGenericoProvider(List<String> textos) {
		dataList = new ArrayList<>();
		for(String texto : textos) {
			dataList.add(texto.getBytes(StandardCharsets.UTF_8));
		}
	}	
	
	public class AssinavelGenericoSourceImpl implements AssinavelSource {
		
		private byte[] data;
		
		public AssinavelGenericoSourceImpl(byte[] data) {
			super();
			this.data = data;
		}

		@Override
		public byte[] digest(TipoHash tipoHash) {
				return tipoHash.digest(data); 
		}
	}
	
	@Override
	public Collection<AssinavelSource> getAssinaveis() {
		if(assinaveis == null) {
			assinaveis = new ArrayList<>();
			for(byte[] data : dataList) {
				assinaveis.add(new AssinavelGenericoSourceImpl(data));
			}
		}
		return assinaveis;
	}	
}
