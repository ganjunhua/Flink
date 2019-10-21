package DataStream.flink.customdatasource

import org.apache.flink.streaming.api.functions.source.{ParallelSourceFunction, SourceFunction}

class CustomParalleleSource extends ParallelSourceFunction[Long] {
  var isRunning = true
  var count = 1L

  override def cancel() = isRunning = false

  override def run(ctx: SourceFunction.SourceContext[Long]) = {
    while (isRunning) {
      ctx.collect(count)
      count += 1
    }

  }
}
