package sk.adresa.eaukcia.core.util;

/**
 * Assertion utility class that assists in validating arguments.
 * 
 * @author juraj
 *
 */
public final class Assert {

    /**
     * Assert a boolean parameter, throwing <code>IllegalArgumentException</code>
     * if the test result is <code>false</code>.
     *
     * @param parameter a boolean parameter
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean parameter, String parameterName) {
        if (!parameter) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' must be true", parameterName));
        }
    }

    /**
     * Assert that an parameter is <code>null</code> .
     *
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException if the object is not <code>null</code>
     */
    public static void isNull(Object parameter, String parameterName) {
        if (parameter != null) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' must be null", parameterName));
        }
    }

    /**
     * Assert that an parameter is not <code>null</code> .
     * 
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException if the parameter is <code>null</code>
     */
    public static void isNotNull(Object parameter, String parameterName) {
        if (parameter == null) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' is required; must not be null", parameterName));
        }
    }

    /**
     * Assert that an parameter is not empty.
     * @param <T>
     * 
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException if the parameter is <code>null</code> or empty
     */
    public static <T> void isNotEmpty(Iterable<T> parameter, String parameterName) {
        Assert.isNotNull(parameter, parameterName);
        if (!parameter.iterator().hasNext()) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' must not be empty", parameterName));
        }
    }

    /**
     * Assert that an string parameter is not <code>null</code> nor empty.
     * 
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException if the parameter is <code>null</code> or empty
     */
    public static void isNotNullOrEmpty(String parameter, String parameterName) {
        Assert.isNotNull(parameter, parameterName);
        if (parameter.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' must not be empty", parameterName));
        }
    }

    /**
     * Assert that number parameter does not have negative value.
     * 
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException is parameter has negative value.
     */
    public static void isNotNegative(Number parameter, String parameterName) {
        if (parameter.doubleValue() < 0) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' must not be negative", parameterName));
        }
    }

    /**
     * Assert that number parameter does not have zero or negative value.
     * 
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException is parameter has negative or zero value.
     */
    public static void isNotNegativeOrZero(Number parameter, String parameterName) {
        if (parameter.doubleValue() <= 0) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' must not be negative nor zero", parameterName));
        }
    }
    
    public static void isGreater(Number parameter, Number graterThan, String parameterName) {
        if (parameter.doubleValue() <= graterThan.doubleValue()) {
            throw new IllegalArgumentException(
                    String.format("[Assertion failed] - parameter '%s' must be greater than '%s'", parameterName, graterThan.toString()));
        }
    }
}
