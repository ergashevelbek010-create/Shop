package dev.pdp

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeletedForPageable(pageable: Pageable): Page<T>
    fun saveAndRefresh(t: T): T
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findAllNotDeletedForPageable(pageable: Pageable): Page<T> =
        findAll(isNotDeletedSpecification, pageable)

    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }

    @Transactional
    override fun saveAndRefresh(t: T): T {
        return save(t).apply { entityManager.refresh(this) }
    }

}

interface UserRepository : BaseRepository<User> {
    @Query("""
        select u from User u where u.username = :username 
    """)
    fun checkByUsername(username: String): User?
}

interface CategoryRepository : BaseRepository<Category> {
    @Query("""
        select c from Category c where c.name = :name and c.deleted = false 
    """)
    fun checkName(name: String): Category?
}

interface ProductRepository : BaseRepository<Product> {
    @Query("""
        select p from Product p where p.name = :name and p.deleted = false
    """)
    fun checkName(name: String): Product?
}

interface TransactionRepository : BaseRepository<Transaction> {
    @Query("""
        select t from Transaction t where t.user.id = :userId and t.deleted = false 
    """)
    fun checkUserId(userId: Long): List<Transaction>
}

interface TransactionItemRepository : BaseRepository<TransactionItem> {
    @Query("""
        select t from TransactionItem t where t.transaction.id = :id and t.deleted = false 
    """)
    fun transactionItems(id: Long): List<TransactionItem>
}

interface UserPaymentTransactionRepository : BaseRepository<UserPaymentTransaction> {
   @Query("""
       select p from UserPaymentTransaction p where p.user.id = :userId and p.deleted = false 
   """)
   fun userAllPaymentTransactions(userId: Long): List<UserPaymentTransaction>
}










