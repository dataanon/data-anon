package com.github.dataanon.utils

import com.github.dataanon.Matchers
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.FunSpec

class RandomUtilsUnitTest : FunSpec(), Matchers {

    init {
        test("should generate random string with default length") {
            val random = RandomUtils.generateRandomString(chars = RandomCharacterSets.ALPHANUMERIC)
            random.length shouldBe 10
        }

        test("should generate random string with length provided") {
            val random = RandomUtils.generateRandomString(length = 5, chars = "abc12")
            random.length shouldBe 5
        }

        test("should generate random float with default range") {
            val random = RandomUtils.generateRandomFloat()
            random should beIn (0.0f..100.0f)
        }

        test("should generate random float given a range") {
            val random = RandomUtils.generateRandomFloat(from = 100.0f, to = 250.0f)
            random should beIn(100.0f..250.0f)
        }

        test("should throw IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> {
                RandomUtils.generateRandomFloat(from = 10.0f, to = 0.0f)
            }
        }

        test("should generate random int with default range") {
            val random = RandomUtils.generateRandomInt()
            random should beIn (0..100)
        }

        test("should generate random int given a range") {
            val random = RandomUtils.generateRandomInt(from = 100, to = 250)
            random should beIn (100..250)
        }

        test("should throw IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> {
                RandomUtils.generateRandomInt(from = 10, to = 0)
            }
        }

        test("should generate random character using default chars") {
            val random = RandomUtils.generateRandomCharacter()
            random should beIn('a'..'z')
        }

        test("should generate random character given chars") {
            val random = RandomUtils.generateRandomCharacter(chars = "ABCD")
            random should beIn('A'..'D')
        }
    }
}