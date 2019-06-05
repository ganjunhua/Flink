package com.holiday.flink.train.datastream.course05;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class JavaCustomNonParallelSourceApplication implements SourceFunction<Long> {
    boolean isRunning = true;
    long count = 1;

    public void run(SourceContext<Long> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(count);
            count += 1;
            Thread.sleep(1000);
        }
    }

    public void cancel() {
        isRunning = false;
    }
}
