package com.rvigo.monads

import com.rvigo.monads.Option.Companion.none
import com.rvigo.monads.Option.Companion.some
import com.rvigo.monads.Result.Companion.err
import com.rvigo.monads.Result.Err
import com.rvigo.monads.Result.Ok

/**
 * Based on Rust Result Enum
 * @see <a href=https://doc.rust-lang.org/std/result/enum.Result.html> Rust Result Documentation</a>
 */
sealed class Result<out T, out E : Throwable> {
    companion object {
        fun <T> ok(value: T) = Ok(value)
        fun <E : Throwable> err(error: E) = Err(error)
    }

    data class Ok<T>(val value: T) : Result<T, Nothing>()
    data class Err<E : Throwable>(val throwable: E) : Result<Nothing, E>()
}

fun <T, E : Throwable> Result<T, E>.isOk() = when (this) {
    is Ok -> true
    is Err -> false
}

fun <T, E : Throwable> Result<T, E>.isErr() = when (this) {
    is Ok -> false
    is Err -> true
}

inline fun <T, E : Throwable, U> Result<T, E>.map(f: (T) -> U) = when (this) {
    is Ok -> Ok(f(value))
    is Err -> this
}

inline fun <T, E : Throwable, U> Result<T, E>.flatMap(f: (T) -> Result<U, E>) = when (this) {
    is Ok -> f(value)
    is Err -> this
}

inline fun <T, E : Throwable, F : Throwable> Result<T, E>.mapErr(f: (E) -> F) = when (this) {
    is Ok -> this
    is Err -> err(f(throwable))
}

inline fun <T, E : Throwable, R> Result<T, E>.ifOk(f: (T) -> R) = when (this) {
    is Ok -> {
        f(value)
        this
    }

    is Err -> this
}

inline fun <T, E : Throwable, R> Result<T, E>.ifErr(f: (E) -> R) = when (this) {
    is Err -> {
        f(throwable)
        this
    }

    is Ok -> this
}

fun <T, E : Throwable> Result<T, E>.getOrThrow() = when (this) {
    is Ok -> value
    is Err -> throw throwable
}

fun <T, E : Throwable> Result<T, E>.toOption() = when (this) {
    is Ok -> some(value)
    is Err -> none()
}
