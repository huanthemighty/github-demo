package dagger;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import dagger.producers.ProducerModule;
import dagger.producers.Produces;
import dagger.producers.ProductionComponent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * This example calculates exp(cos(x)) using dagger.
 */
public class ChainExample {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
    @interface Input {
        String value() default "";
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
    @interface Output {
        String value() default "";
    }

    interface XProvider {
        @Input("x") double x();
    }

    @ProducerModule
    static class ChainModule {
        @Produces
        @Output
        Double exp(@Input("cosx") double cosx) {
            return Math.exp(cosx);
        }

        @Produces
        @Input("cosx")
        Double cos(@Input("x") double x) {
            return Math.cos(x);
        }
    }

    @ProductionComponent(modules = ChainModule.class, dependencies = XProvider.class)
    interface ResultComponent {
        @Output
        ListenableFuture<Double> finalResult();
    }

    @ProductionComponent(modules = ChainModule.class, dependencies = XProvider.class)
    abstract static class ResultClassComponent {
        ListenableFuture<Double> finalResult(@Output double p) {
            return Futures.immediateFuture(2.0 * p);
        }
    }



    public static void main(String[] argv) {

    }

}
