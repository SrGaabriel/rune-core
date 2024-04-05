package com.runerealms.core.feature.command.struct.types

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.runerealms.core.feature.command.util.StringReader
import com.mojang.brigadier.arguments.ArgumentType as BrigadierArgumentType

public abstract class ArgumentType<T>(
    public val name: String,
    public val greedy: Boolean = false
) {

    public abstract fun parse(reader: StringReader): T

    public abstract fun isParseable(reader: StringReader): Boolean

    public abstract fun brigadier(): BrigadierArgumentType<T>

    public object Integer: ArgumentType<Int>("32 bits integer") {
        private val regex = Regex("^\\d+\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader): Int =
            reader.next().toInt()

        override fun brigadier(): BrigadierArgumentType<Int> = IntegerArgumentType.integer()
    }

    public object Double: ArgumentType<kotlin.Double>("64 bits floating point") {
        private val regex = Regex("^\\d+(\\.\\d+|)\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader): kotlin.Double =
            reader.next().toDouble()

        override fun brigadier(): BrigadierArgumentType<kotlin.Double> = DoubleArgumentType.doubleArg()
    }

    public object Long: ArgumentType<kotlin.Long>("64 bits integer") {
        private val regex = Regex("^\\d+\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader): kotlin.Long =
            reader.next().toLong()

        override fun brigadier(): BrigadierArgumentType<kotlin.Long> = LongArgumentType.longArg()
    }

    public object Float: ArgumentType<kotlin.Float>("32 bits floating point") {
        private val regex = Regex("^\\d+(\\.\\d+|)\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader): kotlin.Float =
            reader.next().toFloat()

        override fun brigadier(): BrigadierArgumentType<kotlin.Float> = FloatArgumentType.floatArg()
    }

    public sealed class Text(name: String, greedy: Boolean = false): ArgumentType<String>(name, greedy) {
        public data object Word : Text("Word") {
            override fun isParseable(reader: StringReader): Boolean = !reader.isEnd()

            override fun parse(reader: StringReader): String = reader.next()

            override fun brigadier(): BrigadierArgumentType<String> = StringArgumentType.word()
        }

        public data object Quote : Text("Quote", greedy = true) {
            private val regex = Regex("^\".+\"(?!\\S)(.+|)")

            override fun isParseable(reader: StringReader): Boolean =
                reader.peekRemaining() matches regex

            override fun parse(reader: StringReader): String {
                return reader.readUntilInclusive { it.endsWith('"') }.drop(1).dropLast(1)
            }

            override fun brigadier(): BrigadierArgumentType<String> = StringArgumentType.string()
        }

        public data object Greedy : Text("Text", greedy = true) {
            override fun isParseable(reader: StringReader): Boolean = !reader.isEnd()

            override fun parse(reader: StringReader): String = reader.remaining()

            override fun brigadier(): BrigadierArgumentType<String> = StringArgumentType.greedyString()
        }
    }
}