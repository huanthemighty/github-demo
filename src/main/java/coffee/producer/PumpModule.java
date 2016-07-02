package coffee.producer;

import coffee.Pump;
import coffee.Thermosiphon;
import dagger.producers.ProducerModule;
import dagger.producers.Produces;

/**
 * Created by huansun on 2/29/16.
 */
@ProducerModule
public class PumpModule {
    @Produces
    Pump providePump(Thermosiphon pump) {
        return pump;
    }
}
