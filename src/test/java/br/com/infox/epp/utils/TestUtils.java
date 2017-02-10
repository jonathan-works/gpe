package br.com.infox.epp.utils;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class TestUtils {
    private TestUtils(){}
    public static <T> T loadFromXml(Class<T> type, String classPathFile){
        return loadFromXml(type, type.getResourceAsStream(classPathFile));
    }
    public static <T> T loadFromXml(Class<T> type, InputStream xmlStream) {
        try {
            JAXBContext jc = JAXBContext.newInstance(type);
            Unmarshaller u = jc.createUnmarshaller();
            
            @SuppressWarnings("unchecked")
            T result=(T)u.unmarshal(xmlStream);
            return result;
        } catch (JAXBException e){
            e.printStackTrace();
        }
        return null;
    }
}