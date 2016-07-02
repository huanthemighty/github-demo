package coffee.producer;

import coffee.ElectricHeater;
import coffee.Heater;
import dagger.producers.ProducerModule;
import dagger.producers.Produces;
import dagger.producers.ProductionComponent;

/**
 * Created by huansun on 2/29/16.
 */
@ProducerModule(includes = PumpModule.class)
public class DripCoffeeModule {
    @Produces
    Heater provideHeater() {
        return new ElectricHeater();
    }
}
