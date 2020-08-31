package org.springframework.samples.petclinic.pet

import org.springframework.dao.DataRetrievalFailureException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.visit.VisitRepository
import javax.sql.DataSource

class JdbcPetRepositoryImpl(dataSource: DataSource,
                            ownerRepository: OwnerRepository,
                            visitRepository: VisitRepository
) : PetRepository {

    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val insertPet = SimpleJdbcInsert(dataSource).withTableName("pets").usingGeneratedKeyColumns("id")
    private val ownerRepository = ownerRepository
    private val visitRepository = visitRepository

    override fun findPetTypes(): List<PetType> {
        return jdbcTemplate.query("SELECT id, name FROM types ORDER BY name", emptyMap<String, Any>())
        { rs, _ ->
            PetType(
                    id = rs.getInt(1),
                    name = rs.getString(2))
        }
    }

    private fun findByPetTypeId(petTypeId: Int): PetType {
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", petTypeId)
        return jdbcTemplate.query("SELECT id, name FROM types WHERE id=:id", params)
        { rs, _ ->
            PetType(
                    id = rs.getInt(1),
                    name = rs.getString(2))
        }.first()
    }

    override fun findById(petId: Int): Pet =
            try {
                val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", petId)
                jdbcTemplate.query( // `query` used instead of `queryObject` to avoid nullability
                        "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id=:id", params)
                { rs, _ ->
                    Pet(
                            id = rs.getInt(1),
                            name = rs.getString(2),
                            birthDate = rs.getDate(3).toLocalDate(),
                            type = findByPetTypeId(rs.getInt(4)),
                            owner = ownerRepository.findById(rs.getInt(5)))
                }.first()
            } catch (ex: EmptyResultDataAccessException) {
                throw DataRetrievalFailureException("Cannot find pet with id: $petId")
            }

    override fun findByOwnerId(ownerId: Int): Set<Pet> {
        val petTypes: Collection<PetType> = findPetTypes()
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", ownerId)
        return jdbcTemplate.query(
                "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id=:id ORDER BY id", params)
        { rs, _ ->
            val id = rs.getInt(1)
            Pet(
                    id = id,
                    name = rs.getString(2),
                    birthDate = rs.getDate(3).toLocalDate(),
                    type = petTypes.first { it.id == rs.getInt(4) },
                    owner = ownerRepository.findById(rs.getInt(5)),
                    visits = visitRepository.findByPetId(id))
        }.toSet()
    }

    override fun save(pet: Pet) {
        if (pet.isNew()) {
            pet.copy(id = insertPet.executeAndReturnKey(createPetParameterSource(pet)).toInt())
        } else {
            jdbcTemplate
                    .update("UPDATE pets SET name=:name, birth_date=:birth_date, type_id=:type_id, "
                            + "owner_id=:owner_id WHERE id=:id", createPetParameterSource(pet))
        }
    }

    private fun createPetParameterSource(pet: Pet): MapSqlParameterSource =
            MapSqlParameterSource().addValue("id", pet.id)
                    .addValue("name", pet.name)
                    .addValue("birth_date", pet.birthDate)
                    .addValue("type_id", pet.type.id)
                    .addValue("owner_id", pet.owner.id)
}