package br.com.infox.epp.processo.documento.entity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@RunWith(Parameterized.class)
public class DocumentoRegrasAssinaturaTest {

    @Parameter(value=0)
    public String testCase;
    @Parameter(value=1)
    public Documento documento;
    @Parameter(value=2)
    public PessoaFisica pessoaFisicaAssinando;
    @Parameter(value=3)
    public Papel papelAssinando;
    @Parameter(value=4)
    public boolean podeAssinar;
    
    
    private static Object[] fromJsonObject(JsonObject testCase){
        Gson gson = new Gson();
        return new Object[]{
            testCase.get("testCase").getAsString(),
            gson.fromJson(testCase.get("documento"), Documento.class),
            gson.fromJson(testCase.get("pessoaFisicaAssinando"), PessoaFisica.class),
            gson.fromJson(testCase.get("papelAssinando"), Papel.class),
            testCase.get("podeAssinar").getAsBoolean()
            
        };
    }
    
    @Parameters(name="{index}: test {0}")
    public static Collection<Object[]> parameters(){
        Collection<Object[]> values=new ArrayList<>();
        try {
            Path casosDeTeste = Paths.get("testCases", DocumentoRegrasAssinaturaTest.class.getName(), "testCases.json");
            for(Enumeration<URL> resources = ClassLoader.getSystemResources(casosDeTeste.toString());resources.hasMoreElements();){
                JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(resources.nextElement().openStream()));
                if (jsonElement.isJsonArray()){
                    for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
                        if (jsonElement2.isJsonObject()){
                            values.add(fromJsonObject(jsonElement2.getAsJsonObject()));
                        }
                    }
                } else if (jsonElement.isJsonObject()){
                    values.add(fromJsonObject(jsonElement.getAsJsonObject()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return values;
    }
    
    @Test
    public void testeAssinatura(){
        Assert.assertEquals(String.format("Falha em '%s'", testCase),podeAssinar, documento.isDocumentoAssinavel(pessoaFisicaAssinando, papelAssinando));
    }
    
}
