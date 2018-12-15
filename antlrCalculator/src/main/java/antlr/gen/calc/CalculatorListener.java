// Generated from F:/scalaProject/spark/antlrCalculator/src/main\Calculator.g4 by ANTLR 4.7
package antlr.gen.calc;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CalculatorParser}.
 */
public interface CalculatorListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CalculatorParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(CalculatorParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalculatorParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(CalculatorParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multOrDiv}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMultOrDiv(CalculatorParser.MultOrDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multOrDiv}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMultOrDiv(CalculatorParser.MultOrDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addOrSubstract}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddOrSubstract(CalculatorParser.AddOrSubstractContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addOrSubstract}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddOrSubstract(CalculatorParser.AddOrSubstractContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parentExpr}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParentExpr(CalculatorParser.ParentExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parentExpr}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParentExpr(CalculatorParser.ParentExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code float}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFloat(CalculatorParser.FloatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code float}
	 * labeled alternative in {@link CalculatorParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFloat(CalculatorParser.FloatContext ctx);
}