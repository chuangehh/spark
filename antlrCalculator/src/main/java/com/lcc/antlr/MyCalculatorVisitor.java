package com.lcc.antlr;

import antlr.gen.calc.CalculatorBaseVisitor;
import antlr.gen.calc.CalculatorParser;

public class MyCalculatorVisitor extends CalculatorBaseVisitor<Object> {

    @Override
    public Object visitParentExpr(CalculatorParser.ParentExprContext ctx) {
        return ctx.expr().accept(this);
    }

    @Override
    public Object visitAddOrSubstract(CalculatorParser.AddOrSubstractContext ctx) {
        Object object0 = ctx.expr(0).accept(this);
        Object object1 = ctx.expr(1).accept(this);

        if ("+".equals(ctx.getChild(1).getText())) {
            return (Float) object0 + (Float) object1;
        } else if ("-".equals(ctx.getChild(1).getText())) {
            return (Float) object0 - (Float) object1;
        }
        return 0F;
    }

    @Override
    public Object visitMultOrDiv(CalculatorParser.MultOrDivContext ctx) {
        Object object0 = ctx.expr(0).accept(this);
        Object object1 = ctx.expr(1).accept(this);

        if ("*".equals(ctx.getChild(1).getText())) {
            return (Float) object0 * (Float) object1;
        } else if ("/".equals(ctx.getChild(1).getText())) {
            if ((Float) object1 == 0) {
                return 0F;
            }
            return (Float) object0 / (Float) object1;
        }
        return 0F;
    }

    @Override
    public Object visitFloat(CalculatorParser.FloatContext ctx) {
        return Float.parseFloat(ctx.getText());
    }
}
