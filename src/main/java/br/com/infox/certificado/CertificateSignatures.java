package br.com.infox.certificado;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.certificado.bean.CertificateSignatureBundleBean;

import com.google.gson.Gson;

@Name(CertificateSignatures.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class CertificateSignatures implements Serializable {
    private static final String FILENAME = "signature-session.properties";
    public static final String NAME = "certificateSignatures";
    private static final long serialVersionUID = 1L;

    private Map<String, CertificateSignatureBundleBean> signatures = new ConcurrentHashMap<>();

    public CertificateSignatureBundleBean get(String token) {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signatures.get(token);
    }

    public void put(String token, CertificateSignatureBundleBean bundle) {
        try {
            load();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        signatures.put(token, bundle);
        try {
            store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() throws FileNotFoundException, IOException {
        File file = new File(FILENAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        signatures.clear();
        for (Object object : properties.keySet()) {
            signatures.put(object.toString(), new Gson().fromJson(properties.getProperty(object.toString()),
                    CertificateSignatureBundleBean.class));
        }

    }

    private void store() throws FileNotFoundException, IOException {
        File file = new File(FILENAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        Properties properties = new Properties();
        for (String string : signatures.keySet()) {
            properties.put(string, new Gson().toJson(signatures.get(string)));
        }
        properties.store(new FileOutputStream(file), "");
    }

}
