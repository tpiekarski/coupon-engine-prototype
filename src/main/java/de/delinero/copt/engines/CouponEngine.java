package de.delinero.copt.engines;

import de.delinero.copt.models.*;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.RulesEngineBuilder;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.List;

public class CouponEngine {

    private final RulesEngine rulesEngine;
    private final CouponExpressionEngine expressionEngine;

    public CouponEngine() {
        this(true);
    }

    public CouponEngine(Boolean silent) {
        this.rulesEngine = RulesEngineBuilder.aNewRulesEngine().withSilentMode(silent).build();
        this.expressionEngine = new CouponExpressionEngine(new SpelExpressionParser());
    }

    public Boolean evaluate(Cart cart, CouponRuleSet ruleSet) {
        Rules rules = new Rules();
        registerRules(rules, ruleSet.getRules());

        List<EvaluatedResult> results = initializeResults(rules);
        Facts facts = establishFacts(cart, ruleSet, results);

        rulesEngine.fire(rules, facts);

        return expressionEngine.parse(ruleSet.getExpression(), results);
    }

    private Facts establishFacts(Cart cart, CouponRuleSet ruleSet, List<EvaluatedResult> results) {
        Facts facts = new Facts();

        facts.put("cart", cart);
        facts.put("ruleSet", ruleSet);
        facts.put("results", results);

        return facts;
    }

    private void registerRules(Rules rules, List<CouponRule> couponRules) {
        for (CouponRule rule : couponRules) {
            try {
                Class<?> ruleClass = Class.forName(String.format("de.delinero.copt.rules.%s", rule.getRuleName()));
                rules.register(ruleClass.newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException exception) {
                break;
            }
        }
    }

    private List<EvaluatedResult> initializeResults(Rules rules) {
        List<EvaluatedResult> results = new ArrayList<>();
        rules.forEach((rule -> results.add(new EvaluatedResult(rule.getName(), false))) );

        return results;
    }

}
