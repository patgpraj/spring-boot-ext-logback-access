package net.rakugakibox.springbootext.logback.access.test;

import ch.qos.logback.access.spi.IAccessEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

/**
 * The assertion of the {@link IAccessEvent}.
 */
public class AccessEventAssert<S extends AccessEventAssert<S, A>, A extends IAccessEvent> extends AbstractAssert<S, A> {

    /**
     * Constructs an instance.
     *
     * @param actual the actual value.
     */
    protected AccessEventAssert(A actual) {
        super(actual, AccessEventAssert.class);
    }

    /**
     * Constructs an instance.
     *
     * @param actual the actual value.
     * @param selfClass the self class.
     */
    protected AccessEventAssert(A actual, Class<?> selfClass) {
        super(actual, selfClass);
    }

    /**
     * Verifies that the timestamp in the actual value is in given range.
     *
     * @param start the start value of range (inclusive).
     * @param end the end value of range (inclusive).
     * @return this instance.
     */
    public S hasTimestampOfBetween(LocalDateTime start, LocalDateTime end) {
        LocalDateTime timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actual.getTimeStamp()), ZoneId.systemDefault());
        Assertions.assertThat(timestamp).isAfterOrEqualTo(start).isBeforeOrEqualTo(end);
        return myself;
    }

    /**
     * Verifies that the content length in the actual value is equal to the given one.
     *
     * @param expected the expected content length.
     * @return this instance.
     */
    public S hasContentLength(long expected) {
        Assertions.assertThat(actual.getContentLength()).isEqualTo(expected);
        return myself;
    }

    /**
     * Starts the assertion.
     *
     * @param <A> the actual value type.
     * @param actual the actual value.
     * @return an assertion instance.
     */
    public static <A extends IAccessEvent> AccessEventAssert<?, A> assertThat(A actual) {
        return new AccessEventAssert<>(actual);
    }

}
