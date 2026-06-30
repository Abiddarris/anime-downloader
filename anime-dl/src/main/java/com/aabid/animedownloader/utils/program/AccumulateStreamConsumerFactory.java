package com.aabid.animedownloader.utils.program;

public class AccumulateStreamConsumerFactory implements StreamConsumerFactory<AccumulateStreamConsumer> {

    @Override
    public AccumulateStreamConsumer newStreamConsumer() {
        return new AccumulateStreamConsumer();
    }

}
