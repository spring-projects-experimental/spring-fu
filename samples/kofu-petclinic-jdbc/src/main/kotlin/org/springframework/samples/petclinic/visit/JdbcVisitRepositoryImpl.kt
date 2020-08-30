package org.springframework.samples.petclinic.visit

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import javax.sql.DataSource

class JdbcVisitRepositoryImpl(val dataSource: DataSource) : VisitRepository {

    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val insertVisit = SimpleJdbcInsert(dataSource).withTableName("visits").usingGeneratedKeyColumns("id")

    override fun save(visit: Visit) {
        if (visit.isNew()) {
            visit.id = insertVisit.executeAndReturnKey(createVisitParameterSource(visit)).toInt()
        } else {
            throw UnsupportedOperationException("Visit update not supported")
        }
    }

    override fun findByPetId(petId: Int): Set<Visit> {
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", petId)

        // TODO add a try catch for EmptyResultDataAccessException?
        return jdbcTemplate.query(
                "SELECT id as visit_id, visit_date, description FROM visits WHERE pet_id=:id", params)
        { rs, _ ->
            Visit().apply {
                id = rs.getInt(1)
                date = rs.getDate(2).toLocalDate()
                description = rs.getString(3)
                this.petId = petId
            }
        }.toSet()
    }

    private fun createVisitParameterSource(visit: Visit): MapSqlParameterSource =
            MapSqlParameterSource().addValue("id", visit.id)
                    .addValue("visit_date", visit.date)
                    .addValue("description", visit.description)
                    .addValue("pet_id", visit.petId)
}