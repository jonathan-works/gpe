package br.com.infox.epp.imagem.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_GRANDE;
import static br.com.infox.constants.LengthConstants.DESCRICAO_MD5;
import static br.com.infox.constants.LengthConstants.DESCRICAO_MINIMA;
import static br.com.infox.constants.LengthConstants.DESCRICAO_NOME_ARQUIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.DATA_INCLUSAO;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.EXTENSAO;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.FILE_PATH;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.ID_IMAGEM_BIN;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.IMAGEM;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.LIST_IMAGENS;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.LIST_IMAGENS_QUERY;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.MD5;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.NOME_ARQUIVO;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.SEQUENCE_IMAGEM_BIN;
import static br.com.infox.epp.imagem.query.ImagemBinQuery.TABLE_IMAGEM_BIN;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = TABLE_IMAGEM_BIN)
@NamedQueries({ @NamedQuery(name = LIST_IMAGENS, query = LIST_IMAGENS_QUERY) })
public class ImagemBin implements Serializable {
	private static final float BYTES_IN_A_KILOBYTE = 1024f;

	private static final long serialVersionUID = 1L;

	private Integer idImagemBin;
	private String extensao;
	private String md5Imagem;
	private String nomeArquivo;
	private byte[] imagem;
	private Date dataInclusao;
	private Integer tamanho;
	private String filePath;

	@SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_IMAGEM_BIN)
	@Id
	@GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
	@Column(name = ID_IMAGEM_BIN, unique = true, nullable = false)
	public Integer getIdImagemBin() {
		return idImagemBin;
	}

	public void setIdImagemBin(Integer idImagemBin) {
		this.idImagemBin = idImagemBin;
	}

	@Column(name = EXTENSAO, length = DESCRICAO_MINIMA)
	@Size(max = DESCRICAO_MINIMA)
	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	@Column(name = MD5, length = DESCRICAO_MD5, nullable = false)
	@Size(max = DESCRICAO_MD5)
	@NotNull
	public String getMd5Imagem() {
		return md5Imagem;
	}

	public void setMd5Imagem(String md5Imagem) {
		this.md5Imagem = md5Imagem;
	}

	@Column(name = NOME_ARQUIVO, length = DESCRICAO_NOME_ARQUIVO)
	@Size(max = DESCRICAO_NOME_ARQUIVO)
	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = DATA_INCLUSAO, nullable = false)
	@NotNull
	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	@Column(name = "nr_tamanho")
	public Integer getTamanho() {
		return tamanho;
	}

	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}

	@Transient
	public String getTamanhoFormatado() {
		if (tamanho != null && tamanho > 0) {
			NumberFormat formatter = new DecimalFormat("###,##0.00");
			float sizeF = tamanho / BYTES_IN_A_KILOBYTE;
			return formatter.format(sizeF) + " Kb";
		}
		return "0 Kb";
	}

	@Column(name = IMAGEM, nullable = false)
	@NotNull
	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = Arrays.copyOf(imagem, imagem.length);
	}

	@Column(name = FILE_PATH, length = DESCRICAO_GRANDE)
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
