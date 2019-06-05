package com.holiday.flink.train.datastream.course05;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.ArrayList;
import java.util.List;

public class JavaCustomParallelSourceApplication implements ParallelSourceFunction<List<Tuple2<String, Long>>> {
    boolean isRunning = true;
    long count = 1L;

    public void run(SourceContext<List<Tuple2<String, Long>>> ctx) throws Exception {
        List<Tuple2<String, Long>> list = new ArrayList<Tuple2<String, Long>>();
        while (isRunning) {
            list.add(new Tuple2<String, Long>("holiday", count));
            ctx.collect(list);
            count += 1;
            Thread.sleep(1000);
        }
    }

    public void cancel() {
        isRunning = false;
    }
}
