package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
enum class EggType {
    GRADE_A_LARGE,
    GRADE_A_EXTRA_LARGE,
    GRADE_A_JUMBO,
    GRADE_A_MEDIUM,
    GRADE_A_SMALL,
    ORGANIC,
    CAGE_FREE,
    FREE_RANGE,
    PASTURE_RAISED,
    EGG_WHITE,
    EGG_YOLK,
    EGG_SUBSTITUTE,
    LIQUID_EGG,
    POWDERED_EGG,
    UNKNOWN
}
