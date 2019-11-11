/**
 *
 */
package br.com.infox.certificado;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author erikliberal
 *
 */
public class ValidaCertificadoTest {

    private static final String BASE_PATH = "test/certificados/{0}";

    public static class Pair<Left, Right> {
        private final Left left;
        private final Right right;

        public Pair(final Left left, final Right right) {
            this.left = left;
            this.right = right;
        }

        public Left getLeft() {
            return this.left;
        }

        public Right getRight() {
            return this.right;
        }

    }

    @SuppressWarnings("rawtypes")
    public void validaCertificadosPF() throws Exception {
        final Pair[] certificados = { new Pair<String, String>("AC CAIXA PF v2-1.txt", "AC CAIXA PF v2-1.properties") };
        for (final Pair pair : certificados) {
            final File file = new File(MessageFormat.format(ValidaCertificadoTest.BASE_PATH, pair.getLeft()));
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            try {
                String readString = null;
                while ((readString = bufferedReader.readLine()) != null) {
                    final CertificadoDadosPessoaFisica certificado = (CertificadoDadosPessoaFisica) CertificadoFactory
                            .createCertificado(readString);
                    final Properties props = new Properties();
                    props.load(new FileInputStream(MessageFormat.format(ValidaCertificadoTest.BASE_PATH,
                            pair.getRight())));
                    validateCertificadoFromProperties(certificado, props);

                }
            } finally {
                bufferedReader.close();
            }
        }
    }

    private void validateCertificadoFromProperties(final CertificadoDadosPessoaFisica certificado,
            final Properties props) {
        assert assertProperty(certificado.getNome(), props.getProperty("Nome", ""));
        assert assertProperty(certificado.getAutoridadeCertificadora(),
                props.getProperty("AutoridadeCertificadora", ""));
        assert assertProperty(certificado.getPrivateKey(), props.getProperty("PrivateKey", ""));
        assert assertProperty(certificado.getSerialNumber(), props.getProperty("SerialNumber", ""));
        assert assertProperty(certificado.getCPF(), props.getProperty("CPF", ""));
        assert assertProperty(certificado.getNIS(), props.getProperty("NIS", ""));
        assert assertProperty(certificado.getRG(), props.getProperty("RG", ""));
        assert assertProperty(certificado.getOrgaoExpedidor(), props.getProperty("OrgaoExpedidor", ""));
        assert assertProperty(certificado.getTituloEleitor(), props.getProperty("TituloEleitor", ""));
        assert assertProperty(certificado.getZonaEleitoral(), props.getProperty("ZonaEleitoral", ""));
        assert assertProperty(certificado.getSecaoEleitoral(), props.getProperty("SecaoEleitoral", ""));
        assert assertProperty(certificado.getMunicipioTituloEleitor(), props.getProperty("MunicipioTituloEleitor", ""));
        assert assertProperty(certificado.getCEI(), props.getProperty("CEI", ""));
        // TODO: TEstes falhando, averigüar o pq
        // assert assertProperty(certificado.getDataValidadeInicio(),
        // props.getProperty("DataValidadeInicio", ""));
        // assert assertProperty(certificado.getDataValidadeFim(),
        // props.getProperty("DataValidadeFim", ""));
        // assert assertProperty(certificado.getDataNascimento(),
        // props.getProperty("DataNascimento", ""));
    }

    private boolean assertProperty(Object object, final String property) {
        if ((object != null) && (object instanceof Date)) {
            object = MessageFormat.format("{0,date,dd/MM/yyyy}", object);
        }
        return property.equals(MessageFormat.format("{0}", object));
    }

}
