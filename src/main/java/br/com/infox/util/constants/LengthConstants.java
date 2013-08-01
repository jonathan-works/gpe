package br.com.infox.util.constants;

public interface LengthConstants {
    
    /*
     * Favor, utilizar nomes intuitivos para as constantes mesmo que os valores se repitam
     * além de agrupar por tipo e em ordem crescente.
     * */
    
  //Tamnhos dos Códigos
    int UF = 2; //Unidade da Federação
    int CEP = 9;
    int CNAE = 9;
    int CODIGO_DOCUMENTO = 30;
    
    //Tamanhos das Descrições Usuais
    int DESCRICAO_ABREVIADA = 5;
    int DESCRICAO_MINIMA = 15;
    int DESCRICAO_PEQUENA = 30;
    int DESCRICAO_PADRAO_METADE = 50;
    int DESCRICAO_PADRAO = 100;
    int DESCRICAO_MEDIA = 150;
    int DESCRICAO_PADRAO_DOBRO = 200;
    int DESCRICAO_GRANDE = 255;
    
    //Tamanhos das Descrições Especiais
    int DESCRICAO_MD5 = 32;
    int DESCRICAO_ENTIDADE = 50;
    int DESCRICAO_TITULO = 60;
    int DESCRICAO_PACOTE = 150;
    int DESCRICAO_CLASSIFICACAO = 150; //CBO e CNAE
    int DESCRICAO_NOME_ARQUIVO = 300;
    
    //Tamanhos de Ids
    int ID_PAGINA = 200;
    int ID_ENTIDADE = 200;
    
    //Tamanhos dos Nomes
    int NOME_PADRAO = 100;
    int NOME_BAIRRO = 100;
    int NOME_ATRIBUTO = 150;
    int NOME_MEDIO = 150;
    int NOME_LOGRADOURO = 200;
    
    //Tamanhos de ~Números
    int NUMERO_ENDERECO = 15;
    int NUMERO_RAZAO_SOCIAL = 20;
    int NUMERO_PROCESSO = 30;
    
    //Tipos Especiais
    int FLAG = 1;

}
