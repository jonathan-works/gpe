package br.com.infox.ibpm.variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name(FragmentConfigurationCollector.NAME)
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.APPLICATION)
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
