package br.com.infox.bpm.cdi.qualifier;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.transaction.Transactional;

/**
 * 
 * @author victorpasqualino
 * Essa classe deve ser apagada e colocada no liquibase para migrar os dados
 * 
 */
@Named
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class TesteStart implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private transient Connection connection;
    private Gson gson = new GsonBuilder().create();
    
    @Transactional(timeout = 900)
    public void init() {
        try {
            connection = ApplicationServerService.instance().getDataSource("EPADataSource").getConnection();
            addLabelAndTypeToFormVariables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLabelAndTypeToFormVariables() throws Exception {
        List<Fluxo> fluxos = getFluxos("Principal");
        for (Fluxo fluxo : fluxos) {
            List<Map.Entry<String, String>> variablesMapName = getMapVariablesName(fluxo.getNome());
            if (!StringUtil.isEmpty(fluxo.getXml())) {
                String newXml = alterVariableLabelAndTypeAndConfiguration(fluxo.getXml(), variablesMapName);
                if (!fluxo.getXml().equals(newXml)) {
                    fluxo.setXml(newXml);
                }
            }
            if (!StringUtil.isEmpty(fluxo.getXmlExecucao())) {
                String newXml = alterVariableLabelAndTypeAndConfiguration(fluxo.getXmlExecucao(), variablesMapName);
                if (!fluxo.getXmlExecucao().equals(newXml)) {
                    fluxo.setXmlExecucao(newXml);
                }
            }
        }
    }
    
    private String alterVariableLabelAndTypeAndConfiguration(String xml, List<Map.Entry<String, String>> variablesMapName) throws Exception {
        InputStream inputStream = IOUtils.toInputStream(xml, "UTF-8");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        
        Node processDefinitionNode = doc.getFirstChild();
        String processDefinitionName = processDefinitionNode.getAttributes().getNamedItem("name").getTextContent();
        
        NodeList variableNodes = doc.getElementsByTagName("variable");
        
        for (int i = 0; i < variableNodes.getLength(); i++) {
            
            Node variableNode = variableNodes.item(i);
            NamedNodeMap attr = variableNode.getAttributes();
            
            String mappedName = attr.getNamedItem("mapped-name").getTextContent();
            String variableName = attr.getNamedItem("name").getTextContent();
            
            Attr labelAttr = doc.createAttribute("label");
            String label = getLabel(variablesMapName, processDefinitionName.concat(":").concat(variableName));
            labelAttr.setValue(label);
            attr.setNamedItem(labelAttr);
            
            Attr typeAttr = doc.createAttribute("type");
            typeAttr.setValue(mappedName.split(":")[0]);
            attr.setNamedItem(typeAttr);
            
            //TODO: Criar coluna de configuração no JBPM_VARIABLEACCESS e criar arquitetura para guardar
            if (mappedName.split(":").length > 2) {
                Attr configurationAttr = doc.createAttribute("configuration");
                configurationAttr.setValue(mappedName.split(":")[2]);
                attr.setNamedItem(configurationAttr);
            }
        }
        
        return convertNewXml(doc);
    }

    private String alterVariableClassificacaoAndModeloDocumento(String xml) {
        
    }
    
    public List<Map.Entry<String, String>> getMapVariablesName(String processDefinitionName) throws SQLException {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        String sql = "select nm_variavel, ds_label_variavel from tb_jbpm_variavel_label where nm_variavel like concat(? , ':%') order by id_jbpm_variavel_label desc";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, processDefinitionName);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String name = rs.getString(1);
            String label = rs.getString(2);
            Map.Entry<String, String> entry = Pair.create(name, label);
            if (!result.contains(entry)) {
                result.add(entry);
            }
        }
        return result;
    }
    
    public String getLabel(List<Map.Entry<String, String>> labels, String variableName) {
        for (Map.Entry<String, String> map : labels) {
            if (map.getKey().equals(variableName)) {
                return map.getValue();
            }
        }
        return null;
    }
    
    private String convertNewXml(Document doc) throws Exception {
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(source, result);
        return stringWriter.toString();
    }
    
    public List<Fluxo> getFluxos() throws SQLException {
        return getFluxos(null);
    }
    
    public List<Fluxo> getFluxos(String name) throws SQLException {
        List<Fluxo> result = new ArrayList<>();
        String sql = "select id_fluxo, ds_fluxo, ds_xml, ds_xml_exec from tb_fluxo";
        if (!isEmpty(name)) {
            sql = sql.concat(" where ds_fluxo = ? ");
        }
        PreparedStatement ps = connection.prepareStatement(sql);
        if (!isEmpty(name)) {
            ps.setString(1, name);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Fluxo fluxo = new Fluxo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            result.add(fluxo);
        }
        return result;
    }
    
    private boolean isEmpty(Object value) {
        if (value instanceof String) {
            return value != null && ((String) value).trim().length() != 0;
        }
        if (value instanceof Collection<?>) {
            return ((Collection<?>) value).isEmpty();
        }
        return false;
    }
    
    private static class Fluxo {
        
        private Integer id;
        private String nome;
        private String xml;
        private String xmlExecucao;
        
        public Fluxo(Integer id, String nome, String xml, String xmlExecucao) {
            this.id = id;
            this.nome = nome;
            this.xml = xml;
            this.xmlExecucao = xmlExecucao;
        }

        public Integer getId() {
            return id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }
        
        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getXml() {
            return xml;
        }
        
        public void setXml(String xml) {
            this.xml = xml;
        }
        
        public String getXmlExecucao() {
            return xmlExecucao;
        }
        
        public void setXmlExecucao(String xmlExecucao) {
            this.xmlExecucao = xmlExecucao;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof Fluxo))
                return false;
            Fluxo other = (Fluxo) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            return true;
        }
        
    }
    
    private static class Pair<K, V> implements Map.Entry<K, V> {
        
        private K key;
        private V value;
        
        public static <K, V> Pair<K, V> create(K key, V value) {
            Pair<K, V> pair = new Pair<>();
            pair.setKey(key);
            pair.setValue(value);
            return pair;
        }

        @Override
        public K getKey() {
            return key;
        }
        
        public void setKey(K key) {
            this.key = key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public String toString() {
            return "Pair [key=" + key + ", value=" + value + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof Pair))
                return false;
            Pair<?, ?> other = (Pair<?, ?>) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            return true;
        }
    }

}
