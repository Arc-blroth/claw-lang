package ai.arcblroth.claw.compiler.pass

import ai.arcblroth.claw.compiler.ClawCompilationUnit
import ai.arcblroth.claw.compiler.ClawIntrinstics
import ai.arcblroth.claw.compiler.ast.ClawAST
import ai.arcblroth.claw.compiler.ast.ClawASTVisitor
import ai.arcblroth.claw.compiler.ast.FunctionCallNode
import ai.arcblroth.claw.compiler.ast.FunctionNode
import ai.arcblroth.claw.compiler.ast.NumberPrimitiveNode
import ai.arcblroth.claw.compiler.ast.SpriteNode
import ai.arcblroth.claw.compiler.ast.StageNode
import ai.arcblroth.claw.compiler.ast.StringPrimitiveNode
import ai.arcblroth.claw.scratch.BlockFactory
import ai.arcblroth.claw.scratch.BlockWithId
import ai.arcblroth.claw.scratch.Costume
import ai.arcblroth.claw.scratch.Project
import ai.arcblroth.claw.scratch.Project.Meta
import ai.arcblroth.claw.scratch.Sprite
import ai.arcblroth.claw.scratch.Stage
import ai.arcblroth.claw.scratch.Target
import ai.arcblroth.claw.scratch.pushBlock
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt

class ClawAST2ScratchProjectVisitor : ClawASTVisitor() {
    private val blockFactory = BlockFactory()

    private lateinit var out: Project
    private var lastTarget = ArrayDeque<Target>()
    private var lastBlocks = ArrayDeque<ArrayList<BlockWithId>>()
    private var lastBlock = ArrayDeque<BlockWithId>()

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
        val target = Stage(
            0,
            HashMap(),
            HashMap(),
            HashMap(),
            HashMap(),
            HashMap(),
            arrayListOf(),
            arrayListOf(),
            OptionalDouble.empty(),
            OptionalDouble.empty(),
            OptionalDouble.empty(),
            Optional.empty(),
            OptionalInt.empty()
        )
        target.costumes.add(Costume.EMPTY_COSTUME)
        out.targets.add(target)
        lastTarget.addLast(target)
        super.visitStage(stageNode)
        lastTarget.removeLast()
    }

    override fun visitSprite(spriteNode: SpriteNode) {
        val target = Sprite(
            0,
            HashMap(),
            HashMap(),
            HashMap(),
            HashMap(),
            HashMap(),
            arrayListOf(),
            arrayListOf(),
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
        target.costumes.add(Costume.EMPTY_COSTUME)
        out.targets.add(target)
        lastTarget.addLast(target)
        super.visitSprite(spriteNode)
        lastTarget.removeLast()
    }

    override fun visitFunction(functionNode: FunctionNode) {
        lastBlocks.addLast(arrayListOf())
        if (functionNode.name == ClawIntrinstics.ON_GREEN_FLAG_NAME) {
            val block = blockFactory.onGreenFlag()
            lastBlocks.last().add(block)
            lastBlock.addLast(block)
        }
        super.visitFunction(functionNode)
        lastBlock.removeLast()
        lastBlocks.removeLast().forEach {
            lastTarget.last().blocks.pushBlock(it)
        }
    }

    override fun visitFunctionCall(functionCallNode: FunctionCallNode) {
        // At this point, all function calls should be intrinsics
        val arguments = functionCallNode.arguments
            .map { it.arg }
            .map {
                when (it) {
                    is NumberPrimitiveNode -> it.number
                    is StringPrimitiveNode -> it.string
                }
            }
            .toTypedArray()
        appendBlock(blockFactory.buildStatementBlock(functionCallNode.functionName, *arguments))
        super.visitFunctionCall(functionCallNode)
    }

    private fun appendBlock(block: BlockWithId) {
        val lastLastBlock = lastBlock.last()
        block.parent = Optional.of(lastLastBlock)
        lastLastBlock.next = Optional.of(block)
        lastBlocks.last().add(block)
        lastBlock.swap(block)
    }

    private fun <E> ArrayDeque<E>.swap(element: E) {
        synchronized(this) {
            this.removeLast()
            this.addLast(element)
        }
    }

    fun toProject(): Project {
        return out
    }
}
