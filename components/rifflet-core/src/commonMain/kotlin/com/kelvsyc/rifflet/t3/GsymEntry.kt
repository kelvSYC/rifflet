package com.kelvsyc.rifflet.t3

import okio.ByteString

/**
 * A single entry from a `GSYM` block. The `name` is UTF-8 encoded per the T3 spec.
 * [Unknown] captures entries with unrecognised type codes; the [GSYM entry wire format][GsymEntry]
 * includes an explicit `extraDataLen` field that allows lossless skip.
 */
sealed interface GsymEntry {
    val name: String

    data class Function(
        override val name: String,
        val codeOffset: UInt,
        val argCount: Int,
        val isVarArgs: Boolean,
        val hasReturn: Boolean,
        val optionalArgCount: Int,
    ) : GsymEntry                                                        // type 1

    data class Object(
        override val name: String,
        val objectId: UInt,
        val modifyingObjectId: UInt,
    ) : GsymEntry                                                        // type 2

    data class Property(
        override val name: String,
        val propertyId: UShort,
        val flags: Int,
    ) : GsymEntry                                                        // type 3

    data class IntrinsicFunction(
        override val name: String,
        val functionIndex: Int,
        val functionSetIndex: Int,
        val hasReturn: Boolean,
        val minArgCount: Int,
        val maxArgCount: Int,
        val isVarArgs: Boolean,
    ) : GsymEntry                                                        // type 6

    data class IntrinsicClass(
        override val name: String,
        val metaclassIndex: Int,
        val intrinsicClassObjectId: UInt,
    ) : GsymEntry                                                        // type 9

    data class EnumeratorValue(
        override val name: String,
        val enumeratorId: UInt,
        val flags: Int,
    ) : GsymEntry                                                        // type 10

    data class Unknown(
        override val name: String,
        val typeCode: Int,
        val extraData: ByteString,
    ) : GsymEntry
}
