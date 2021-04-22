package ai.arcblroth.claw.compiler.ast;

import ai.arcblroth.claw.util.ArrayListQueue;

public record StageNode(ArrayListQueue<FunctionNode> functions) implements TargetNode {
    public StageNode() {
        this(new ArrayListQueue<>());
    }
}
