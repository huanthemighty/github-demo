package coffee.producer;

import coffee.CoffeeMaker;
import coffee.Heater;
import coffee.Pump;
import com.google.common.util.concurrent.ListenableFuture;
import dagger.producers.ProductionComponent;

/**
 * Created by huansun on 2/29/16.
 */
public class CoffeeApp {
    public interface Coffee {
        ListenableFuture<CoffeeMaker> maker();
    }

    public static void main(String[] argv) {

    }
}
