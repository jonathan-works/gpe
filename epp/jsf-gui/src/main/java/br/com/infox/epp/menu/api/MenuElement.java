package br.com.infox.epp.menu.api;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import br.com.infox.core.util.XmlUtil.AnyTypeAdapter;

@XmlJavaTypeAdapter(AnyTypeAdapter.class)
public interface MenuElement {
    String getIcon();

    void setIcon(String icon);

    IconAlignment getIconAlignment();

    void setIconAlignment(IconAlignment iconAlignment);

    boolean isHideLabel();

    void setHideLabel(boolean hideLabel);

    String getLabel();

    void setLabel(String label);

    String getPermission();
    void setPermission(String permission);
}