package com.runerealms.core.feature.command.struct

import com.mojang.brigadier.builder.ArgumentBuilder
import com.runerealms.core.feature.command.struct.types.ArgumentType
import org.bukkit.command.CommandSender
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public abstract class CommandNode(public open val name: String) {
    public val children: MutableList<CommandNode> = mutableListOf()
    private var executor: (MinecraftCommandContext.() -> Unit)? = null

    @PublishedApi
    internal var currentContext: MinecraftCommandContext? = null

    public val isExecutable: Boolean get() = executor != null

    internal val delegatedArguments: MutableList<DelegatedArgument<*>> = mutableListOf()


    public fun executor(executor: MinecraftCommandContext.() -> Unit) {
        this.executor = executor
    }

    public fun literal(vararg names: String, scope: CommandLiteralNode.() -> Unit) {
        for (name in names) {
            children.add(CommandLiteralNode(name).apply(scope))
        }
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

    public fun execute(context: MinecraftCommandContext) {
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
            context.run { infer() }
        }

    public inline operator fun <reified T : Any> DelegatedArgument.Optional<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T?> =
        ReadOnlyProperty { _, _ ->
            val context =
                currentContext ?: error("Tried to delegate argument value while not in a command context")
            context.run { inferOrNull() } ?: return@ReadOnlyProperty null
        }

    public abstract fun brigadierBuilder(): ArgumentBuilder<CommandSender, *>
}

public data class CommandLiteralNode(override val name: String): CommandNode(name) {
    override fun equals(other: Any?): Boolean = other === this

    override fun brigadierBuilder(): ArgumentBuilder<CommandSender, *> =
        com.mojang.brigadier.builder.LiteralArgumentBuilder.literal(name)

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
