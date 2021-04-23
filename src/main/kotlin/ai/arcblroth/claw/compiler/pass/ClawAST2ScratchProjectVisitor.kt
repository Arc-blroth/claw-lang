package ai.arcblroth.claw.compiler.pass

import ai.arcblroth.claw.compiler.ClawCompilationUnit
import ai.arcblroth.claw.compiler.ast.ClawAST
import ai.arcblroth.claw.compiler.ast.ClawASTVisitor
import ai.arcblroth.claw.compiler.ast.SpriteNode
import ai.arcblroth.claw.compiler.ast.StageNode
import ai.arcblroth.claw.scratch.Costume
import ai.arcblroth.claw.scratch.Project
import ai.arcblroth.claw.scratch.Project.Meta
import ai.arcblroth.claw.scratch.Sprite
import ai.arcblroth.claw.scratch.Stage
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt

class ClawAST2ScratchProjectVisitor : ClawASTVisitor() {
    private lateinit var out: Project

    override fun visitAST(ast: ClawAST) {
        out = Project(
            Meta(
                "3.0.0",
                "0.2.0-claw",
                "Claw/" + ClawCompilationUnit.COMPILER_VERSION
            ),
            ArrayList()
        )
        super.visitAST(ast)
    }

    override fun visitStage(stageNode: StageNode) {
        val costumes = ArrayList<Costume>()
        costumes.add(Costume.EMPTY_COSTUME)
        out.targets.add(
            Stage(
                0,
                HashMap(),
                HashMap(),
                HashMap(),
                HashMap(),
                HashMap(),
                costumes,
                ArrayList(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                Optional.empty(),
                OptionalInt.empty()
            )
        )
        super.visitStage(stageNode)
    }

    override fun visitSprite(spriteNode: SpriteNode) {
        val costumes = ArrayList<Costume>()
        costumes.add(Costume.EMPTY_COSTUME)
        out.targets.add(
            Sprite(
                0,
                HashMap(),
                HashMap(),
                HashMap(),
                HashMap(),
                HashMap(),
                costumes,
                ArrayList(),
                OptionalDouble.empty(),
                spriteNode.name,
                Optional.of(true),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                Optional.of(false),
                Sprite.RotationStyle.ALL_AROUND,
                OptionalInt.empty()
            )
        )
        super.visitSprite(spriteNode)
    }

    fun toProject(): Project {
        return out
    }
}
