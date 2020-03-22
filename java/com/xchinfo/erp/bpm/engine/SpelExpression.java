package com.xchinfo.erp.bpm.engine;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Spring el表达式解析器
 *
 * @author roman.li
 * @date 2019/3/21
 * @update
 */
//@Component
public class SpelExpression implements Expression {
    ExpressionParser parser = new SpelExpressionParser();

    @Override
    public <T> T eval(Class<T> T, String expr, Map<String, Object> params) {
        EvaluationContext context = new StandardEvaluationContext();

        for(Entry<String, Object> entry : params.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        return parser.parseExpression(expr).getValue(context, T);
    }
}
