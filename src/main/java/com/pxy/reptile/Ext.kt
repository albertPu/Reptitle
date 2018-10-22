package com.pxy.reptile


fun <T> Iterable<T>.isNull(msg: String = "未知错误"): Boolean {
    when (this) {
        is List -> {
            when (size) {
                0 -> true
                else -> false
            }
        }
        else -> {
            val iterator = iterator()
            if (!iterator.hasNext())
                true
            if (iterator.hasNext())
                false
        }
    }
    return false
}