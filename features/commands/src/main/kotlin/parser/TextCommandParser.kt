package com.runerealms.core.feature.command.parser

import com.mojang.datafixers.util.Either
import com.runerealms.core.feature.command.Commands
import com.runerealms.core.feature.command.repository.CommandRepository
import com.runerealms.core.feature.command.struct.*
import com.runerealms.core.feature.command.util.ArgumentMap
import com.runerealms.core.feature.command.util.MutableArgumentMap
import com.runerealms.core.feature.command.util.StringReader

public open class TextCommandParser(private val repository: CommandRepository) {
    public var caseSensitive: Boolean = false

    public fun parse(text: String): Either<ParsingResult.Failure, CommandCall> {
        val content = text.trim().ifBlank { return Either.left(ParsingResult.Failure) }
//        val cached = cache?.get(content)
//        if (cached != null)
//            return cached

        val split = content.split(" ")
        println(content)
        println(split)
        val root = repository.search(
            name = split.first(),
            ignoreCase = !caseSensitive
        ) ?: return Either.left(ParsingResult.Failure)
        val args = split.drop(1).toMutableList()

        return getCommandCall(root, args)
    }

    protected fun getCommandCall(root: Command, arguments: List<String>): Either<ParsingResult.Failure, CommandCall> {
        if (root.children.isEmpty() && root.delegatedArguments.isEmpty() && arguments.isEmpty())
            return Either.right(CommandCall(root, root, mutableMapOf(), arguments))
        else if (root.children.isEmpty() && root.delegatedArguments.isEmpty())
            return Either.left(ParsingResult.Failure)
        val stringReader = StringReader(arguments)

        val result = scanCallAndStoreArguments(root, stringReader)
        if (result is ParsingResult.Failure)
            return Either.left(ParsingResult.Failure)
        else {
            val success = result as ParsingResult.Success
            return Either.right(CommandCall(root, success.node, success.arguments, arguments))
        }
    }

    protected fun scanCallAndStoreArguments(command: Command, reader: StringReader): ParsingResult {
        if (!reader.hasMore && command.delegatedArguments.filterIsInstance<DelegatedArgument.Required<*>>().isNotEmpty()) {
            return ParsingResult.Failure
        }
        println("kfsda")

        var currentNode: CommandNode = command
        val arguments: MutableArgumentMap = mutableMapOf()
        println("lele")
        while (reader.hasMore) {
            val matchingLiteral =
                currentNode.children.firstOrNull { it is CommandLiteralNode && it.name.equals(reader.peek(), !caseSensitive) }
            println("opa")

            if (matchingLiteral != null) {
                reader.index++
                currentNode = matchingLiteral
                continue
            }

            println("Made it here")
            var argumentIndex = 0
            while (argumentIndex < command.delegatedArguments.size) {
                println("Argument index: $argumentIndex")
                val argument = command.delegatedArguments[argumentIndex]
                println("Argument: $argument")
                argumentIndex++
                println("Argument index: $argumentIndex")

                when {
                    argument is DelegatedArgument.Required<*> && !reader.hasMore -> {
                        println("Required argument not found")
                        return ParsingResult.Failure
                    }
                    argument is DelegatedArgument.Optional<*> && !reader.hasMore -> {
                        println("Optional argument not found")
                        continue
                    }
                    argument is DelegatedArgument.Optional<*> && !argument.type.isParseable(reader) -> {
                        println("Optional argument not parseable")
                        if (command.delegatedArguments.size == 1)
                            return ParsingResult.Failure
                        println("Passed")
                        continue
                    }
                }

                println("Argument type: ${argument.type}")

                if (!argument.type.isParseable(reader)) {
                    return ParsingResult.Failure
                }
                println("Argument type is parseable")
                arguments[argument] = argument.type.parse(reader)
            }

            if (currentNode.children.isEmpty())
                break

//            val argumentNodes = currentNode.children.filterIsInstance<CommandArgumentNode<*>>()
//            if (argumentNodes.isEmpty())
//                continue
//
//            if (argumentNodes.size == 1) {
//                val argumentFound = argumentNodes.first()
//                if (!argumentFound.type.isParseable(reader)) {
//                    throw CommandParsingException.UnexpectedArgumentType(argumentFound, reader.peek())
//                }
//                arguments[argumentFound] = argumentFound.type.parse(reader, dictionary)
//                currentNode = argumentFound
//                continue
//            }
//
//            val validArgumentNode = argumentNodes.asSequence().filter { it.type.isParseable(reader) }.maxByOrNull { it.children.size } ?: return null
//            arguments[validArgumentNode] = validArgumentNode.type.parse(reader, dictionary)
//            currentNode = validArgumentNode
        }
        return ParsingResult.Success(
            node = currentNode,
            arguments = arguments
        )
    }

    public sealed interface ParsingResult {
        public data class Success(
            public val node: CommandNode,
            public val arguments: ArgumentMap,
        ): ParsingResult

        public object Failure: ParsingResult
    }
}