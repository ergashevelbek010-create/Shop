package dev.pdp

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.Date

interface CategoryService {
    fun create(categoryRequest: CategoryRequest)
    fun update(id: Long, categoryUpdateRequest: CategoryUpdateRequest)
    fun delete(id: Long)
    fun getOne(id: Long): Category?
    fun getAll(): List<Category>
}

@Service
class CategoryServiceImpl(
    private val categoryMapper: CategoryMapper,
    private val categoryRepository: CategoryRepository
) : CategoryService {
    @Transactional
    override fun create(categoryRequest: CategoryRequest) {
        categoryRepository.checkName(categoryRequest.name)?.let {
            throw CategoryAlreadyExistsException()
        } ?: categoryRepository.save(categoryMapper.toEntity(categoryRequest))
    }

    @Transactional
    override fun update(id: Long, categoryUpdateRequest: CategoryUpdateRequest) {
        var newCategory = categoryRepository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFound()
        categoryUpdateRequest.run {
            name?.let { categoryName ->
                categoryRepository.checkName(categoryName)?.let {
                    throw CategoryAlreadyExistsException()
                }
                newCategory.name = categoryName
            }
            description?.let { newCategory.description = it }
            order?.let { newCategory.orders = it }
        }
        categoryRepository.save(newCategory)
    }

    override fun delete(id: Long) {
        categoryRepository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFound()
        categoryRepository.trash(id)
    }

    override fun getOne(id: Long): Category? =
        categoryRepository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFound()


    override fun getAll(): List<Category> {
        val categoryList = categoryRepository.findAllNotDeleted()
        if (categoryList.isEmpty()) throw CategoryNotFound()
        return categoryList
    }

}

interface ProductService {
    fun create(productRequest: ProductRequest)
    fun update(id: Long, productUpdateRequest: ProductUpdateRequest)
    fun delete(id: Long)
    fun getOne(id: Long): Product?
    fun getAll(): List<ProductResponse>
}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
    private val categoryRepository: CategoryRepository
) : ProductService {

    @Transactional
    override fun create(productRequest: ProductRequest) {
        var category = categoryRepository.findByIdAndDeletedFalse(productRequest.categoryId) ?: throw CategoryNotFound()
        productRepository.checkName(productRequest.name)?.let { throw ProductAlreadyExistsException() }
        productRepository.save(productMapper.toEntity(productRequest, category))
    }

    @Transactional
    override fun update(id: Long, productUpdateRequest: ProductUpdateRequest) {
        var product = productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFound()
        productUpdateRequest.run {
            name?.let { productName ->
                productRepository.checkName(productName)?.let { throw ProductAlreadyExistsException() }
                    ?: run { product.name = productName }
            }
            count?.let { product.count = it }
            categoryId?.let {
                var category = categoryRepository.findByIdAndDeletedFalse(it) ?: throw CategoryNotFound()
                product.category = category
            }
        }
    }

    override fun delete(id: Long) {
        productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFound()
        productRepository.trash(id)
    }

    override fun getOne(id: Long): Product? {
        return productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFound()
    }

    override fun getAll(): List<ProductResponse> {
        val productList = productRepository.findAllNotDeleted().map { product ->
            var category =
                categoryRepository.findByIdAndDeletedFalse(product.category?.id!!) ?: throw CategoryNotFound()
            productMapper.fromEntity(product, category)
        }
        if (productList.isEmpty()) throw ProductNotFound()
        return productList
    }

}

