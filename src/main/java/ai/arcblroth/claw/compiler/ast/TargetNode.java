package ai.arcblroth.claw.compiler.ast;

import ai.arcblroth.claw.util.ArrayListQueue;

public interface TargetNode {
    ArrayListQueue<FunctionNode> functions();
}
