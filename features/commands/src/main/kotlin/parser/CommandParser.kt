package com.runerealms.core.feature.command.parser

import com.runerealms.core.feature.command.struct.CommandNode
import com.runerealms.core.feature.command.util.ArgumentMap
import com.runerealms.core.monad.Either

public interface CommandParser {
    public var caseSensitive: Boolean

    public fun parse(text: String): Either<ParsingResult.Failure, CommandCall>

    public sealed interface ParsingResult {
        public data class Success(
            public val node: CommandNode,
            public val arguments: ArgumentMap,
        ): ParsingResult

        public data object Failure: ParsingResult
    }
}