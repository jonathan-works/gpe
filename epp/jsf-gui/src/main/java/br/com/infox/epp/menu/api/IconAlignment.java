package br.com.infox.epp.menu.api;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum IconAlignment {
    RIGHT, LEFT;
    
    public String toString() {
        return name().toLowerCase();
    };
}