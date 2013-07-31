package br.com.infox.util.constants;

public interface LengthConstants {
    
    /*
     * Favor, utilizar nomes intuitivos para as constantes mesmo que os valores se repitam
     * al�m de agrupar por tipo e em ordem crescente.
     * */
    
  //Tamnhos dos C�digos
    int UF = 2; //Unidade da Federa��o
    int CEP = 9;
    int CNAE = 9;
    
    //Tamanhos das Descri��es
    int DESCRICAO_MINIMA = 15;
    int DESCRICAO_PEQUENA = 30;
    int DESCRICAO_MD5 = 32;
    int DESCRICAO_ENTIDADE = 50;
    int DESCRICAO_PADRAO = 100;
    int DESCRICAO_PACOTE = 150;
    int DESCRICAO_CLASSIFICACAO = 150; //CBO e CNAE
    int DESCRICAO_GRANDE = 255;
    int DESCRICAO_NOME_ARQUIVO = 300;
    
    //Tamanhos de Ids
    int ID_PAGINA = 200;
    int ID_ENTIDADE = 200;
    
    //Tamanhos dos Nomes
    int NOME_CAIXA = 100;
    int NOME_BAIRRO = 100;
    int NOME_ATRIBUTO = 150;
    int NOME_LOGRADOURO = 200;
    
    //Tamanhos de ~N�meros
    int NUMERO_ENDERECO = 15;
    
    //Tipos Especiais
    int FLAG = 1;

}
