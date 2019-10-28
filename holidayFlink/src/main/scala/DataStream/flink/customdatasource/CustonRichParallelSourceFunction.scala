package DataStream.flink.customdatasource

import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

class CustonRichParallelSourceFunction extends RichParallelSourceFunction[Long] {
  var isRunning = true
  var count = 1l

  override def cancel() = isRunning = false

  override def run(ctx: SourceFunction.SourceContext[Long]) = {
    while (isRunning) {
      ctx.collect(count)
      count += 1
      Thread.sleep(1000)
    }
  }
}
