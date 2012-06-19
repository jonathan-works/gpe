
package br.com.infox.epa.webservice.globalweather.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-04/11/2011 03:11 PM(mockbuild)-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "GlobalWeatherSoap", targetNamespace = "http://www.webserviceX.NET")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface GlobalWeatherSoap {


    /**
     * Get weather report for all major cities around the world.
     * 
     * @param countryName
     * @param cityName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetWeather", action = "http://www.webserviceX.NET/GetWeather")
    @WebResult(name = "GetWeatherResult", targetNamespace = "http://www.webserviceX.NET")
    @RequestWrapper(localName = "GetWeather", targetNamespace = "http://www.webserviceX.NET", className = "br.com.infox.epa.webservice.globalweather.client.GetWeather")
    @ResponseWrapper(localName = "GetWeatherResponse", targetNamespace = "http://www.webserviceX.NET", className = "br.com.infox.epa.webservice.globalweather.client.GetWeatherResponse")
    public String getWeather(
        @WebParam(name = "CityName", targetNamespace = "http://www.webserviceX.NET")
        String cityName,
        @WebParam(name = "CountryName", targetNamespace = "http://www.webserviceX.NET")
        String countryName);

    /**
     * Get all major cities by country name(full / part).
     * 
     * @param countryName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetCitiesByCountry", action = "http://www.webserviceX.NET/GetCitiesByCountry")
    @WebResult(name = "GetCitiesByCountryResult", targetNamespace = "http://www.webserviceX.NET")
    @RequestWrapper(localName = "GetCitiesByCountry", targetNamespace = "http://www.webserviceX.NET", className = "br.com.infox.epa.webservice.globalweather.client.GetCitiesByCountry")
    @ResponseWrapper(localName = "GetCitiesByCountryResponse", targetNamespace = "http://www.webserviceX.NET", className = "br.com.infox.epa.webservice.globalweather.client.GetCitiesByCountryResponse")
    public String getCitiesByCountry(
        @WebParam(name = "CountryName", targetNamespace = "http://www.webserviceX.NET")
        String countryName);

}
