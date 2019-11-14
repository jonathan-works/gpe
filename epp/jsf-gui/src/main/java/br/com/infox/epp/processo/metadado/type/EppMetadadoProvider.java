package br.com.infox.epp.processo.metadado.type;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;

public class EppMetadadoProvider extends MetadadoProcessoProvider {
	
	public static final MetadadoProcessoDefinition LOCALIZACAO_DESTINO = 
			new MetadadoProcessoDefinition("localizacaoDestino", "Destino", Localizacao.class);
	
	public static final MetadadoProcessoDefinition PESSOA_DESTINATARIO = 
			new MetadadoProcessoDefinition("pessoaDestinatario", "Destinatário", PessoaFisica.class);
	
	public static final MetadadoProcessoDefinition PERFIL_DESTINO = 
	        new MetadadoProcessoDefinition("perfilTemplateDestino", "Perfil Destino", PerfilTemplate.class);
	
	public static final MetadadoProcessoDefinition ITEM_DO_PROCESSO = 
			new MetadadoProcessoDefinition("itemProcesso", "Item do Processo", Item.class);
	
	public static final MetadadoProcessoDefinition TIPO_PROCESSO = 
			new MetadadoProcessoDefinition("tipoProcesso", TipoProcesso.class);

	public static final MetadadoProcessoDefinition STATUS_PROCESSO = 
			new MetadadoProcessoDefinition("statusProcesso", "Estágio do Processo", StatusProcesso.class);

	public static final MetadadoProcessoDefinition PASTA_DEFAULT = 
			new MetadadoProcessoDefinition("pastaDefault", Pasta.class);
	
	public static final MetadadoProcessoDefinition DOCUMENTO_EM_ANALISE = 
			new MetadadoProcessoDefinition("documentoEmAnalise", Documento.class);

	public static final MetadadoProcessoDefinition ENTREGA =
	        new MetadadoProcessoDefinition("entrega", Entrega.class);

	public static final MetadadoProcessoDefinition NATUREZA =
            new MetadadoProcessoDefinition("naturezaMetadado", Natureza.class);

	public static final MetadadoProcessoDefinition CATEGORIA =
	        new MetadadoProcessoDefinition("categoriaMetadado", Categoria.class);
}
