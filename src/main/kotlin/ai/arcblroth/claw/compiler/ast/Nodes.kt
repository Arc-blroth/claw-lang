package ai.arcblroth.claw.compiler.ast

sealed class TargetNode {
    abstract var functions: ArrayList<FunctionNode>
}

data class StageNode(override var functions: ArrayList<FunctionNode> = arrayListOf()) : TargetNode()

data class SpriteNode(var name: String, override var functions: ArrayList<FunctionNode> = arrayListOf()) : TargetNode()

data class FunctionNode(var name: String, var statements: ArrayList<StatementNode> = arrayListOf())

sealed class StatementNode

data class FunctionCallNode(var functionName: String, var arguments: ArrayList<FunctionArgumentNode> = arrayListOf()) : StatementNode()

data class FunctionArgumentNode(var arg: PrimitiveNode)

sealed class PrimitiveNode

data class NumberPrimitiveNode(var number: Number) : PrimitiveNode()

data class StringPrimitiveNode(var string: String) : PrimitiveNode()
