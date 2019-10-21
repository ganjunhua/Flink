package DataStream.flink.customdatasource

import org.apache.flink.streaming.api.functions.source.SourceFunction

class CustomNonParalleleSource extends SourceFunction[Long] {
  var count = 0L
  var isRunning = true

  override def cancel(): Unit = {
    println("override def cancel()..................")
    isRunning = false
  }

  override def run(ctx: SourceFunction.SourceContext[Long]) = {

    while (isRunning ) {
      println("isRunning:=" + isRunning)
      //将数据写出去
      ctx.collect(count)
      count += 1
      if (count == 11) cancel()
      Thread.sleep(100)
    }

  }
}
