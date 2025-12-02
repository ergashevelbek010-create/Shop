package dev.pdp

import java.math.BigDecimal
import java.util.*


data class BaseMessage(
    val code: Long? = null,
    val message: String? = null
)

data class UserRequest(
    var name: String?,
    var fullName: String?,
    var balance: BigDecimal?,
)

data class UserResponse(
    var id: Long,
    var name: String,
    var fullName: String?,
    var balance: BigDecimal
)

data class UserUpdateRequest(
    var name: String?,
    var fullName: String?,
    var balance: BigDecimal?,
)

data class UserPaymentTransactionResponse(
    var id: Long,
    var userId: Long,
    var amount: BigDecimal,
    var date: Date,
)

data class TransactionResponse(
    var userId: Long,
    var totalAmount: BigDecimal,
    var date: Date,
)
data class CategoryRequest(
    var name: String,
    var description: String?,
    var order: Long?,
)
data class CategoryResponse(
    var id: Long,
    var name: String,
    var description: String?,
    var order: Long?,
)
data class CategoryUpdateRequest(
    var name: String?,
    var description: String?,
    var order: Long?,
)

data class ProductRequest(
    var name: String,
    var count: Long,
    var categoryId: Long,
)

data class ProductResponse(
    var id: Long,
    var name: String,
    var count: Long,
    var categoryId: Category,
)

data class ProductUpdateRequest(
    var name: String?,
    var count: Long?,
    var categoryId: Long?,
)
