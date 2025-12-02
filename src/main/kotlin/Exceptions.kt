package main

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler(private val errorMessageSource: ResourceBundleMessageSource) {
    @ExceptionHandler(DemoExceptionHandler::class)
    fun handleAccountException(exception: DemoExceptionHandler): ResponseEntity<BaseMessage> {
        return ResponseEntity.badRequest().body(exception.getErrorMessage(errorMessageSource))
    }
}

sealed class DemoExceptionHandler() : RuntimeException() {
    abstract fun errorCode(): ErrorCodes
    open fun getArguments(): Array<Any?>? = null


    fun getErrorMessage(resourceBundleMessageSource: ResourceBundleMessageSource): BaseMessage {
        val message = try {
            resourceBundleMessageSource.getMessage(
                errorCode().name, getArguments(), LocaleContextHolder.getLocale()
            )
        } catch (e: Exception) {
            e.message
        }

        return BaseMessage(errorCode().code, message)
    }
}

class UserNameAlreadyExistsException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_ALREADY_EXISTS
}

class UserNotFound : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_NOT_FOUND
}


class CategoryNotFound : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.CATEGORY_NOT_FOUND
}

class CategoryAlreadyExists : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.CATEGORY_ALREADY_EXISTS
}

class ProductAlreadyExists : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_ALREADY_EXISTS
}

class ProductNotFound : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_NOT_FOUND
}

class ProductNotFountOrEnough: DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_NOR_FOUNT_OR_ENOUGH
}

class OrderNotFound : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.ORDER_NOT_FOUND
}

class OrderAlreadyExists : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.ORDER_ALREADY_EXISTS
}

class OrderItemNotFound : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.ORDER_ITEM_NOT_FOUND
}

class OrderItemAlreadyExists : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.ORDER_ITEM_ALREADY_EXISTS
}

class PaymentAlreadyExists : DemoExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PAYMENT_METHOD_NOT_FOUND
    }
}

class PaymentNotFound : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.PAYMENT_METHOD_NOT_FOUND
}


