package com.github.mauricio.async.db.mysql.codec

import com.github.jasync.sql.db.util.ByteBufferUtils
import com.github.mauricio.async.db.mysql.message.client.ClientMessage
import com.github.mauricio.async.db.mysql.message.client.SendLongDataMessage
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import mu.KotlinLogging


private val loggger = KotlinLogging.logger {}

class SendLongDataEncoder
  : MessageToMessageEncoder<SendLongDataMessage>(SendLongDataMessage::class.java) {


  companion object {
    val LONG_THRESHOLD = 1023

  }

  override fun encode(ctx: ChannelHandlerContext, message: SendLongDataMessage, out: MutableList<Any>) {
    if (loggger.isTraceEnabled) {
      loggger.trace("Writing message $message")
    }

    val sequence = 0

    val headerBuffer = ByteBufferUtils.mysqlBuffer(3 + 1 + 1 + 4 + 2)
    ByteBufferUtils.write3BytesInt(headerBuffer, 1 + 4 + 2 + message.value.readableBytes())
    headerBuffer.writeByte(sequence)

    headerBuffer.writeByte(ClientMessage.PreparedStatementSendLongData)
    headerBuffer.writeBytes(message.statementId)
    headerBuffer.writeShort(message.paramId)

    val result = Unpooled.wrappedBuffer(headerBuffer, message.value)

    out.add(result)
  }

}
