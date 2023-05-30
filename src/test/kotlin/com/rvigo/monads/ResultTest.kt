package com.rvigo.monads

import com.rvigo.monads.Option.Companion.none
import com.rvigo.monads.Option.Companion.some
import com.rvigo.monads.Result.Companion.err
import com.rvigo.monads.Result.Companion.ok
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class ResultTest {
    @Test
    fun testMap() {
        val value = 1
        val ok: Result<Int, RuntimeException> = ok(value)
        val mapped = ok.map {
            it + 1
        }
        val expected: Result<Int, RuntimeException> = Result.Ok(2)

        assertEquals(expected, mapped)

        val errorMessage = "Something went wrong"
        val exception = RuntimeException(errorMessage)
        val err: Result<Int, RuntimeException>  = err(exception)
        val mappedErr= err.map {
            it + 1
        }

        assertEquals(Result.Err(exception), mappedErr)
    }

    @Test
    fun testNestedMap() {
        val ok: Result<Int, RuntimeException> = ok(1)
        val res = ok
            .map { it + 1 }
            .map { it + 1 }
            .map { it * 10 }

        assertEquals(ok(30), res)
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
    fun testIfErr() {val errorMessage = "Something went wrong"
        val exception = RuntimeException(errorMessage)
        val err: Result<Int, RuntimeException> = err(exception)

        val res = err.ifErr {
            assertEquals(it.message, errorMessage)
        }.ifOk {
            // should not be called
            it + 1
        }

        assertEquals(err(exception), res)
    }

    @Test
    fun testGetOrThrow() {
        val value = 1
        val ok = ok(value)
        val res= ok.getOrThrow()

        assertEquals(value, res)

        val exceptionMessage = "Something went wrong"
        val err = err(RuntimeException(exceptionMessage))

        val exception= assertThrows<RuntimeException> { err.getOrThrow()  }
        assertEquals(exceptionMessage, exception.message)
    }

    @Test
    fun testToOption() {
        val value = 1
        val ok: Result<Int, RuntimeException> = ok(value)

        val some: Option<Int> = ok.toOption()

        assertEquals(some(value), some)

        val err: Result<Int, RuntimeException> = err(RuntimeException("Something went wrong"))
        val none: Option<Int> = err.toOption()

        assertEquals(none(),none )
    }
}
