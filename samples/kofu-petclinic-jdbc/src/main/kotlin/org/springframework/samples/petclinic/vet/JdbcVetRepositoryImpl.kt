package org.springframework.samples.petclinic.vet

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcVetRepositoryImpl(val jdbcTemplate: JdbcTemplate) : VetRepository {

    override fun findAll(): Collection<Vet> {
        // Retrieve the list of all vets.
        val vets = jdbcTemplate.query("SELECT id, first_name, last_name FROM vets ORDER BY last_name,first_name")
            { rs, _ -> Vet().apply {
                id = rs.getInt(1)
                firstName = rs.getString(2)
                lastName = rs.getString(3)
            }  }

        // Retrieve the list of all possible specialties.
        val specialties = jdbcTemplate.query("SELECT id, name FROM specialties")
            { rs, _ -> Specialty().apply {
                id = rs.getInt(1)
                name = rs.getString(2)
            } }

        // Build each vet's list of specialties.
        for (vet in vets) {
            val vetSpecialtiesIds = jdbcTemplate.query<Int>(
                    "SELECT specialty_id FROM vet_specialties WHERE vet_id=?", RowMapper<Int> { rs, _ -> rs.getInt(1) }, vet.id)
            specialties.filter { it.id in vetSpecialtiesIds }.forEach(vet::addSpecialty)
        }
        return vets
    }
}