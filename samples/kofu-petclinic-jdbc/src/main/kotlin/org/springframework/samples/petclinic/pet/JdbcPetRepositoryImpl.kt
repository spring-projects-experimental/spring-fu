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
            PetType().apply {
                id = rs.getInt(1)
                name = rs.getString(2)
            }
        }
    }

    private fun findByPetTypeId(petTypeId: Int): PetType {
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", petTypeId)
        return jdbcTemplate.query("SELECT id, name FROM types WHERE id=:id", params)
        { rs, _ ->
            PetType().apply {
                id = rs.getInt(1)
                name = rs.getString(2)
            }
        }
                .first()
    }

    override fun findById(petId: Int): Pet {
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", petId)
        var pet: Pet

        try {
            pet = jdbcTemplate.query( // `query` used instead of `queryObject` to avoid nullability
                    "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id=:id", params)
            { rs, _ ->
                Pet().apply {
                    id = rs.getInt(1)
                    name = rs.getString(2)
                    birthDate = rs.getDate(3).toLocalDate()
                    typeId = rs.getInt(4)
                    ownerId = rs.getInt(5)
                }

            }.first()

        } catch (ex: EmptyResultDataAccessException) {
            throw DataRetrievalFailureException("Cannot find pet with id: $petId")
        }

        pet.owner = ownerRepository.findById(pet.ownerId)
        pet.type = findByPetTypeId(pet.typeId)
        return pet
    }

    override fun findByOwnerId(ownerId: Int): Set<Pet> {
        val params: MapSqlParameterSource = MapSqlParameterSource().addValue("id", ownerId)
        val pets = jdbcTemplate.query(
                "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id=:id ORDER BY id", params)
        { rs, _ ->
            Pet().apply {
                id = rs.getInt(1)
                name = rs.getString(2)
                birthDate = rs.getDate(3).toLocalDate()
                owner = null
                type = PetType()
                typeId = rs.getInt(4)
                this.ownerId = rs.getInt(5)
            }
        }
                .toSet()

        val petTypes: Collection<PetType> = findPetTypes()

        for (pet in pets) {
            pet.type = petTypes.first { it.id == pet.typeId }
            pet.visits = visitRepository.findByPetId(pet.id)
        }

        return pets
    }

    override fun save(pet: Pet) {
        if (pet.isNew()) {
            pet.id = insertPet.executeAndReturnKey(createPetParameterSource(pet)).toInt()
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
                    .addValue("type_id", pet.typeId)
                    .addValue("owner_id", pet.ownerId)
}