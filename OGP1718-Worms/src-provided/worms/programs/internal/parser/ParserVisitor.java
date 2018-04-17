package worms.programs.internal.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import worms.programs.IProgramFactory;
import worms.programs.SourceLocation;
import worms.programs.internal.parser.generated.WormsProgramBaseVisitor;
import worms.programs.internal.parser.generated.WormsProgramLexer;
import worms.programs.internal.parser.generated.WormsProgramParser.AddSubExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.AndOrExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.AssignmentStatementContext;
import worms.programs.internal.parser.generated.WormsProgramParser.BreakStatementContext;
import worms.programs.internal.parser.generated.WormsProgramParser.ComparisonExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.ConstantExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.CosExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.DistanceExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.EatActionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.FalseLiteralExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.FireActionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.GetAPExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.GetDirectionExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.GetHPExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.GetMaxAPExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.GetRadiusExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.GetXExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.GetYExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.IfStatementContext;
import worms.programs.internal.parser.generated.WormsProgramParser.InvokeStatementContext;
import worms.programs.internal.parser.generated.WormsProgramParser.IsFoodExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.IsProjectileExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.IsWormExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.JumpActionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.MoveActionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.MulDivExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.NotExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.NullExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.ParenExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.PrintStatementContext;
import worms.programs.internal.parser.generated.WormsProgramParser.ProceduredefContext;
import worms.programs.internal.parser.generated.WormsProgramParser.ProgramContext;
import worms.programs.internal.parser.generated.WormsProgramParser.ReadVariableExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.SameTeamExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.SearchObjExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.SelfExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.SequenceStatementContext;
import worms.programs.internal.parser.generated.WormsProgramParser.SinExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.SqrtExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.TrueLiteralExpressionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.TurnActionContext;
import worms.programs.internal.parser.generated.WormsProgramParser.WhileStatementContext;

public class ParserVisitor<E, S, P, Prg> extends WormsProgramBaseVisitor<Void> {

	@Override
	public Void visitProgram(ProgramContext ctx) {
		procedures = ctx.procdef.stream().map(procdef -> procDefVisitor.visit(procdef)).collect(Collectors.toList());
		List<S> mainStmts = ctx.programBody.stream().map(stmt -> statementVisitor.visit(stmt))
				.collect(Collectors.toList());
		if (mainStmts.size() == 1) {
			main = mainStmts.get(0);
		} else {
			main = factory.createSequenceStatement(mainStmts, toSourceLocation(ctx));
		}
		assert main != null;
		return null;
	}

	private class ProcedureDefinitionVisitor extends WormsProgramBaseVisitor<P> {

		@Override
		public P visitProceduredef(ProceduredefContext ctx) {
			return getFactory().createProcedureDefinition(ctx.procname.getText(), statementVisitor.visit(ctx.body),
					toSourceLocation(ctx));
		}
	}

	private class StatementVisitor extends WormsProgramBaseVisitor<S> {

		@Override
		public S visitAssignmentStatement(AssignmentStatementContext ctx) {
			return getFactory().createAssignmentStatement(ctx.variableName.getText(),
					expressionVisitor.visit(ctx.value), toSourceLocation(ctx));
		}

		@Override
		public S visitWhileStatement(WhileStatementContext ctx) {
			return getFactory().createWhileStatement(expressionVisitor.visit(ctx.condition),
					statementVisitor.visit(ctx.body), toSourceLocation(ctx));
		}

		@Override
		public S visitIfStatement(IfStatementContext ctx) {
			S ifBody = statementVisitor.visit(ctx.ifbody);
			S elseBody = null;
			if (ctx.elsebody != null)
				elseBody = statementVisitor.visit(ctx.elsebody);
			return getFactory().createIfStatement(expressionVisitor.visit(ctx.condition), ifBody, elseBody,
					toSourceLocation(ctx));
		}

