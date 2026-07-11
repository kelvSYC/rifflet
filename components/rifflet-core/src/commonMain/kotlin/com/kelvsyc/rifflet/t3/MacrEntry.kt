package com.kelvsyc.rifflet.t3

data class MacrEntry(
    val name: String,
    val isFunctionLike: Boolean,
    val isVarArgs: Boolean,
    val params: List<MacrParam>,
    val expansion: String,
)
