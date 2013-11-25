package br.com.infox.epp.access.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name(PasswordService.NAME)
@Scope(ScopeType.EVENT)
public class PasswordService {
    
    public static final String NAME = "passwordService";

}
