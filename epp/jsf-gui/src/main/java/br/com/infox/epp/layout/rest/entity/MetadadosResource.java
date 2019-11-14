package br.com.infox.epp.layout.rest.entity;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.ws.rs.core.EntityTag;

import br.com.infox.epp.layout.entity.ResourceBin;
import br.com.infox.epp.layout.entity.ResourceBin.TipoArquivo;

public class MetadadosResource {

	private Date lastModified;
	private EntityTag etag;
	private TipoArquivo tipo;

	// FIXME: Arrumar outra forma de gerar etag a partir de um resource no WAR
	public MetadadosResource(URL urlResourceWar) {
		URLConnection conexao;
		if(urlResourceWar == null)
			throw new RuntimeException("Não foi possível identicar a urlResourceWAR");
		try {
			conexao = urlResourceWar.openConnection();
			try {
				lastModified = new Date(conexao.getLastModified());
				this.etag = new EntityTag(Long.toString(lastModified.getTime()));
			} finally {
				conexao.getInputStream().close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public MetadadosResource(ResourceBin resource) {
		this.etag = new EntityTag(Long.toString(resource.getDataModificacao().getTime()));
		this.lastModified = resource.getDataModificacao();
		this.tipo = resource.getTipo();
	}

	public Date getLastModified() {
		return lastModified;
	}

	public EntityTag getEtag() {
		return etag;
	}

	public TipoArquivo getTipo() {
		return tipo;
	}

}
