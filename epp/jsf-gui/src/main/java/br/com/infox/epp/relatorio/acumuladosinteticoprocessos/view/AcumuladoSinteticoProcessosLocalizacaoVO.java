package br.com.infox.epp.relatorio.acumuladosinteticoprocessos.view;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcumuladoSinteticoProcessosLocalizacaoVO implements Comparable<AcumuladoSinteticoProcessosLocalizacaoVO> {

	private String localizacao;
	private List<AcumuladoSinteticoProcessosVO> listaRelatorioEmAndamento = new ArrayList<AcumuladoSinteticoProcessosVO>();
	private List<AcumuladoSinteticoProcessosVO> listaRelatorioFinalizadoArquivado = new ArrayList<AcumuladoSinteticoProcessosVO>();
	@Override
	public int compareTo(AcumuladoSinteticoProcessosLocalizacaoVO o) {
		return this.localizacao.compareTo(o.getLocalizacao());
	}
	
}
