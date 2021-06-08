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
            )

    companion object {

        const val TABLE_NAME = "lines_table"

        private val LOCAL: Set<String> = listOf(
            "KBC", "1", "1A", "1B", "2",
            "2A", "3", "3A", "4", "4A",
            "5", "5A", "6", "7", "7A", "8", "13").toSet()

        private val NIGHT: Set<String> = listOf("101", "102", "103").toSet()

        fun findArea(lineNumber: String): String {
            if (LOCAL.contains(lineNumber)) return "Local"
            if (NIGHT.contains(lineNumber)) return "Night"
            return "Wide"
        }
    }
}