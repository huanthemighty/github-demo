package dagger;

import com.google.auto.value.AutoValue;
import com.google.auto.value.AutoValue.Builder;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import dagger.ChainExample.Output;
import dagger.producers.ProducerModule;
import dagger.producers.Produces;
import dagger.producers.ProductionComponent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutionException;
import javax.inject.Qualifier;

public class RotationExample {
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
    @interface Input {
        String value() default "";
    }

    @AutoValue
    abstract static class Matrix {
        abstract double a();
        abstract double b();
        abstract double c();
        abstract double d();

        static Builder builder() {
            return new AutoValue_RotationExample_Matrix.Builder();
        }

        @AutoValue.Builder
        interface Builder {
            Builder a(double a);
            Builder b(double b);
            Builder c(double c);
            Builder d(double d);
            Matrix build();
        }
    }

    @AutoValue
    abstract static class Vector {
        abstract double x();
        abstract double y();

        static Builder builder() {
            return new AutoValue_RotationExample_Vector.Builder();
        }

        @AutoValue.Builder
        interface Builder {
            Builder x(double x);
            Builder y (double y);
            Vector build();
        }
    }


    @ProducerModule
    static class MatrixModule {
        @Produces
        @Input
        static Matrix produce(@Input("cosine") double cosine, @Input("sine") double sine) {
            return Matrix.builder().a(cosine).b(-sine).c(sine).d(cosine).build();
        }
    }

    @ProducerModule
    static class VectorModule {
        @Produces
        @Input
        static Vector produce(@Input("x") double x, @Input("y") double y) {
            return Vector.builder().x(x).y(y).build();
        }
    }

    // note that we can use either T or ListenableFuture<T> as return type in ProducerModule.
    @ProducerModule
    static class CosineModule {
        @Produces
        @Input("cosine")
        static Double cosine(@Input("theta") double theta) {
            return Math.cos(theta);
        }
    }

    @ProducerModule
    static class SineModule {
        @Produces
        @Input("sine")
        static ListenableFuture<Double> sine(@Input("theta") double theta) {
            return Futures.immediateFuture(Math.sin(theta));
        }
    }

    @ProducerModule
    static class MultiplicationModule {
        @Produces
        @Output
        static Vector multiply(@Input Matrix matrix, @Input Vector vector) {
            return Vector.builder().x(matrix.a() * vector.x() + matrix.b() * vector.y()).y(matrix.c() * vector.x() + matrix.d() * vector.y()).build();
        }
    }

    @AutoValue
    abstract static class InputProvider {
        @Input("theta") abstract double theta();

        static Builder builder() {
            return new AutoValue_RotationExample_InputProvider.Builder();
        }

        @AutoValue.Builder
        interface Builder {
            InputProvider build();
            Builder setTheta(double theta);
        }
    }

    @AutoValue
    abstract static class VectorProvider {
        @Input("x") abstract double x();
        @Input("y") abstract double y();

        static Builder builder() {
            return new AutoValue_RotationExample_VectorProvider.Builder();
        }

        @AutoValue.Builder
        interface Builder {
            VectorProvider build();
            Builder setX(double x);
            Builder setY(double y);
        }
    }

    @ProductionComponent(modules = {SineModule.class, CosineModule.class, VectorModule.class, MatrixModule.class, MultiplicationModule.class}, dependencies = {InputProvider.class, VectorProvider.class})
    interface MultiplicationComponent {
        @Output ListenableFuture<Vector> result();
    }

    public static void main(String[] argv) throws ExecutionException, InterruptedException {
        double theta = Math.PI; // rotate 180 degree
        double x = 0.5;
        double y = 1.0;

        MultiplicationComponent dag = DaggerRotationExample_MultiplicationComponent.builder().executor(MoreExecutors.directExecutor()).inputProvider(InputProvider.builder().setTheta(theta).build()).vectorProvider(VectorProvider.builder().setX(x).setY(y).build()).build();
        System.out.println(dag.result().get());
    }

}
