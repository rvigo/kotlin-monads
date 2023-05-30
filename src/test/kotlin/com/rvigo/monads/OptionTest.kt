package com.rvigo.monads

import com.rvigo.monads.Option.Companion.none
import com.rvigo.monads.Option.Companion.some
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class OptionTest {
    @Nested
    inner class Some {
        @Test
        fun testIsSome() {
            val value = 1
            val option: Option<Int> = some(value)

            assert(option.isSome())
            assertFalse(option.isNone())
        }

        @Test
        fun testGetOrThrow() {
            val value = 1
            val some: Option<Int> = some(value)

            assertEquals(value, some.getOrThrow())
        }

        @Test
        fun testGetOrThrowCustomException() {
            val value = 1
            val some: Option<Int> = some(value)

            assertEquals(value, some.getOrThrow())
        }

        @Test
        fun testGetOrNull() {
            val some: Option<Int> = some(1)

            assertNotNull(some.getOrNull())
        }

        @Test
        fun testMap() {
            val value = 1
            val option: Option<Int> = some(value)
            val mapped: Option<Int> = option.map { it + 1 }

            assertEquals(some(value + 1), mapped)
        }


        @Test
        fun testNestedMap() {
            val value = 1
            val option: Option<Int> = some(value)
            val mapped: Option<Int> = option
                .map { it + 1 }
                .map { it + 1 }
                .map { it * 10 }

            assertEquals(some(30), mapped)
        }

        @Test
        fun testFlatMap() {
            val value = 1
            val option: Option<Int> = some(value)
            val mapped: Option<Double> = option.flatMap {
                some(it.toDouble())
            }

            assertEquals(some(1.0), mapped)
        }
    }

    @Nested
    inner class None {
        @Test
        fun testIsNone() {
            val option: Option<Int> = none()

            assert(option.isNone())
            assertFalse(option.isSome())
        }

        @Test
        fun testGetOrThrow() {
            val none: Option<Int> = none()
            val ex = assertThrows<NoSuchElementException> { none.getOrThrow() }

            assertEquals("Option is None", ex.message)
        }

        @Test
        fun testGetOrThrowCustomException() {
            val none: Option<Int> = none()
            val errorMessage = "Something went wrong"
            val ex = assertThrows<RuntimeException> {
                none.getOrThrow {
                    RuntimeException(errorMessage)
                }
            }

            assertEquals(errorMessage, ex.message)
        }

        @Test
        fun testGetOrElse() {
            val none: Option<Int> = none()
            val expected = 1

            assertEquals(expected, none.getOrElse { 1 })
        }

        @Test
        fun testGetOrNull() {
            val none: Option<Int> = none()

            assertNull(none.getOrNull())
        }

        @Test
        fun testMap() {
            val none: Option<Int> = none()

            assertEquals(none(), none.map { it + 1 })
        }

        @Test
        fun testFlatMap() {
            val none: Option<Int> = none()

            val mapped = none.flatMap { some(it.toDouble()) }

            assertEquals(none(), mapped)
        }
    }
}
