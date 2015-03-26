package org.acra;

/**
 * The interface can be used with
 * {@link org.acra.ErrorReporter#setExceptionHandlerInitializer(org.acra.ExceptionHandlerInitializer)}
 * to add an additional initialization of the {@link org.acra.ErrorReporter} before
 * exception is handled.
 *
 * @see org.acra.ErrorReporter#setExceptionHandlerInitializer(org.acra.ExceptionHandlerInitializer)
 */
public interface ExceptionHandlerInitializer {
    /**
     * Called before {@link org.acra.ErrorReporter} handles the Exception.
     *
     * @param reporter The {@link org.acra.ErrorReporter} that will handle the exception
     */
    void initializeExceptionHandler(ErrorReporter reporter);
}