package com.runerealms.core.feature.command.parser

import com.runerealms.core.feature.command.Commands
import com.runerealms.core.feature.command.struct.*
import com.runerealms.core.feature.command.util.MutableArgumentMap
import com.runerealms.core.feature.command.util.StringReader
import com.runerealms.core.feature.command.parser.CommandParser.ParsingResult
import com.runerealms.core.monad.Either

public open class TextCommandParser(private val feature: Commands): CommandParser {
    override var caseSensitive: Boolean = false

    public override fun parse(text: String): Either<ParsingResult.Failure, CommandCall> {
        val content = text.trim().ifBlank { return Either.left(ParsingResult.Failure) }
        val cached = feature.cache?.get(content)
        if (cached != null)
            return cached

        val split = content.split(' ')
        val root = feature.repository.search(
            name = split.first(),
            ignoreCase = !caseSensitive
        ) ?: return Either.left(ParsingResult.Failure)
        val args = split.drop(1).toMutableList()
        val result = getCommandCall(root, args)

        feature.cache?.put(content, result)
        return result
    }

    private fun getCommandCall(root: Command, arguments: List<String>): Either<ParsingResult.Failure, CommandCall> {
        if (root.children.isEmpty() && root.delegatedArguments.isEmpty() && arguments.isEmpty())
            return Either.right(CommandCall(root, root, mutableMapOf(), arguments))
        else if (root.children.isEmpty() && root.delegatedArguments.isEmpty())
            return Either.left(ParsingResult.Failure)
        val stringReader = StringReader(arguments)

        val result = parseCall(root, stringReader)
        if (result is ParsingResult.Failure)
            return Either.left(ParsingResult.Failure)
        else {
            val success = result as ParsingResult.Success
            return Either.right(CommandCall(root, success.node, success.arguments, arguments))
        }
    }

    private fun parseCall(command: Command, reader: StringReader): ParsingResult {
        if (!reader.hasMore && command.delegatedArguments.filterIsInstance<DelegatedArgument.Required<*>>().isNotEmpty()) {
            return ParsingResult.Failure
        }

        var currentNode: CommandNode = command
        val arguments: MutableArgumentMap = mutableMapOf()
        while (reader.hasMore) {
            val matchingLiteral =
                currentNode.children.firstOrNull { it is CommandLiteralNode && it.name.equals(reader.peek(), !caseSensitive) }

            if (matchingLiteral != null) {
                reader.index++
                currentNode = matchingLiteral
                continue
            }

            var argumentIndex = 0
            while (argumentIndex < command.delegatedArguments.size) {
                val argument = command.delegatedArguments[argumentIndex]
                argumentIndex++

                when {
                    argument is DelegatedArgument.Required<*> && !reader.hasMore -> {
                        return ParsingResult.Failure
                    }
                    argument is DelegatedArgument.Optional<*> && !reader.hasMore -> {
                        continue
                    }
                    argument is DelegatedArgument.Optional<*> && !argument.type.isParseable(reader) -> {
                        if (command.delegatedArguments.size == 1)
                            return ParsingResult.Failure
                        continue
                    }
                }

                if (!argument.type.isParseable(reader)) {
                    return ParsingResult.Failure
                }
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
}