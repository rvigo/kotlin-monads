package com.rvigo.monads

import com.rvigo.monads.Option.Companion.none
import com.rvigo.monads.Option.Companion.some
import com.rvigo.monads.Option.None
import com.rvigo.monads.Option.Some

/**
 * Based on Rust Option Enum
 * @see <a href=https://doc.rust-lang.org/std/option/enum.Option.html> Rust Option Documentation</a>
 */
sealed class Option<out T> {
    companion object {
        fun <T> some(value: T) = Some(value)
        fun none() = None
    }

    data class Some<T>(val value: T) : Option<T>()
    object None : Option<Nothing>()
}

fun <T> Option<T>.isSome() = when (this) {
    is Some -> true
    is None -> false
}

fun <T> Option<T>.isNone() = when (this) {
    is None -> true
    is Some -> false
}

inline fun <T, R> Option<T>.map(f: (T) -> R) = when (this) {
    is Some -> some(f(value))
    is None -> none()
}

inline fun <T,  U> Option<T>.flatMap(f: (T)-> Option<U>) = when (this) {
    is Some -> f(value)
    is None -> none()
}

fun <T> Option<T>.getOrThrow() = when (this) {
    is Some -> value
    is None -> throw NoSuchElementException("Option is None")
}

inline fun <T, E : Throwable> Option<T>.getOrThrow(exception: () -> E) = when (this) {
    is Some -> value
    is None -> throw exception()
}

inline fun <T> Option<T>.getOrElse(f: () -> T) = when (this) {
    is Some -> value
    is None -> f()
}

fun <T> Option<T>.getOrNull() = when (this) {
    is Some -> value
    is None -> null
}
