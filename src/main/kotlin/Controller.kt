package dev.pdp

import org.springframework.web.bind.annotation.*
import java.math.BigDecimal




@RestController
@RequestMapping("/api/category")
class CategoryController(
    private val categoryService: CategoryService
){
    @PostMapping("/create")
    fun create(@RequestBody categoryRequest: CategoryRequest) {
      categoryService.create(categoryRequest)
    }

    @GetMapping("/get/one/{id}")
    fun getOne(@PathVariable id: Long): Category? {
       return categoryService.getOne(id)
    }

    @GetMapping("/categories")
    fun getCategories(): List<Category> {
     return categoryService.getAll()
    }

    @DeleteMapping("/delete/{id}")
    fun deleteOne(@PathVariable id: Long) {
        categoryService.delete(id)
    }

    @PutMapping("/update/{id}")
    fun update(@PathVariable id: Long,@RequestBody categoryUpdateRequest: CategoryUpdateRequest) {
        categoryService.update(id, categoryUpdateRequest)
    }
}

@RestController
@RequestMapping("/api/product")
class ProductController(
    private val productService: ProductService
){
    @PostMapping("/create")
    fun create(@RequestBody productRequest: ProductRequest) {
        productService.create(productRequest)
    }

    @PutMapping("/update/{id}")
    fun update(@PathVariable id: Long,@RequestBody productUpdateRequest: ProductUpdateRequest) {
        productService.update(id, productUpdateRequest)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteOne(@PathVariable id: Long) {
        productService.delete(id)
    }

    @GetMapping("/get/one/{id}")
    fun getOne(@PathVariable id: Long): Product? {
      return  productService.getOne(id)
    }

    @GetMapping("/products")
    fun getProducts(): List<ProductResponse> {
        return productService.getAll()
    }
}

@RestController
@RequestMapping("/api/transaction")
class TransactionController(
    private val transactionService: TransactionService
){
    @PostMapping("/deposit/{id}/{amount}")
    fun deposit(@PathVariable id: Long, @PathVariable amount: BigDecimal) {
        transactionService.deposit(id, amount)
    }

    @GetMapping("/item/{id}")
    fun userTransactions(@PathVariable id: Long): List<TransactionItem> {
       return transactionService.userTransactionItem(id)
    }

    @GetMapping("/transactions/{id}")
    fun getTransactions(@PathVariable id: Long): List<UserPaymentTransaction> {
        return transactionService.userPaymentTransactionHistory(id)
    }

    @GetMapping("/transactions")
    fun getTransactions(): List<UserPaymentTransaction> {
        return transactionService.adminAllTransactionHistory()
    }

    @GetMapping("/user/transactions/{id}")
    fun getUserTransactions(@PathVariable id: Long): List<Transaction> {
        return transactionService.userTransaction(id)
    }

    @PostMapping("/user/{id}")
    fun userByProducts(
        @PathVariable id: Long,
        @RequestParam list: List<Long>) {
     return transactionService.userBuyProduct(id,list)
    }


}


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UsersService
) {
    @GetMapping("/users")
    fun getUsers(): List<UserResponse> {
        return userService.getAll()
    }

    @GetMapping("/user/{id}")
    fun getUser(@PathVariable id: Long): User? {
        return userService.getOne(id)
    }

    @PostMapping("/create")
    fun create(@RequestBody userRequest: UserRequest) {
        userService.create(userRequest)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: Long) {
        userService.delete(id)
    }

    @PutMapping("/update/{id}")
    fun update(@PathVariable id: Long,@RequestBody userUpdateRequest: UserUpdateRequest) {
        userService.update(id, userUpdateRequest)
    }
}









