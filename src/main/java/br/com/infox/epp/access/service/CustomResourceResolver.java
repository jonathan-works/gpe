package br.com.infox.epp.access.service;

import java.net.URL;

import com.sun.faces.facelets.impl.DefaultResourceResolver;

public class CustomResourceResolver extends DefaultResourceResolver {
    @Override
    public URL resolveUrl(String resource) {
        URL resourceUrl = super.resolveUrl(resource);
        if (resourceUrl == null) {
            if (resource.startsWith("/")) {
                return Thread.currentThread().getContextClassLoader().getResource(resource.substring(1));
            } else {
                return Thread.currentThread().getContextClassLoader().getResource(resource);
            }
        }
        return resourceUrl;
    }
}
