package br.com.infox.epp.assinador.assinavel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

public class AssinavelGenericoProvider implements AssinavelProvider {

    @AllArgsConstructor @Data
    public static class DocumentoComRegraAssinatura {
        private final TipoMeioAssinaturaEnum tipoMeioAssinatura;
        private final byte[] documentoBin;
    }

	private List<AssinavelSource> assinaveis;
	private List<DocumentoComRegraAssinatura> dataList;


    public AssinavelGenericoProvider(DocumentoComRegraAssinatura data) {
        dataList = Arrays.asList(data);
    }

	public AssinavelGenericoProvider(String... texto) {
		this(Arrays.asList(texto));
	}

	public AssinavelGenericoProvider(byte[]... data) {
	    dataList = new ArrayList<>();
	    for (byte[] bs : data) {
	        dataList.add(new DocumentoComRegraAssinatura(
                TipoMeioAssinaturaEnum.T, bs
            ));
        }
	}

	public AssinavelGenericoProvider(List<String> textos) {
		dataList = new ArrayList<>();
		for(String texto : ObjectUtils.defaultIfNull(textos, new ArrayList<String>())) {
			dataList.add(new DocumentoComRegraAssinatura(
		        TipoMeioAssinaturaEnum.T, texto.getBytes(StandardCharsets.UTF_8)
	        ));
		}
	}

	@Override
	public List<AssinavelSource> getAssinaveis(){
		if(assinaveis == null){
			assinaveis = new ArrayList<>();
			for(DocumentoComRegraAssinatura data : dataList) {
				assinaveis.add(new AssinavelGenericoSourceImpl(
			        data.getDocumentoBin(), data.getTipoMeioAssinatura()
		        ));
			}
		}
		return assinaveis;
	}
}
