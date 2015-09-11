package br.com.infox.epp.view.municipio;

public interface MunicipioQuery {
    String VIEW_NAME = "ETCE_Municipio";
    
    String NOME_MUNICIPIO_QUERY = "select TNOM from " + VIEW_NAME + " where CCOD = ?";
    String CODIGO_IBGE_QUERY = "select CodigoIBGE from " + VIEW_NAME + " where CCOD = ?";
    String ESTADO_QUERY = "select CUNIFED from " + VIEW_NAME + " where CCOD = ?";

    String MUNICIPIOS = "select CCOD, TNOM from " + VIEW_NAME + " where CUNIFED = 'PE' order by TNOM";

    String MUNCIPIO_BY_ESTADO = "select CCOD, TNOM from " + VIEW_NAME + " where CUNIFED = ? order by TNOM";    
}
