package dev.pdp

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

class CategoryAlreadyExistsException : DemoExceptionHandler() {
    override fun errorCode(): ErrorCodes = ErrorCodes.CATEGORY_ALREADY_EXISTS
}

class CategoryNotFound : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.CATEGORY_NOT_FOUND
}

class ProductAlreadyExistsException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_ALREADY_EXISTS
}
class ProductNotFound : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_NOT_FOUND
}

class UserPaymentAmountHistoryNotFount: DemoExceptionHandler(){
    override fun errorCode(): ErrorCodes = ErrorCodes.USER_PAYMENT_NOT_FOUND
}

class TransactionNotFound : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.TRANSACTION_NOT_FOUND
}

class TransactionAlreadyExists : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.TRANSACTION_ALREADY_EXISTS
}

class TransactionItemAlreadyExists : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.TRANSACTION_ITEM_ALREADY_EXISTS
}

class TransactionItemNotFound : DemoExceptionHandler(){
    override fun errorCode() = ErrorCodes.TRANSACTION_ITEM_NOT_FOUND
}


