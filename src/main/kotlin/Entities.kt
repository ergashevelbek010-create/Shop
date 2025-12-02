package dev.pdp

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @CreatedBy var createdBy: Long? = null,
    @LastModifiedBy var lastModifiedBy: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)



@Entity
class Category(
   @Column(nullable = false, unique = true) var name:String,
    var description:String?,
    var orders:Long?
    ) : BaseEntity()


@Entity
class Product(
    @Column(nullable = false, unique = true) var name:String,
    @Column(nullable = false)  var count: Long,
    @ManyToOne var category:Category?,
): BaseEntity()

@Entity
class TransactionItem(
    @ManyToOne var product: Product,
    @Column(nullable = false) var count:Long,
    @Column(nullable = false) var amount:BigDecimal,
    @Column(nullable = false) var total_amount:BigDecimal,
    @ManyToOne var transaction: Transaction
): BaseEntity()

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true) var username:String,
    var fullName:String?,
    @Column(nullable = false, )var balance: BigDecimal = BigDecimal("0.00"),
):BaseEntity()

@Entity
class Transaction(
    @ManyToOne var user: User,
    @Column(nullable = false) var total_amount: BigDecimal,
    @CreationTimestamp var  date: Date
): BaseEntity()

@Entity
class UserPaymentTransaction(
    @ManyToOne var user: User,
    @Column(nullable = false)var amount: BigDecimal,
    @CreationTimestamp var date: Date
): BaseEntity()