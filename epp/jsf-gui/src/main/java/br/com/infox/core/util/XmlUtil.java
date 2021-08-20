package br.com.infox.core.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.com.infox.core.file.download.FileDownloader;

public class XmlUtil {

    public static class AnyTypeAdapter extends XmlAdapter<Object,Object>{

        @Override
        public Object unmarshal(Object v) throws Exception {
            return v;
        }

        @Override
        public Object marshal(Object v) throws Exception {
            return v;
        }

    }

    private XmlUtil() {
    }

    public static <T> T loadFromXml(Class<T> type, String classPathFile) {
        return loadFromXml(type, type.getResourceAsStream(classPathFile));
    }

    public static <T> T loadFromXml(Class<T> type, InputStream xmlStream) {
        try {
            JAXBContext jc = JAXBContext.newInstance(type);
            Unmarshaller u = jc.createUnmarshaller();

            @SuppressWarnings("unchecked")
            T result = (T) u.unmarshal(xmlStream);
            return result;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> void saveToXml(T obj, OutputStream xmlStream) {
        saveToXml(obj, xmlStream, true);
    }

    public static <T> void saveToXml(T obj, OutputStream xmlStream, boolean showSchema) {
        try {
            JAXBContext jc = JAXBContext.newInstance(obj.getClass());
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if(showSchema) {
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
            }
            m.marshal(obj, xmlStream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transforma objeto em XML e realizao download
     *
     * @param obj
     * @param nomeArquivo
     */
    public static <T> void downloadXml(T obj, String nomeArquivo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlUtil.saveToXml(obj, baos, false);
        FileDownloader.download(baos.toByteArray(), "application/xml", nomeArquivo);
    }
}
