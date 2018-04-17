// Generated from WormsProgram.g4 by ANTLR 4.7.1

    package worms.programs.internal.parser.generated;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WormsProgramParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WormsProgramVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(WormsProgramParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#proceduredef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProceduredef(WormsProgramParser.ProceduredefContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(WormsProgramParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#assignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(WormsProgramParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(WormsProgramParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(WormsProgramParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#printStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStatement(WormsProgramParser.PrintStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#sequenceStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequenceStatement(WormsProgramParser.SequenceStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#invokeStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvokeStatement(WormsProgramParser.InvokeStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WormsProgramParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(WormsProgramParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code turnAction}
	 * labeled alternative in {@link WormsProgramParser#actionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTurnAction(WormsProgramParser.TurnActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code moveAction}
	 * labeled alternative in {@link WormsProgramParser#actionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMoveAction(WormsProgramParser.MoveActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code jumpAction}
	 * labeled alternative in {@link WormsProgramParser#actionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJumpAction(WormsProgramParser.JumpActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eatAction}
	 * labeled alternative in {@link WormsProgramParser#actionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEatAction(WormsProgramParser.EatActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fireAction}
	 * labeled alternative in {@link WormsProgramParser#actionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFireAction(WormsProgramParser.FireActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code distanceExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDistanceExpression(WormsProgramParser.DistanceExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cosExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCosExpression(WormsProgramParser.CosExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code getYExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetYExpression(WormsProgramParser.GetYExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andOrExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndOrExpression(WormsProgramParser.AndOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code isWormExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsWormExpression(WormsProgramParser.IsWormExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(WormsProgramParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparisonExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExpression(WormsProgramParser.ComparisonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpression(WormsProgramParser.ParenExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code getRadiusExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetRadiusExpression(WormsProgramParser.GetRadiusExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code trueLiteralExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueLiteralExpression(WormsProgramParser.TrueLiteralExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullExpression(WormsProgramParser.NullExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sqrtExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSqrtExpression(WormsProgramParser.SqrtExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sinExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSinExpression(WormsProgramParser.SinExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sameTeamExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSameTeamExpression(WormsProgramParser.SameTeamExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code isProjectileExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsProjectileExpression(WormsProgramParser.IsProjectileExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code isFoodExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsFoodExpression(WormsProgramParser.IsFoodExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constantExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantExpression(WormsProgramParser.ConstantExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code searchObjExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearchObjExpression(WormsProgramParser.SearchObjExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code getXExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetXExpression(WormsProgramParser.GetXExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code readVariableExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadVariableExpression(WormsProgramParser.ReadVariableExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mulDivExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivExpression(WormsProgramParser.MulDivExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code getDirectionExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetDirectionExpression(WormsProgramParser.GetDirectionExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selfExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelfExpression(WormsProgramParser.SelfExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code getHPExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetHPExpression(WormsProgramParser.GetHPExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addSubExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSubExpression(WormsProgramParser.AddSubExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code getMaxAPExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetMaxAPExpression(WormsProgramParser.GetMaxAPExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code getAPExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetAPExpression(WormsProgramParser.GetAPExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code falseLiteralExpression}
	 * labeled alternative in {@link WormsProgramParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseLiteralExpression(WormsProgramParser.FalseLiteralExpressionContext ctx);
}