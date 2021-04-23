package ai.arcblroth.claw.compiler.pass

import ai.arcblroth.claw.compiler.ast.ClawASTVisitor

/**
 * Marker class denoting a compiler pass.
 */
abstract class CompilerPass : ClawASTVisitor()
