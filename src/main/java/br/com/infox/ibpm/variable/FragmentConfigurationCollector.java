package br.com.infox.ibpm.variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;

@Singleton
@Named(FragmentConfigurationCollector.NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class FragmentConfigurationCollector implements Serializable {
    public static final String NAME = "fragmentConfigurationCollector";

    public Collection<FragmentConfiguration> getAvailableFragmentConfigurations() {
        return new ArrayList<>();
    }

    public FragmentConfiguration getByCode(String code) {
        return null;
    }

    private static final long serialVersionUID = 1L;
}
