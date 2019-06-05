package com.holiday.flink.train.datastream.course05

import org.apache.flink.streaming.api.functions.source.SourceFunction

class CustomNonParallelSourceFunctionApp extends SourceFunction[Long] {
  var count = 1l
  var isRunning = true

  override def cancel() = isRunning = false

  override def run(ctx: SourceFunction.SourceContext[Long]) = {
    while (isRunning) {
      ctx.collect(count)
      count += 1
      Thread.sleep(1000)
    }
  }
}
