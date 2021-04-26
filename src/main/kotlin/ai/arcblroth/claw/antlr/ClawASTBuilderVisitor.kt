package ai.arcblroth.claw.antlr

import ai.arcblroth.claw.compiler.ast.ClawAST
import ai.arcblroth.claw.compiler.ast.FunctionArgumentNode
import ai.arcblroth.claw.compiler.ast.FunctionCallNode
import ai.arcblroth.claw.compiler.ast.FunctionNode
import ai.arcblroth.claw.compiler.ast.NumberPrimitiveNode
import ai.arcblroth.claw.compiler.ast.SpriteNode
import ai.arcblroth.claw.compiler.ast.StringPrimitiveNode
import ai.arcblroth.claw.compiler.ast.TargetNode
import org.antlr.v4.runtime.misc.ParseCancellationException

/**
 * Builds a [ClawAST] from the ANTLR CST.
 */
class ClawASTBuilderVisitor : ClawParserBaseListener() {
    private lateinit var ast: ClawAST

    private var lastTarget = ArrayDeque<TargetNode>()
    private var lastFunction = ArrayDeque<FunctionNode>()
    private var lastFunctionCall = ArrayDeque<FunctionCallNode>()

    override fun enterCompilationUnit(ctx: ClawParser.CompilationUnitContext) {
        if (this::ast.isInitialized) {
            throw ParseCancellationException("Only one compilation unit may be specified in a file")
        }
        ast = ClawAST()
    }

    override fun enterSpriteDeclaration(ctx: ClawParser.SpriteDeclarationContext) {
        val sprite = SpriteNode(ctx.IDENTIFIER().text, arrayListOf())
        lastTarget.addLast(sprite)
    }

    override fun exitSpriteDeclaration(ctx: ClawParser.SpriteDeclarationContext) {
        ast.sprites.add(lastTarget.removeLast() as SpriteNode)
    }

    override fun enterFunctionDeclaration(ctx: ClawParser.FunctionDeclarationContext) {
        val function = FunctionNode(ctx.IDENTIFIER().text)
        lastFunction.addLast(function)
    }

    override fun exitFunctionDeclaration(ctx: ClawParser.FunctionDeclarationContext?) {
        lastTarget.last().functions.add(lastFunction.removeLast())
    }

    override fun enterFunctionCall(ctx: ClawParser.FunctionCallContext) {
        val node = FunctionCallNode(ctx.IDENTIFIER().text)
        lastFunctionCall.addLast(node)
    }

    override fun enterFunctionCallArg(ctx: ClawParser.FunctionCallArgContext) {
        lastFunctionCall.last().arguments.add(
            FunctionArgumentNode(
                if (ctx.primitive().NumberLiteral() != null) {
                    NumberPrimitiveNode(ctx.primitive().NumberLiteral().text.toDouble())
                } else {
                    StringPrimitiveNode(
                        ctx.primitive().StringLiteral().text.run {
                            // remove the quotes
                            substring(1, length - 1)
                        }
                    )
                }
            )
        )
    }

    override fun exitFunctionCall(ctx: ClawParser.FunctionCallContext?) {
        lastFunction.last().statements.add(lastFunctionCall.removeLast())
    }

    fun toAST(): ClawAST {
        return ast
    }
}
