package com.runerealms.core.feature.command.repository

import com.runerealms.core.feature.command.struct.Command

public interface CommandRepository {
    public fun register(command: Command)

    public fun search(name: String, ignoreCase: Boolean = true): Command?

    public fun exclude(command: Command)
}