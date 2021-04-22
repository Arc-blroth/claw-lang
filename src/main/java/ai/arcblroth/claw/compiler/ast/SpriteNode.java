package ai.arcblroth.claw.compiler.ast;

import ai.arcblroth.claw.util.ArrayListQueue;

public record SpriteNode(String name, ArrayListQueue<FunctionNode> functions) implements TargetNode {
    public SpriteNode(String name) {
        this(name, new ArrayListQueue<>());
    }
}
