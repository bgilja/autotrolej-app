package com.example.autotrolejapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.autotrolejapp.network.LineResponse

@Entity(tableName = Line.TABLE_NAME)
data class Line(

    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "variant_id")
    var variantId: String = "",

    @ColumnInfo(name = "variant_name")
    var variantName: String = "",

    @ColumnInfo(name = "line_number")
    var lineNumber: String = "",

    @ColumnInfo(name = "direction")
    var direction: String = "",

    @ColumnInfo(name = "variant")
    var variant: String = "",

    @ColumnInfo(name = "area")
    var area: String = "",

    @ColumnInfo(name = "display_order")
    var displayOrder: Int = 0,

) : BaseEntity() {

    constructor(lineResponse: LineResponse) :
            this(
                lineResponse.id,
                lineResponse.lineVariantId,
                lineResponse.name,
                lineResponse.lineNumber,
                lineResponse.direction,
                lineResponse.variant,
                findArea(lineResponse.lineNumber),
                findDisplayOrder(lineResponse.lineNumber)
            )
    companion object {

        const val TABLE_NAME = "lines_table"

        private val LOCAL: Set<String> = listOf(
            "KBC", "1", "1A", "1B", "2",
            "2A", "3", "3A", "4", "4A",
            "5", "5A", "6", "7", "7A", "8", "13").toSet()

        private val NIGHT: Set<String> = listOf("101", "102", "103").toSet()

        private val SUBURBAN: Set<String> = listOf(
            "10", "10A", "11", "12", "12B", "14", "15",
            "17", "18", "18B", "18C", "19", "20", "21",
            "22", "23", "25", "26", "27", "27A", "29",
            "29A", "30", "32", "32A", "34", "35", "36", "37"
        ).toSet()

        fun findArea(lineNumber: String): String {
            if (LOCAL.contains(lineNumber)) return "Local"
            if (NIGHT.contains(lineNumber)) return "Night"
            return "Wide"
        }

        private val LOCAL_ORDER: Map<String, Int> = LOCAL.mapIndexed { index, x -> Pair(x, index) }.toMap()

        private val NIGHT_ORDER: Map<String, Int> = NIGHT.mapIndexed { index, x -> Pair(x, index) }.toMap()

        private val SUBURBAN_ORDER: Map<String, Int> = SUBURBAN.mapIndexed { index, x -> Pair(x, index) }.toMap()

        private fun findDisplayOrder(lineNumber: String): Int {
            val area = findArea(lineNumber)

            if (area == "Local") return LOCAL_ORDER.getOrDefault(lineNumber, 0)
            if (area == "Night") return NIGHT_ORDER.getOrDefault(lineNumber, 0)
            return SUBURBAN_ORDER.getOrDefault(lineNumber, 0)
        }
    }

    fun containsLineNumber(lineNumber: String): Boolean {
        if (this.lineNumber == lineNumber) return true
        return false
    }
}