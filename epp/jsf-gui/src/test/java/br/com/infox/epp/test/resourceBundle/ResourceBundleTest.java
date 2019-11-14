package br.com.infox.epp.test.resourceBundle;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Before;

/**
 * @author Luciano Sampaio
 */
public class ResourceBundleTest {

    private Locale[] supportedLocales = null;

    @Before
    public void setUp() throws Exception {
        supportedLocales = new Locale[] { new Locale("pt", "BR"),
                new Locale("en", "US"), new Locale("fr", "FR") };
    }

//    @Test
    public void test_01() {
        testFile("entity_messages");
    }

//    @Test
    public void test_02() {
        testFile("messages");
    }

//    @Test
    public void test_03() {
        testFile("process_definition_messages");
    }

//    @Test
    public void test_04() {
        testFile("standard_messages");
    }

    private void testFile(final String baseName) {
        List<ResourceBundle> resourceBundles = new ArrayList<ResourceBundle>();

        // 01 - Add into the list, the resourceBundle of the supported locales.
        for (Locale locale : supportedLocales) {
            resourceBundles.add(ResourceBundle.getBundle(baseName, locale));
        }

        // 02 - This object will hold all the key values from the resourceBundle
        // files.
        List<List<String>> keys = new ArrayList<List<String>>();

        for (ResourceBundle bundle : resourceBundles) {
            keys.add(Collections.list(bundle.getKeys()));
        }

        // 03 - Test if one file has more keys than the other.
        for (int i = 0; i < keys.size(); i++) {
            List<String> keysFirstFile = keys.get(i);

            // To avoid IndexOutOfBounds.
            if (keys.size() < (i + 1)) {
                break;
            }

            List<String> keysSecondFile = keys.get(i + 1);

            // 04 - Check if the files have the same amount of key/value pairs.
            assertEquals(
                    String.format(
                            "O arquivo '%s_%s' não tem a mesma quantidade chave/valor que o arquivo '%s_%s'.",
                            baseName, resourceBundles.get(i).getLocale()
                                    .toString(), baseName,
                            resourceBundles.get(i + 1).getLocale().toString()),
                    keysFirstFile.size(), keysSecondFile.size());

            // 05 - Check if the files have the key/value pairs in the same
            // order.
            int numberOfElements = keysFirstFile.size();
            for (int j = 0; j < numberOfElements; j++) {
                String keyUS = keysFirstFile.get(j);
                String keyBR = keysSecondFile.get(j);

                // 2º - Test if the keys are in the same order.
                assertEquals(keyUS, keyBR);
            }
        }

    }

}
