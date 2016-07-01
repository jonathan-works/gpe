package br.com.infox.epp.assinador.assinavel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		public byte[] dataToSign(TipoSignedData tipoHash) {
				return tipoHash.dataToSign(data); 
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
