package br.com.infox.epp.processo.comunicacao;

import java.util.Arrays;
import java.util.Comparator;

import br.com.infox.core.type.Displayable;

public enum MeioExpedicao implements Displayable {
    EM("Email"), DO("Diário Oficial"), SI("Via Sistema (eletrônica)"), IM("Impressão");

    private String label;

    private MeioExpedicao(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static MeioExpedicao[] getValues(boolean podeEnviarEletronico) {
        if (podeEnviarEletronico) {
            return order(values());
        }
        return order(new MeioExpedicao[] { EM, DO, IM });
    }

    private static MeioExpedicao[] order(MeioExpedicao[] original) {
        Arrays.sort(original, new Comparator<MeioExpedicao>() {
            @Override
            public int compare(MeioExpedicao o1, MeioExpedicao o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        return original;
    }
}
