package br.com.infox.hibernate.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.jboss.seam.Component;

import br.com.infox.hibernate.session.SessionAssistant;

public class SequenceUtil {
    private static final Pattern SEQUENCE_PATTERN = Pattern.compile("nextval\\('(.*?)'::regclass\\)", Pattern.CASE_INSENSITIVE);
    private static final String COLUMN_INFORMATION_QUERY = "SELECT table_name, column_default, column_name "
            + "from information_schema.columns WHERE table_schema='public' AND column_default IS NOT NULL";

    public void fixSequences() throws SQLException, NamingException {
        try (Connection con = sessionAssistant().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(COLUMN_INFORMATION_QUERY);) {
            while (rs.next()) {
                String tableName = rs.getString(1);
                String columnDefault = rs.getString(2);
                String columnName = rs.getString(3);

                Matcher m = SEQUENCE_PATTERN.matcher(columnDefault);

                if (m.find()) {
                    String sequence = m.group(1);
                    if (!sequence.startsWith("hibernate_sequence")) {
                        Statement st2 = con.createStatement();
                        st2.executeQuery("SELECT setval('" + sequence
                                + "'::regclass, "
                                + (getMaxValue(tableName, columnName) + 1)
                                + "::bigint)");
                        st2.close();
                    }
                }
            }
            rs.close();
            st.close();
            con.close();
        }
    }

    private long getMaxValue(String tableName, String columnName) throws NamingException, SQLException {
        try (Connection con = sessionAssistant().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("select max(" + columnName
                     + ") from " + tableName);) {
            rs.next();
            long value = rs.getLong(1);
            rs.close();
            st.close();
            con.close();
            return value;
        }
    }

    private SessionAssistant sessionAssistant() {
        return (SessionAssistant) Component.getInstance(SessionAssistant.NAME);
    }
}
