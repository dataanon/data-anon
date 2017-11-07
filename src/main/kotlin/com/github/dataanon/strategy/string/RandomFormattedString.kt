package com.github.dataanon.strategy.string

import com.github.dataanon.Field
import com.github.dataanon.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomCharacterSets.UPPERCASE
import com.github.dataanon.utils.RandomUtils

class RandomFormattedString : AnonymizationStrategy<String> {

    override fun anonymize(field: Field<String>, record: Record): String {
        val sb = StringBuilder()
        with(sb){
            field.oldValue.forEach {
                if      ( it.isDigit() )    sb.append(RandomUtils.generateRandomInt(from = 0, to = 9))
                else if ( it in 'a'..'z' )  sb.append(RandomUtils.generateRandomCharacter())
                else if ( it in 'A'..'Z' )  sb.append(RandomUtils.generateRandomCharacter(chars = UPPERCASE))
                else                        sb.append(it)
            }
        }
        return sb.toString()
    }
}