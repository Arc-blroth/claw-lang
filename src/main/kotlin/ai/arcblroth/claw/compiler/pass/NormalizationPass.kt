package ai.arcblroth.claw.compiler.pass

import ai.arcblroth.claw.compiler.ast.ClawAST
import ai.arcblroth.claw.compiler.ast.StageNode

/**
 * Initial pass to add defaults.
 */
class NormalizationPass : CompilerPass() {
    override fun visitAST(ast: ClawAST) {
        if (ast.stage == null) {
            ast.stage = StageNode(ArrayList())
        }
        super.visitAST(ast)
    }
}