		@Override
		public S visitPrintStatement(PrintStatementContext ctx) {
			return getFactory().createPrintStatement(expressionVisitor.visit(ctx.value), toSourceLocation(ctx));
		}

		@Override
		public S visitSequenceStatement(SequenceStatementContext ctx) {
			if (ctx.stmts.size() != 1) {
				return getFactory().createSequenceStatement(
						ctx.stmts.stream().map(this::visit).collect(Collectors.toList()), toSourceLocation(ctx));
			} else {
				return visit(ctx.stmts.get(0));
			}
		}

		@Override
		public S visitInvokeStatement(InvokeStatementContext ctx) {
			return getFactory().createInvokeStatement(ctx.procName.getText(), toSourceLocation(ctx));
		}

		@Override
		public S visitBreakStatement(BreakStatementContext ctx) {
			return getFactory().createBreakStatement(toSourceLocation(ctx));
		}

		@Override
		public S visitTurnAction(TurnActionContext ctx) {
			return getFactory().createTurnStatement(expressionVisitor.visit(ctx.angle), toSourceLocation(ctx));
		}

		@Override
		public S visitMoveAction(MoveActionContext ctx) {
			return getFactory().createMoveStatement(toSourceLocation(ctx));
		}

		@Override
		public S visitJumpAction(JumpActionContext ctx) {
			return getFactory().createJumpStatement(toSourceLocation(ctx));
		}

		@Override
		public S visitEatAction(EatActionContext ctx) {
			return getFactory().createEatStatement(toSourceLocation(ctx));
		}

		@Override
		public S visitFireAction(FireActionContext ctx) {
			return getFactory().createFireStatement(toSourceLocation(ctx));
		}

	}

	private class ExpressionVisitor extends WormsProgramBaseVisitor<E> {

		@Override
		public E visitReadVariableExpression(ReadVariableExpressionContext ctx) {
			return getFactory().createReadVariableExpression(ctx.variable.getText(), toSourceLocation(ctx));
		}

		@Override
		public E visitConstantExpression(ConstantExpressionContext ctx) {
			return getFactory().createDoubleLiteralExpression(Double.parseDouble(ctx.value.getText()),
					toSourceLocation(ctx));
		}

		@Override
		public E visitTrueLiteralExpression(TrueLiteralExpressionContext ctx) {
			return getFactory().createBooleanLiteralExpression(true, toSourceLocation(ctx));
		}

		@Override
		public E visitFalseLiteralExpression(FalseLiteralExpressionContext ctx) {
			return getFactory().createBooleanLiteralExpression(false, toSourceLocation(ctx));
		}

		@Override
		public E visitNullExpression(NullExpressionContext ctx) {
			return getFactory().createNullExpression(toSourceLocation(ctx));
		}

		@Override
		public E visitSelfExpression(SelfExpressionContext ctx) {
			return getFactory().createSelfExpression(toSourceLocation(ctx));
		}

		@Override
		public E visitParenExpression(ParenExpressionContext ctx) {
			return expressionVisitor.visit(ctx.subExpr);
		}

		@Override
		public E visitAddSubExpression(AddSubExpressionContext ctx) {
			switch (ctx.op.getType()) {
			case WormsProgramLexer.ADD:
				return getFactory().createAdditionExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.SUB:
				return getFactory().createSubtractionExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			default:
				throw new IllegalArgumentException("Unknown operator: " + ctx.op);
			}
		}

		@Override
		public E visitMulDivExpression(MulDivExpressionContext ctx) {
			switch (ctx.op.getType()) {
			case WormsProgramLexer.MUL:
				return getFactory().createMultiplicationExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.DIV:
				return getFactory().createDivisionExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			default:
				throw new IllegalArgumentException("Unknown operator: " + ctx.op);
			}
		}

