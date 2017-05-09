package de.smartsquare.ddd.sonarqube.rules;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * List of all rules provided by this plugin.
 */
public class RulesList {

    public static final String REPOSITORY_KEY = "ddd";
    public static final String REPOSITORY_NAME = "Domain Driven Design";

    private RulesList() throws InstantiationException {
        throw new InstantiationException("You shall not construct");
    }

    public static List<Class<? extends DDDAwareCheck>> checkClasses() {
        return ImmutableList.of(IdentityProvidedCheck.class, ImmutabilityCheck.class);
    }
}