package ai.arcblroth.claw.compiler.exceptions

import java.lang.Exception

/**
 * Base class for all compiler errors.
 */
open class CompilerException(message: String) : Exception(message)

class ParsingErrorException : CompilerException("Parsing failed, aborting compilation!")

/**
 * Base class for all compiler errors that are bugs within the compiler rather than errors
 * arising from user code.
 */
open class InternalCompilerException(message: String) : CompilerException("ICE: $message")

class NoSuchIntrinsicException(intrinsicSignature: String) : InternalCompilerException(
    "The intrinsic function $intrinsicSignature is not defined! Intrinsic functions must be defined both in Claw code and in the compiler."
)
