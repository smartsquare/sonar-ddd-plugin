package de.smartsquare.ddd.sonarqube.rules;

import com.google.common.collect.ImmutableList;
import de.smartsquare.ddd.sonarqube.util.TreeUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Rule to check Entities for identity.
 */
@Rule(key = "IdentityProvided")
public class IdentityProvidedCheck extends DDDAwareCheck {

    private Predicate<String> nameMatcher;

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        ClassTree classTree = (ClassTree) tree;
        if (!isEntity(classTree)) {
            return;
        }
        IdentifierTree className = classTree.simpleName();
        if (!hasIdentity(classTree) && className != null) {
            reportIssue(className, "DDD Entity should have an identity");
        }
    }

    private boolean hasIdentity(ClassTree classTree) {
        return hasGetIdMethod(classTree.symbol())
                ||TreeUtil.superClasses(classTree).anyMatch(this::hasGetIdMethod);
    }

    private boolean hasGetIdMethod(Symbol.TypeSymbol classTree) {
        return classTree.memberSymbols()
                .stream()
                .filter(Symbol::isMethodSymbol)
                .map(Symbol::name)
                .anyMatch(matchName());
    }

    private Predicate<String> matchName() {
        if (nameMatcher == null) {
            nameMatcher = Pattern.compile(settings.getString("sonar.ddd.entity.identityMethods")).asPredicate();
        }
        return nameMatcher;
    }
}
