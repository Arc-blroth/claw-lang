package ai.arcblroth.claw.compiler.ast

sealed class TargetNode {
    abstract var functions: ArrayList<FunctionNode>
}

data class StageNode(override var functions: ArrayList<FunctionNode> = arrayListOf()) : TargetNode()

data class SpriteNode(var name: String, override var functions: ArrayList<FunctionNode> = arrayListOf()) : TargetNode()

data class FunctionNode(var name: String)
