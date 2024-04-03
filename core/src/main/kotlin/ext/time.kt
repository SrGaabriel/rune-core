package com.runerealms.core.ext

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toJavaDuration

public val Int.ticks: Duration
    get() = (this / 20).seconds

public val Long.ticks: Duration
    get() = (this / 20).seconds

public val Double.ticks: Duration
    get() = (this / 20).seconds

public val Short.ticks: Duration
    get() = (this / 20).seconds

public val Duration.inWholeTicks: Long
    get() = this.inWholeSeconds * 20

public val Duration.inWholeTicksInt: Int
    get() = this.inWholeSeconds.toInt() * 20

public val Duration.inTicks: Double
    get() = this.toDouble(DurationUnit.SECONDS) * 20