/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.entity;

import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.access.entity.Papel;

@Entity
@Table(name = ListaEmail.TABLE_NAME, schema="public")
public class ListaEmail implements java.io.Serializable {

	public final static String TABLE_NAME = "tb_lista_email";
	
	private static final long serialVersionUID = 1L;

	private int idListaEmail;
	private int idGrupoEmail;
	private Localizacao localizacao;
	private Papel papel;
	private Localizacao estrutura;

	public ListaEmail() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_lista_email")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_lista_email", unique = true, nullable = false)
	public int getIdListaEmail() {
		return this.idListaEmail;
	}

	public void setIdListaEmail(int id) {
		this.idListaEmail = id;
	}

	@Column(name = "id_grupo_email", unique = true, nullable = false)
	public int getIdGrupoEmail() {
		return this.idGrupoEmail;
	}

	public void setIdGrupoEmail(int id) {
		this.idGrupoEmail = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao")
	public Localizacao getLocalizacao() {
		return this.localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_papel")	
	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estrutura")
	public Localizacao getEstrutura() {
		return estrutura;
	}
	
	public void setEstrutura(Localizacao estrutura) {
		this.estrutura = estrutura;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}:{1}-{2}{3}/{4}", idListaEmail, idGrupoEmail,
				(estrutura == null ? "" : estrutura + "/"), 
				localizacao, papel);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ListaEmail)) {
			return false;
		}
		ListaEmail other = (ListaEmail) obj;
		if (getIdListaEmail() != other.getIdListaEmail()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdListaEmail();
		return result;
	}
}