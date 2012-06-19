package br.com.infox.epa.webservice.globalweather.client.service;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


@Name(GetCitiesByCountryService.NAME)
public class GetCitiesByCountryService {

	public static final String NAME = "getCitiesByCountryService";

	private static final String WEATHER = "http://www.webservicex.net/globalweather.asmx/GetWeather?CityName={0}&CountryName={1}";
	private static final String CITYS = "http://www.webservicex.net/globalweather.asmx/GetCitiesByCountry?CountryName={0}";
	private static final String MASK_TAG = "<{0}>(.*?)</{0}>";

	private Map<String, BeanClima> mapBeanClima = new HashMap<String, BeanClima>();

	public String cidade;

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public BeanClima getClima() throws Exception {
		if (Strings.isEmpty(cidade)) {
			return null;
		}
		BeanClima beanClima = mapBeanClima.get(cidade);
		if (beanClima != null) {
			return beanClima;
		} else {
			try {
				String url = MessageFormat.format(WEATHER, cidade, "brazil").replace(" ", "%20");
				String xml = FileDataDownload.getUrlAsString(url);
				xml = StringEscapeUtils.unescapeHtml(xml);	
				beanClima = processaXMLClima(xml);
				mapBeanClima.put(cidade, beanClima);
				return beanClima;
			} catch (Exception e) {
				FacesMessages.instance().add(Severity.ERROR, "Erro: " + e.getMessage(), e);
				return null;
			}
		}
	}

	public List<String> getCidadeListBrazil() {
		try {
			return getCidadeList("brazil");
		} catch (Exception e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro: " + e.getMessage(), e);
			return null;
		}
	}

	public List<String> getCidadeList(String nomePais) throws Exception { 
		String urlAsString = FileDataDownload.getUrlAsString(MessageFormat.format(CITYS, "brazil"));
		urlAsString = StringEscapeUtils.unescapeHtml(urlAsString);
		List<String> processaXML = processaXML(urlAsString);
		Collections.sort(processaXML);	
		return processaXML;
	}

	private BeanClima processaXMLClima(String xml) throws Exception {
		BeanClima beanClima = new BeanClima();
		beanClima.setLocation(getValueTag("Location", xml));
		beanClima.setTime(getValueTag("Time", xml));
		beanClima.setWind(getValueTag("Wind", xml));
		beanClima.setRelativeHumidity(getValueTag("RelativeHumidity", xml));
		beanClima.setTemperature(getValueTag("Temperature", xml));
		beanClima.setPressure(getValueTag("Pressure", xml));
		return beanClima;
	}	

	private String getValueTag(String tag, String xml) {
		Pattern pattern = Pattern.compile(MessageFormat.format(MASK_TAG, tag));
		Matcher matcher = pattern.matcher(xml);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private List<String> processaXML(String xml) throws Exception {
		List<String> dados = new ArrayList<String>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

		Element retDadosAdvogado = doc.getDocumentElement();
		NodeList tableNlist = retDadosAdvogado.getElementsByTagName("Table");

		if (tableNlist != null && tableNlist.getLength() > 0) {
			for (int i = 0; i < tableNlist.getLength(); i++) {

				NodeList tags = tableNlist.item(i).getChildNodes();
				for (int j = 0; j < tags.getLength(); j++) {
					String tagName = tags.item(j).getNodeName();
					String tagValue = tags.item(j).getTextContent();
					if ("City".equalsIgnoreCase(tagName)) {
						dados.add(tagValue);
					} 
				}
			} 
		}   	
		return dados;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("http.proxyHost", "wpad.infox.intranet");
		System.setProperty("http.proxyPort", "8080");		
		GetCitiesByCountryService service = new GetCitiesByCountryService();
		List<String> cidadeList = service.getCidadeList("brazil");
		System.out.println(cidadeList);
	}

}