		@Override
		public E visitSqrtExpression(SqrtExpressionContext ctx) {
			return getFactory().createSqrtExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitSinExpression(SinExpressionContext ctx) {
			return getFactory().createSinExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitCosExpression(CosExpressionContext ctx) {
			return getFactory().createCosExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitAndOrExpression(AndOrExpressionContext ctx) {
			switch (ctx.op.getType()) {
			case WormsProgramLexer.AND:
				return getFactory().createAndExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.OR:
				return getFactory().createOrExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			default:
				throw new IllegalArgumentException("Unknown operator: " + ctx.op);
			}
		}

		@Override
		public E visitNotExpression(NotExpressionContext ctx) {
			return getFactory().createNotExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitComparisonExpression(ComparisonExpressionContext ctx) {
			switch (ctx.op.getType()) {
			case WormsProgramLexer.LT:
				return getFactory().createLessThanExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.LTE:
				return getFactory().createLessThanOrEqualExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.GT:
				return getFactory().createGreaterThanExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.GTE:
				return getFactory().createGreaterThanOrEqualExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.EQ:
				return getFactory().createEqualityExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			case WormsProgramLexer.NEQ:
				return getFactory().createInequalityExpression(expressionVisitor.visit(ctx.left),
						expressionVisitor.visit(ctx.right), toSourceLocation(ctx));
			default:
				throw new IllegalArgumentException("Unknown operand: " + ctx.op);
			}

		}

		@Override
		public E visitGetXExpression(GetXExpressionContext ctx) {
			return getFactory().createGetXExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitGetYExpression(GetYExpressionContext ctx) {
			return getFactory().createGetYExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitGetRadiusExpression(GetRadiusExpressionContext ctx) {
			return getFactory().createGetRadiusExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitGetDirectionExpression(GetDirectionExpressionContext ctx) {
			return getFactory().createGetDirectionExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitGetAPExpression(GetAPExpressionContext ctx) {
			return getFactory().createGetActionPointsExpression(expressionVisitor.visit(ctx.expr),
					toSourceLocation(ctx));
		}

		@Override
		public E visitGetMaxAPExpression(GetMaxAPExpressionContext ctx) {
			return getFactory().createGetMaxActionPointsExpression(expressionVisitor.visit(ctx.expr),
					toSourceLocation(ctx));
		}

		@Override
		public E visitGetHPExpression(GetHPExpressionContext ctx) {
			return getFactory().createHitPointsExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitSameTeamExpression(SameTeamExpressionContext ctx) {
			return getFactory().createSameTeamExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitSearchObjExpression(SearchObjExpressionContext ctx) {
			return getFactory().createSearchObjectExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitDistanceExpression(DistanceExpressionContext ctx) {
			return getFactory().createDistanceExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitIsWormExpression(IsWormExpressionContext ctx) {
			return getFactory().createIsWormExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitIsFoodExpression(IsFoodExpressionContext ctx) {
			return getFactory().createIsFoodExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

		@Override
		public E visitIsProjectileExpression(IsProjectileExpressionContext ctx) {
			return getFactory().createIsProjectileExpression(expressionVisitor.visit(ctx.expr), toSourceLocation(ctx));
		}

	}

	private final IProgramFactory<E, S, P, Prg> factory;

	private final ProcedureDefinitionVisitor procDefVisitor = new ProcedureDefinitionVisitor();
	private final StatementVisitor statementVisitor = new StatementVisitor();
	private final ExpressionVisitor expressionVisitor = new ExpressionVisitor();

	private S main;
	private List<P> procedures;

	public ParserVisitor(IProgramFactory<E, S, P, Prg> factory) {
		if (factory == null) {
			throw new NullPointerException();
		}
		this.factory = factory;
	}

	private SourceLocation toSourceLocation(ParserRuleContext ctx) {
		int line = ctx.getStart().getLine();
		int column = ctx.getStart().getCharPositionInLine();
		return new SourceLocation(line, column);
	}

	public IProgramFactory<E, S, P, Prg> getFactory() {
		return factory;
	}

	public int toInt(Token z) {
		return Integer.parseInt(z.getText());
	}

	public S getMain() {
		return main;
	}

	public List<P> getProcedures() {
		return Collections.unmodifiableList(procedures);
	}

}