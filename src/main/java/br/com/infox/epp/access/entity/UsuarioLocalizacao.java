/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.epp.access.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1
import static br.com.infox.core.persistence.ORConstants.*;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.*;
import static javax.persistence.FetchType.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

@Entity
@Table(name = TABLE_USUARIO_LOCALIZACAO, schema=PUBLIC, 
		uniqueConstraints = @UniqueConstraint(columnNames={USUARIO, PAPEL, LOCALIZACAO, ESTRUTURA}))
@Inheritance(strategy = InheritanceType.JOINED)

public class UsuarioLocalizacao implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(UsuarioLocalizacao.class);

	private int idUsuarioLocalizacao;
	private Localizacao localizacao;
	private UsuarioLogin usuario;
	private Boolean responsavelLocalizacao;
	private Papel papel;
	private Localizacao estrutura;
	private boolean contabilizar;

	public UsuarioLocalizacao() {
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_USUARIO_LOCALIZACAO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_USUARIO_LOCALIZACAO, unique = true, nullable = false)
	public int getIdUsuarioLocalizacao() {
		return this.idUsuarioLocalizacao;
	}

	public void setIdUsuarioLocalizacao(int idUsuarioLocalizacao) {
		this.idUsuarioLocalizacao = idUsuarioLocalizacao;
	}
	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = LOCALIZACAO, nullable = false)
	@NotNull
	public Localizacao getLocalizacao() {
		return this.localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = PAPEL)	
	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}
	
	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = USUARIO, nullable = false)
	@NotNull
	public UsuarioLogin getUsuario() {
		return this.usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}

	@Column(name = RESPONSAVEL_LOCALIZACAO, nullable = false)
	@NotNull
	public Boolean getResponsavelLocalizacao() {
		return this.responsavelLocalizacao;
	}

	public void setResponsavelLocalizacao(Boolean responsavelLocalizacao) {
		this.responsavelLocalizacao = responsavelLocalizacao;
	}
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = ESTRUTURA)
	public Localizacao getEstrutura() {
		return estrutura;
	}
	
	public void setEstrutura(Localizacao estrutura) {
		this.estrutura = estrutura;
	}

	@Override
	public String toString() {
		try {
			return (estrutura == null ? "" : estrutura + "/") + localizacao + "/" + papel;
		} catch (Exception e) {
			LOG.warn(".toString()", e);
			return super.toString();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UsuarioLocalizacao)) {
			return false;
		}
		UsuarioLocalizacao other = (UsuarioLocalizacao) obj;
		if (getIdUsuarioLocalizacao() != other.getIdUsuarioLocalizacao()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdUsuarioLocalizacao();
		return result;
	}

	public void setContabilizar(boolean contabilizar) {
		this.contabilizar = contabilizar;
	}

	@Column(name=CONTABILIZAR, nullable=false)
	public boolean getContabilizar() {
		return contabilizar;
	}

}