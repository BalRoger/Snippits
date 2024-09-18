// package whatever;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static whatever.Util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log4j2
public abstract class BaseTest {

    private AutoCloseable mockCloseable;

    private Object[] mocks = new Object[0];

    @SuppressWarnings("unchecked")
    protected final <T> Class<T> anyClass() {
        return (Class<T>)any(Class.class);
    }

    protected void registerMocks(Object... mocks) {
        this.mocks = opt(mocks).orElse(this.mocks);
    }

    private final List<Runnable> verifierList = new ArrayList<>();

    protected void initializeMocks() {
        mockCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    protected void tearDown() {
        verifierList.forEach(Runnable::run);
        if (mocks.length > 0){
            verifyNoMoreInteractions(mocks);
        }
        opt(mockCloseable).ifPresent( mc -> {
            try {
                mc.close();
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    protected <T,R,E extends Exception> OngoingStubbing<R> methodMock(T mock, EFunction<T,R,E> method, int n) {
        VerificationMode vMode = times(n);
        return methodMock(mock, method, vMode);
    }
   
    protected <T,R,E extends Exception> OngoingStubbing<R> methodMock(
            T mock, EFunction<T,R,E> method, VerificationMode vMode) {
        if(Arrays.stream(mocks).anyMatch(m -> m == mock)) {
            verifierList.add(() -> {
                try {
                    method.apply(verify(mock, vMode));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                return Mockito.when(method.apply(mock));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("the given mock of class '" + mock.getClass() + "' is not registered.");
        }
    }

    protected <T,E extends Exception> void voidMethodMock(T mock, EConsumer<T,E> method, int n, Exception... exes) {
        VerificationMode vMode = times(n);
        voidMethodMock(mock, method, vMode, exes);
    }

    protected <T,E extends Exception> void voidMethodMock(
            T mock, EConsumer<T,E> method, VerificationMode vMode, Exception... exes) {
        if(Arrays.stream(mocks).anyMatch(m -> m == mock)) {
            verifierList.add(() -> {
                try {
                    method.accept(verify(mock, vMode));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                if (opt(exes).filter( e -> e.length != 0).isEmpty()) {
                    method.accept(doNothing().when(mock));
                } else {
                    method.accept(doThrow(exes[0]).when(mock));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("the given mock of class '" + mock.getClass() + "' is not registered.");
        }
    }

    protected <E extends Exception> E executeFailure(ERunnable<E> execute, Class<E> expected) {
        try {
            execute.run();
        } catch (Exception e) {
            return (E) e;
        }

        fail("Should have thrown " + expected.getName());
        return null; // appeasing the compiler: this line will never be executed.
    }

    public interface EFunction<T,R,E extends Exception> {
        R apply(T t) throws E;
    }

    public interface EConsumer<T,E extends Exception> {
        void accept(T t) throws E;
    }

    public interface ERunnable<E extends Exception> {
        void run() throws E;
    }
}
