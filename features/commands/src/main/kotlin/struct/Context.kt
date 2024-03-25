package com.runerealms.core.feature.command.struct

import com.runerealms.core.feature.command.util.StandardCommandContext

public abstract class RuneCommandContext {

}

public class WrappingCommandContext(private val context: StandardCommandContext) {

}