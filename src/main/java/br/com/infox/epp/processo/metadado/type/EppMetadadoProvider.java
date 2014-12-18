package br.com.infox.epp.processo.metadado.type;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

public class EppMetadadoProvider extends MetadadoProcessoProvider {
	
	public EppMetadadoProvider(){}
	
	public EppMetadadoProvider(Processo processo) {
		super(processo);
	}

	public static final MetadadoProcessoDefinition UNIDADE_DECISORA_MONOCRATICA = 
			new MetadadoProcessoDefinition("unidadeDecisoraMonocratica", "Unidade Decisora Monocrática", UnidadeDecisoraMonocratica.class, true);
	
	public static final MetadadoProcessoDefinition UNIDADE_DECISORA_COLEGIADA = 
			new MetadadoProcessoDefinition("unidadeDecisoraColegiada", "Unidade Decisora Colegiada", UnidadeDecisoraColegiada.class, true);
	
	public static final MetadadoProcessoDefinition RELATOR = 
			new MetadadoProcessoDefinition("relator", "Relator", PessoaFisica.class, true);

	public static final MetadadoProcessoDefinition LOCALIZACAO_DESTINO = 
			new MetadadoProcessoDefinition("localizacaoDestino", "Destino", Localizacao.class, true);
	
	public static final MetadadoProcessoDefinition PESSOA_DESTINATARIO = 
			new MetadadoProcessoDefinition("pessoaDestinatario", "Destinatário", PessoaFisica.class, true);
	
	public static final MetadadoProcessoDefinition ITEM_DO_PROCESSO = 
			new MetadadoProcessoDefinition("itemProcesso", "Item do Processo", Item.class, false);
	
	public static final MetadadoProcessoDefinition TIPO_PROCESSO = 
			new MetadadoProcessoDefinition("tipoProcesso", TipoProcesso.class);

	public static final MetadadoProcessoDefinition STATUS_PROCESSO = 
			new MetadadoProcessoDefinition("statusProcesso", "Status do Processo", StatusProcesso.class, true);
	
	
}
