package br.com.infox.epp.imagem.query;

import br.com.infox.core.persistence.ORConstants;

public interface ImagemBinQuery {
	String LIST_IMAGENS = "listImagens";
	String LIST_IMAGENS_QUERY = "select o from ImagemBin o";
	String TABLE_IMAGEM_BIN = "tb_imagem_bin";
	String SEQUENCE_IMAGEM_BIN = ORConstants.PUBLIC + ".sq_tb_imagem_bin";
	String ID_IMAGEM_BIN = "id_imagem_bin";
	String EXTENSAO = "ds_extensao";
	String MD5 = "ds_md5_imagem";
	String NOME_ARQUIVO = "nm_arquivo";
	String DATA_INCLUSAO = "dt_inclusao";
	String IMAGEM = "ob_imagem";
	String FILE_PATH = "ds_file_path";
}
