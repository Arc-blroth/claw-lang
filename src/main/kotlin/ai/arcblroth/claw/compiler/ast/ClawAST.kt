package ai.arcblroth.claw.compiler.ast

data class ClawAST(
    var stage: StageNode? = null,
    var sprites: ArrayList<SpriteNode> = arrayListOf(),
)

abstract class ClawASTVisitor {
    open fun visitAST(ast: ClawAST) {
        if (ast.stage != null) {
            visitStage(ast.stage!!)
        }
        ast.sprites.forEach { spriteNode: SpriteNode ->
            visitSprite(spriteNode)
        }
    }

    open fun visitStage(stageNode: StageNode) {
        visitTarget(stageNode)
    }

    open fun visitSprite(spriteNode: SpriteNode) {
        visitTarget(spriteNode)
    }

    open fun visitTarget(targetNode: TargetNode) {
        targetNode.functions.forEach { visitFunction(it) }
    }

    open fun visitFunction(functionNode: FunctionNode?) {}
}
