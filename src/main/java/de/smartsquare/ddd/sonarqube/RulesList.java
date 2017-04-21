package de.smartsquare.ddd.sonarqube;

import com.google.common.collect.ImmutableList;
import de.smartsquare.ddd.sonarqube.checks.IdentityProvidedCheck;
import org.sonar.plugins.java.api.JavaCheck;

import java.util.List;

/**
 * List of all rules provided by this plugin.
 */
class RulesList {

    private RulesList() throws InstantiationException {
        throw new InstantiationException("You shall not construct");
    }

    static List<Class<? extends JavaCheck>> checkClasses() {
        return ImmutableList.of(IdentityProvidedCheck.class);
    }

    static List<Class<? extends JavaCheck>> testCheckClasses() {
        return ImmutableList.of();
    }
}