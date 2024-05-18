package domain.util

// Response Wrapper Class
sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    // Utility functions to get state of the data
    fun isLoading(): Boolean = this is Loading
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error

    // Functions to get generic data from the state
    fun getSuccessData() = (this as Success).data
    fun getErrorMessage() = (this as Error).message
}