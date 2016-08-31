package br.com.infox.epp.processo.metadado.type;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.entrega.rest.Categoria;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

public class EppMetadadoProvider extends MetadadoProcessoProvider {
	
	public static final MetadadoProcessoDefinition UNIDADE_DECISORA_MONOCRATICA = 
			new MetadadoProcessoDefinition("unidadeDecisoraMonocratica", "Unidade Decisora Monocrática", UnidadeDecisoraMonocratica.class);
	
	public static final MetadadoProcessoDefinition UNIDADE_DECISORA_COLEGIADA = 
			new MetadadoProcessoDefinition("unidadeDecisoraColegiada", "Unidade Decisora Colegiada", UnidadeDecisoraColegiada.class);
	
	public static final MetadadoProcessoDefinition RELATOR = 
			new MetadadoProcessoDefinition("relator", "Relator", PessoaFisica.class);

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
