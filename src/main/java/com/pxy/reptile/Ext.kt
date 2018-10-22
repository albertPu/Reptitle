package com.pxy.reptile


fun <T> Iterable<T>.isNull(msg: String = "未知错误"): Boolean {
    return if (this is List) {
        size == 0
    } else {
        val iterator = iterator()
        !iterator.hasNext()
    }
}