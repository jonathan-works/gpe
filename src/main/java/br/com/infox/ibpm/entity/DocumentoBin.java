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

package br.com.infox.ibpm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import br.com.itx.util.ArrayUtil;

/**
 * Esta entidade mapeia a tabela tb_processo_documento_bin da base BIN, 
 * na qual os arquivos binários são armazenados.. 
 * @author joaopaulo
 */
@Entity
@Table(name = "tb_processo_documento_bin", schema="public")
public class DocumentoBin implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idDocumentoBin;
	private byte[] documentoBin;

	@Id
	@Column(name = "id_processo_documento_bin", unique = true, nullable = false)
	@NotNull
	public int getIdDocumentoBin() {
		return this.idDocumentoBin;
	}

	public void setIdDocumentoBin(int idDocumentoBin) {
		this.idDocumentoBin = idDocumentoBin;
	}	
	
	@Column(name = "ob_processo_documento", nullable = false)
	@NotNull
	public byte[] getDocumentoBin() {
		return ArrayUtil.copyOf(documentoBin);
	}
	
	public void setDocumentoBin(byte[] documentoBin) {
		this.documentoBin = ArrayUtil.copyOf(documentoBin);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DocumentoBin)) {
			return false;
		}
		DocumentoBin other = (DocumentoBin) obj;
		if (getIdDocumentoBin() != other.getIdDocumentoBin()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdDocumentoBin();
		return result;
	}
	
}