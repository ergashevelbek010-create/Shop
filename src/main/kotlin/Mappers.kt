package dev.pdp

import org.springframework.stereotype.Component


@Component
class UserMapper{
    fun toEntity(user: UserRequest): User {
        user.run {
            return User(
                username = name!!,
                fullName = fullName,
                balance = balance!!
            )
        }
    }

    fun fromEntity(user: User): UserResponse{
      user.run {
          return UserResponse(
              id = id!!,
              name = username,
              fullName = fullName,
              balance = balance
          )
      }
    }
}

@Component
class ProductMapper{
    fun toEntity(product: ProductRequest,category: Category): Product {
        product.run {
            return Product(
                name = name,
                count = count,
                category = category
            )
        }
    }

    fun fromEntity(product: Product,category: Category): ProductResponse {
        product.run {
            return ProductResponse(
                name = name,
                count = count,
                id = id!!,
                categoryId = category,
            )
        }
    }

}

@Component
class CategoryMapper{
    fun toEntity(categoryRequest: CategoryRequest): Category {
        categoryRequest.run {
            return Category(
                name = name,
                description = description,
                orders = order
            )
        }
    }

    fun fromEntity(category: Category): CategoryResponse{
        category.run {
            return CategoryResponse(
                id = id!!,
                name = name,
                description = description,
                order = orders
            )
        }
    }
}

