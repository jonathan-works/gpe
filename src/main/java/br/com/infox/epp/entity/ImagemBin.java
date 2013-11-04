package br.com.infox.epp.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(name = "tb_imagem_bin", schema="public")
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
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_imagem_bin")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_imagem_bin", unique = true, nullable = false)
	@NotNull
	public Integer getIdImagemBin() {
		return idImagemBin;
	}
	public void setIdImagemBin(Integer idImagemBin) {
		this.idImagemBin = idImagemBin;
	}
	
	@Column(name = "ds_extensao", length=LengthConstants.DESCRICAO_MINIMA)
	@Size(max=LengthConstants.DESCRICAO_MINIMA)
	public String getExtensao() {
		return extensao;
	}
	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}
	
	@Column(name = "ds_md5_imagem", length=LengthConstants.DESCRICAO_MD5, nullable=false)
	@Size(max=LengthConstants.DESCRICAO_MD5)
	@NotNull
	public String getMd5Imagem() {
		return md5Imagem;
	}
	public void setMd5Imagem(String md5Imagem) {
		this.md5Imagem = md5Imagem;
	}
	
	@Column(name = "nm_arquivo", length=LengthConstants.DESCRICAO_NOME_ARQUIVO)
	@Size(max=LengthConstants.DESCRICAO_NOME_ARQUIVO)
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_inclusao", nullable = false)
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
	
	@Column(name = "ob_imagem", nullable = false)
	@NotNull
	public byte[] getImagem() {
		return imagem;
	}
	public void setImagem(byte[] imagem) {
		this.imagem = Arrays.copyOf(imagem, imagem.length);
	}
	
	@Column(name = "ds_file_path", length=LengthConstants.DESCRICAO_GRANDE)
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }	
	
}
