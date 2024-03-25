package com.runerealms.core.feature.command.struct

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.runerealms.core.feature.command.struct.argument.DelegatedArgument
import com.runerealms.core.feature.command.util.StandardCommandContext
import org.bukkit.command.CommandSender
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public abstract class CommandNode(public open val name: String) {
    public val children: MutableList<CommandNode> = mutableListOf()
    private var executor: (StandardCommandContext.() -> Unit)? = null

    @PublishedApi
    internal var currentContext: StandardCommandContext? = null
    public val isExecutable: Boolean get() = executor != null
    public val delegatedArguments: MutableList<DelegatedArgument<*>> = mutableListOf()

    public fun executor(executor: StandardCommandContext.() -> Unit) {
        this.executor = executor
    }

    public fun literal(vararg names: String, scope: CommandLiteralNode.() -> Unit) {
        for (name in names) {
            children.add(CommandLiteralNode(name).apply(scope))
        }
    }

    public fun <T : Any> argument(name: String, type: ArgumentType<T>, scope: CommandArgumentNode<T>.(CommandArgumentNode<T>) -> Unit) {
        children.add(CommandArgumentNode(name, type).apply { scope(this) })
    }

    public fun <T : Any> requiredArgument(name: String, type: ArgumentType<T>): DelegatedArgument.Required<T> {
        val argument = DelegatedArgument.Required(name, type)
        delegatedArguments.add(argument)
        return argument
    }

    public fun <T : Any> optionalArgument(name: String, type: ArgumentType<T>): DelegatedArgument.Optional<T> {
        val argument = DelegatedArgument.Optional(name, type)
        delegatedArguments.add(argument)
        return argument
    }

    public fun selector(options: List<String>, scope: CommandLiteralNode.(String) -> Unit) {
        options.forEach { option ->
            literal(option) {
                scope(option)
            }
        }
    }

//    public fun suggestions(vararg suggestions: String) {
//        for (suggestion in suggestions) {
//            literal(suggestion) {
//                executor {
//                    execute(StandardCommandContext(
//                        source,
//                        input,
//                        arguments,
//                        command,
//                        rootNode,
//                        nodes,
//                        range,
//                        child,
//                        redirectModifier,
//                        isForked
//                    ))
//                }
//            }
//        }
//    }

    public fun execute(context: StandardCommandContext) {
        var caughtException: Throwable? = null

        try {
            currentContext = context
            executor?.let { it(context) }
            currentContext = null
        } catch (throwable: Throwable) {
            caughtException = throwable
        }
        if (caughtException != null)
            throw caughtException
    }

    public inline operator fun <reified T : Any> DelegatedArgument.Required<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> =
        ReadOnlyProperty { _, _ ->
            val context =
                currentContext ?: error("Tried to delegate argument value while not in a command context")
            context.getArgument(name, T::class.java)
        }

    public inline operator fun <reified T : Any> DelegatedArgument.Optional<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T?> =
        ReadOnlyProperty { _, _ ->
            val context =
                currentContext ?: error("Tried to delegate argument value while not in a command context")
            runCatching { context.getArgument(name, T::class.java) }.getOrNull() ?: return@ReadOnlyProperty null
        }

    public abstract fun brigadierBuilder(): ArgumentBuilder<CommandSender, *>
}