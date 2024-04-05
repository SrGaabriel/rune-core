package com.runerealms.core.feature

import com.runerealms.core.RunePlugin
import com.runerealms.core.lifecycle.ILifecycle

public open class RuneFeatureInstance(
    public val plugin: RunePlugin
): ILifecycle {
    public open fun install() {}

    public open fun uninstall() {}
}