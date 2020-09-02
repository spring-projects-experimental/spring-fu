package org.springframework.samples.petclinic.visit

import org.springframework.dao.DataRetrievalFailureException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import javax.sql.DataSource

class JdbcVisitRepositoryImpl(dataSource: DataSource) : VisitRepository {

    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val insertVisit = SimpleJdbcInsert(dataSource).withTableName("visits").usingGeneratedKeyColumns("id")

    override fun save(visit: Visit, petId: Int) {
        if (visit.isNew()) {
            visit.copy(id = insertVisit.executeAndReturnKey(createVisitParameterSource(visit).addValue("pet_id", petId)).toInt())
        } else {
            throw UnsupportedOperationException("Visit update not supported")
        }
    }

    override fun findByPetId(petId: Int): Set<Visit> =
            try {
                val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", petId)
                jdbcTemplate.query(
                        "SELECT id as visit_id, visit_date, description FROM visits WHERE pet_id=:id", params)
                { rs, _ ->
                    Visit(
                            id = rs.getInt(1),
                            date = rs.getDate(2).toLocalDate(),
                            description = rs.getString(3))
                }.toSet()
            } catch (ex: EmptyResultDataAccessException) {
                throw DataRetrievalFailureException("Cannot find Visit for petId: $petId")
            }

    private fun createVisitParameterSource(visit: Visit): MapSqlParameterSource =
            MapSqlParameterSource().addValue("id", visit.id)
                    .addValue("visit_date", visit.date)
                    .addValue("description", visit.description)
}