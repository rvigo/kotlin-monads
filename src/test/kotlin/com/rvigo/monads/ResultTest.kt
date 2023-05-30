package com.rvigo.monads

import com.rvigo.monads.Option.Companion.none
import com.rvigo.monads.Option.Companion.some
import com.rvigo.monads.Result.Companion.err
import com.rvigo.monads.Result.Companion.ok
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class ResultTest {
    @Nested
    inner class Ok {
        @Test
        fun testIsOk() {
            val value = 1

            val ok: Result<Int, RuntimeException> = ok(value)

            assert(ok.isOk())
            assertFalse(ok.isErr())
        }

        @Test
        fun testIfOk() {
            var j = 0
            val value = 1
            val ok: Result<Int, RuntimeException> = ok(value)

            val res = ok.ifOk {
                j = it + 1
            }.ifErr {
                // should not be called
                throw it
            }

            assertEquals(2, j)
            assertEquals(ok(value), res)
        }

        @Test
        fun testMap() {
            val value = 1
            val ok: Result<Int, RuntimeException> = ok(value)

            val mapped = ok.map {
                it + 1
            }

            val expected: Result<Int, RuntimeException> = Result.Ok(2)

            assertEquals(expected, mapped)
        }

        @Test
        fun testFlatMap() {
            val value = 1
            val ok: Result<Int, RuntimeException> = ok(value)

            val mapped: Result<Double, RuntimeException> = ok.flatMap {
                ok(it.toDouble())
            }

            assertEquals(ok(1.0), mapped)
        }

        @Test
        fun testNestedMap() {
            val ok: Result<Int, RuntimeException> = ok(1)

            val res = ok
                // value = 2
                .map { it + 1 }
                // value = 3
                .map { it + 1 }
                // value = 30
                .map { it * 10 }

            assertEquals(ok(30), res)
        }

        @Test
        fun testGetOrThrow() {
            val value = 1
            val ok = ok(value)

            val res = ok.getOrThrow()

            assertEquals(value, res)
        }

        @Test
        fun testToOption() {
            val value = 1
            val ok: Result<Int, RuntimeException> = ok(value)

            val some: Option<Int> = ok.toOption()

            assertEquals(some(value), some)
        }
    }

    @Nested
    inner class Err {
        private val defaultErrorMessage = "Something went wrong"

        @Test
        fun testIsErr() {
            val err: Result<Int, RuntimeException> = err(RuntimeException())

            assert(err.isErr())
            assertFalse(err.isOk())
        }

        @Test
        fun testMap() {
            val exception = RuntimeException(defaultErrorMessage)
            val err: Result<Int, RuntimeException> = err(exception)

            val mappedErr = err.map {
                it + 1
            }

            assertEquals(Result.Err(exception), mappedErr)
        }

        @Test
        fun testFlatMap() {
            val exception = RuntimeException(defaultErrorMessage)
            val err: Result<Int, RuntimeException> = err(exception)

            val mapped: Result<Double, RuntimeException> = err.flatMap {
                ok(it.toDouble())
            }

            assertEquals(err(exception), mapped)
        }

        @Test
        fun testMapErr() {
            data class CustomError(override val message: String?) : RuntimeException(message)

            val runtimeException = RuntimeException(defaultErrorMessage)
            val expectedException = CustomError(defaultErrorMessage)
            val err: Result<Int, RuntimeException> = err(runtimeException)

            val res = err.mapErr { CustomError(it.message) }

            val exception = assertThrows<CustomError> { res.getOrThrow() }
            assertEquals(expectedException, exception)
            assertEquals(exception.message, defaultErrorMessage)
        }

        @Test
        fun testIfErr() {
            val exception = RuntimeException(defaultErrorMessage)
            val err: Result<Int, RuntimeException> = err(exception)

            val res = err.ifErr {
                assertEquals(it.message, defaultErrorMessage)
            }.ifOk {
                // should not be called
                it + 1
            }

            assertEquals(err(exception), res)
        }

        @Test
        fun testGetOrThrow() {
            val err = err(RuntimeException(defaultErrorMessage))

            val exception = assertThrows<RuntimeException> { err.getOrThrow() }
            assertEquals(defaultErrorMessage, exception.message)
        }

        @Test
        fun testToOption() {
            val err: Result<Int, RuntimeException> = err(RuntimeException(defaultErrorMessage))

            val none: Option<Int> = err.toOption()

            assertEquals(none(), none)
        }
    }
}
