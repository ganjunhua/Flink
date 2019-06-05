package com.holiday.flink.train.datastream.course05

import org.apache.flink.streaming.api.functions.source.{ParallelSourceFunction, SourceFunction}

class CustomParallelSourceFunction extends ParallelSourceFunction[Long] {
  var isRunning = true
  var count = 1L

  override def cancel() = isRunning = false

  override def run(ctx: SourceFunction.SourceContext[Long]) = {
    while (isRunning) {
      ctx.collect(count)
      count += 1
      Thread.sleep(1000)
    }
  }
}
