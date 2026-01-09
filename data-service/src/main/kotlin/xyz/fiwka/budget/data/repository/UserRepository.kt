package xyz.fiwka.budget.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import xyz.fiwka.budget.data.entity.User

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
interface UserRepository : JpaRepository<User, Long>, PagingAndSortingRepository<User, Long>
