package org.springframework.samples.petclinic.owner

import org.springframework.dao.DataRetrievalFailureException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import javax.sql.DataSource

class JdbcOwnerRepositoryImpl(val dataSource: DataSource) : OwnerRepository {

    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val insertOwner = SimpleJdbcInsert(dataSource).withTableName("owners").usingGeneratedKeyColumns("id")

    override fun findByLastName(lastName: String): Collection<Owner> {
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("lastName", "${lastName}%")
        return jdbcTemplate.query("SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name like :lastName", params) { rs, _ ->
            Owner(
                    id = rs.getInt(1),
                    firstName = rs.getString(2),
                    lastName = rs.getString(3),
                    address = rs.getString(4),
                    city = rs.getString(5),
                    telephone = rs.getString(6))
        }
    }

    override fun findById(ownerId: Int): Owner {
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", ownerId)
        val owner: Owner

        try {
            owner = jdbcTemplate.query(
                    "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id= :id", params)
            { rs, _ ->
                Owner(
                        id = rs.getInt(1),
                        firstName = rs.getString(2),
                        lastName = rs.getString(3),
                        address = rs.getString(4),
                        city = rs.getString(5),
                        telephone = rs.getString(6))
            }.first()

        } catch (ex: EmptyResultDataAccessException) {
            throw DataRetrievalFailureException("Cannot find Owner: $ownerId")
        }

        return owner
    }

    override fun save(owner: Owner): Owner {
        val parameterSource = BeanPropertySqlParameterSource(owner) // TODO wtf is that?
        return if (owner.isNew()) {
            owner.copy(id = insertOwner.executeAndReturnKey(parameterSource).toInt())
        } else {
            this.jdbcTemplate
                    .update("UPDATE owners SET first_name=:firstName, last_name=:lastName, address=:address, "
                            + "city=:city, telephone=:telephone WHERE id=:id", parameterSource)
            owner
        }
    }
}