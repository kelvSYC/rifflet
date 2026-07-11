package com.kelvsyc.rifflet.t3

/**
 * A T3 DATA_HOLDER value: a 5-byte spec-level variant type (1-byte type tag + 4-byte value).
 * Used in [SymdEntry] to represent a symbol's runtime value.
 */
sealed interface T3DataHolder {
    data object Nil : T3DataHolder                                      // type 1
    data object True : T3DataHolder                                     // type 2
    data object Empty : T3DataHolder                                    // type 13
    data class ObjectRef(val objectId: UInt) : T3DataHolder             // type 5, UINT4
    data class PropertyRef(val propertyId: UShort) : T3DataHolder       // type 6, UINT2 in low 2 bytes
    data class IntValue(val value: Int) : T3DataHolder                  // type 7, INT4
    data class SingleQuotedStringRef(val offset: UInt) : T3DataHolder   // type 8, UINT4
    data class DoubleQuotedStringRef(val offset: UInt) : T3DataHolder   // type 9, UINT4
    data class ListRef(val offset: UInt) : T3DataHolder                 // type 10, UINT4
    data class CodeOffset(val offset: UInt) : T3DataHolder              // type 11, UINT4
    data class FuncPtr(val offset: UInt) : T3DataHolder                 // type 12, UINT4
    data class EnumValue(val value: UInt) : T3DataHolder                // type 15, UINT4
    data class BuiltinFuncPtr(val value: UInt) : T3DataHolder           // type 16, UINT4
}
