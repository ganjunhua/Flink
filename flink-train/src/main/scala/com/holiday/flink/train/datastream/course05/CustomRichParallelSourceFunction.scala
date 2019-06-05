package com.holiday.flink.train.datastream.course05

import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

class CustomRichParallelSourceFunction extends RichParallelSourceFunction[Long] {
  var isRunning = true
  var count = 1L

  override def cancel() = isRunning = false

  override def run(ctx: SourceFunction.SourceContext[Long]) = {
    while (isRunning) {
      // 把数据发出去
      ctx.collect(count)
      count += 1
      Thread.sleep(1000)
    }
  }
}
