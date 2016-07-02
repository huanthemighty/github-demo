package dagger;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import dagger.producers.ProducerModule;
import dagger.producers.Produces;
import dagger.producers.ProductionComponent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * In this example we are accomplishing the simple task of multiplication using Dagger producer framework.
 */
public class MultiplierExample {
    /**
     * Annotation for Input of the multiplier
     */
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.PARAMETER})
    @interface MultiplierInput {}

    interface MultiplyInput {
        // it also works if we return a ListenableFuture<Integer> here
        @MultiplierInput Integer input();
    }

    @ProducerModule
    static class AsyncMultiplyModule {

        private final int multiplier;

        private AsyncMultiplyModule(int multiplier) {
            this.multiplier = multiplier;
        }

        static AsyncMultiplyModule instance(int multiplier) {
            return new AsyncMultiplyModule(multiplier);
        }

        @Produces
        ListenableFuture<Integer> asyncDoubleIt(@MultiplierInput int value) {
            // pretend this is an expensive computation
            return Futures.immediateFuture(value * multiplier);
        }
    }

    /**
     * This is the entry point calling dagger and getting output
     */
    @ProductionComponent(modules = AsyncMultiplyModule.class, dependencies = MultiplyInput.class)
    public interface AsyncMultiplyComponent {
        ListenableFuture<Integer> finalResult();
    }

    public static void main(String[] argv) throws Exception {
        final int val = 1;
        AsyncMultiplyComponent component = DaggerMultiplierExample_AsyncMultiplyComponent.builder().asyncMultiplyModule(AsyncMultiplyModule.instance(2)).multiplyInput(new MultiplyInput() {
            @Override
            public Integer input() {
                return val;
            }
        }).executor(MoreExecutors.directExecutor()).build();

        int result = component.finalResult().get();

        System.out.println(result);
    }
}
