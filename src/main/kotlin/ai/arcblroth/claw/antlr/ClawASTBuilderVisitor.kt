package ai.arcblroth.claw.antlr

import ai.arcblroth.claw.compiler.ast.ClawAST
import ai.arcblroth.claw.compiler.ast.FunctionNode
import ai.arcblroth.claw.compiler.ast.SpriteNode
import org.antlr.v4.runtime.misc.ParseCancellationException

/**
 * Builds a [ClawAST] from the ANTLR CST.
 */
class ClawASTBuilderVisitor : ClawParserBaseListener() {

    private lateinit var ast: ClawAST

    override fun enterCompilationUnit(ctx: ClawParser.CompilationUnitContext) {
        if (this::ast.isInitialized) {
            throw ParseCancellationException("Only one compilation unit may be specified in a file")
        }
        ast = ClawAST()
    }

    override fun enterSpriteDeclaration(ctx: ClawParser.SpriteDeclarationContext) {
        ast.sprites.add(SpriteNode(ctx.IDENTIFIER().text, arrayListOf()))
    }

    override fun enterFunctionDeclaration(ctx: ClawParser.FunctionDeclarationContext) {
        ast.sprites.last().functions.add(FunctionNode(ctx.IDENTIFIER().text))
    }

    fun toAST(): ClawAST {
        return ast
    }
}
