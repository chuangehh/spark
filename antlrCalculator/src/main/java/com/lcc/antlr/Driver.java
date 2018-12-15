package com.lcc.antlr;

import antlr.gen.calc.CalculatorLexer;
import antlr.gen.calc.CalculatorParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * 计算器驱动
 *
 * @author lcc
 */
public class Driver {

    public static void main(String[] args) {
        String query = "3.0 * (6.51 - 4.51) ";

        // 词法解析器
        CalculatorLexer lexer = new CalculatorLexer(new ANTLRInputStream(query));
        // 语法解析器
        CalculatorParser parser = new CalculatorParser(new CommonTokenStream(lexer));

        MyCalculatorVisitor visitor = new MyCalculatorVisitor();
        System.out.println(visitor.visit(parser.expr()));
    }
}