interface UsersService {
    fun create(userRequest: UserRequest)
    fun update(id: Long, userUpdateRequest: UserUpdateRequest)
    fun delete(id: Long)
    fun getOne(id: Long): User?
    fun getAll(): List<UserResponse>
}


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) : UsersService {
    @Transactional
    override fun create(userRequest: UserRequest) {
        userRepository.checkByUsername(userRequest.name!!)?.let {  throw UserNameAlreadyExistsException()}
        userRepository.save(userMapper.toEntity(userRequest))
    }

    @Transactional
    override fun update(id: Long, userUpdateRequest: UserUpdateRequest) {
        var newUser = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFound()
        userUpdateRequest.run {
            name?.let { userName ->
                userRepository.checkByUsername(userName)?.let {
                    throw UserNameAlreadyExistsException()
                } ?: run { newUser.username = userName }
            }
            fullName?.let { newUser.fullName = it }
            balance?.let { newUser.balance = it }
        }
        userRepository.save(newUser)
    }

    override fun delete(id: Long) {
        userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFound()
        userRepository.trash(id)
    }

    override fun getOne(id: Long): User? {
        return userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFound()
    }

    @Transactional
    override fun getAll(): List<UserResponse> {
        return userRepository.findAllNotDeleted().map { userMapper.fromEntity(it) }
    }

}


interface TransactionService {
    fun deposit(id: Long, amount: BigDecimal)
    fun userPaymentTransactionHistory(id: Long): List<UserPaymentTransaction>
    fun userTransactionItem(id: Long): List<TransactionItem>
    fun userTransaction(id: Long): List<Transaction>
    fun adminAllTransactionHistory(): List<UserPaymentTransaction>
    fun userBuyProduct(userId: Long, productsId: List<Long>)
}

@Service
class TransactionServiceImpl(
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val userPaymentTransactionRepository: UserPaymentTransactionRepository
) : TransactionService {
    override fun deposit(id: Long, amount: BigDecimal) {
        var newUser = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFound()
        amount?.let { newUser.balance = newUser.balance.plus(amount) }
        val userPaymentTransaction = UserPaymentTransaction(newUser, amount, Date())
        userPaymentTransactionRepository.save(userPaymentTransaction)
        userPaymentTransactionRepository
        userRepository.save(newUser)
    }

    override fun userPaymentTransactionHistory(id: Long): List<UserPaymentTransaction> {
        return userPaymentTransactionRepository.userAllPaymentTransactions(id)
    }

    @Transactional
    override fun userTransactionItem(id: Long): List<TransactionItem> {
        val transactionList = transactionRepository.checkUserId(id)
        var resultTransactionItem: MutableList<TransactionItem> = mutableListOf()
        if (transactionList.isEmpty()) throw TransactionItemNotFound()
        transactionList.forEach { transactionItem ->
            val transaction =
                transactionItemRepository.transactionItems(transactionItem!!.id!!)
            println(transaction.toString())
            transaction.forEach {it->
                resultTransactionItem.add(it)
            }
        }
        return resultTransactionItem
    }

    override fun userTransaction(id: Long): List<Transaction> {
        val transactionList = transactionRepository.checkUserId(id)
        if (transactionList.isNotEmpty()) throw TransactionAlreadyExists()
        return transactionList
    }

    override fun adminAllTransactionHistory(): List<UserPaymentTransaction> {
        var payment = userPaymentTransactionRepository.findAllNotDeleted()
        if (payment.isEmpty()) throw TransactionNotFound()
        return payment
    }

    @Transactional
    override fun userBuyProduct(userId: Long, productsId: List<Long>) {
        var user = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFound()
        var totalAmount = BigDecimal("0.00")
        var transaction = Transaction(user, totalAmount, Date())
        val group = productsId.groupBy { it }
        group.forEach { it ->
            val product = productRepository.findByIdAndDeletedFalse(it.key) ?: throw ProductNotFound()
            totalAmount.plus(product.count.toBigDecimal().multiply(it.value.count().toBigDecimal()))
            group.forEach { it ->
                val product = productRepository.findByIdAndDeletedFalse(it.key) ?: throw ProductNotFound()
                var transactionItem = TransactionItem(
                    product, it.value.count().toLong(), product.count.toBigDecimal(),
                    product.count.toBigDecimal().multiply(it.value.count().toBigDecimal()), transaction
                )
                transactionItemRepository.save(transactionItem)
            }
            transaction.total_amount = totalAmount
            transactionRepository.save(transaction)
        }
    }
}




