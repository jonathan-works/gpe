package br.com.infox.epp.processo.documento.entity;

import static org.mockito.Mockito.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import javax.inject.Inject;

import org.jglue.cdiunit.ParameterizedCdiRunner;
import org.jglue.cdiunit.ejb.SupportEjb;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.domain.Assinavel;
import br.com.infox.epp.documento.domain.RegraAssinaturaService;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import lombok.Setter;

@RunWith(ParameterizedCdiRunner.class)
@SupportEjb
public class DocumentoRegrasAssinaturaTest {

    @Setter
    @Parameter(value=0)
    private String testCase;
    @Setter
    @Parameter(value=1)
    private Documento documento;
    @Setter
    @Parameter(value=2)
    private PessoaFisica pessoaFisicaAssinando;
    @Setter
    @Parameter(value=3)
    private Papel papelAssinando;
    @Setter
    @Parameter(value=4)
    private boolean podeAssinar;
    
    @Inject
    private RegraAssinaturaService regraAssinaturaService;
    
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
        Assert.assertEquals(String.format("Falha em '%s'", testCase),podeAssinar, regraAssinaturaService.permiteAssinaturaPor(documento, pessoaFisicaAssinando, papelAssinando));
    }
    
}
