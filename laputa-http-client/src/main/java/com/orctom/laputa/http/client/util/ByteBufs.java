package com.orctom.laputa.http.client.util;

import io.netty.buffer.ByteBuf;

public abstract class ByteBufs {

  public static byte[] toByteArray(ByteBuf buf) {
    int readable = buf.readableBytes();
    int readerIndex = buf.readerIndex();
    if (buf.hasArray()) {
      byte[] array = buf.array();
      if (buf.arrayOffset() == 0 && readerIndex == 0 && array.length == readable) {
        return array;
      }
    }
    byte[] array = new byte[readable];
    buf.getBytes(readerIndex, array);
    return array;
  }
}
