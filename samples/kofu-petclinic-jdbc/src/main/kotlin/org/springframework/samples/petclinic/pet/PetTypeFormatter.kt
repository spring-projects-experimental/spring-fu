package org.springframework.samples.petclinic.pet

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.Formatter
import java.text.ParseException
import java.util.*

class PetTypeFormatter(@Autowired val pets: PetRepository) : Formatter<PetType> {
    override fun print(petType: PetType, locale: Locale): String = petType.name

    override fun parse(text: String, locale: Locale): PetType =
            pets.findPetTypes().firstOrNull { text == it.name } ?: throw ParseException("type not found: $text", 0)

}