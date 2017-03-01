package com.orctom.laputa.service.translator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestMapping;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Encode data to protobuff format
 * Created by hao on 11/25/15.
 */
class ProtoBufResponseTranslator implements ResponseTranslator {

  static final MediaType TYPE = MediaType.PROTO_BUF;

  private static LoadingCache<Class<?>, Schema<?>> schemaCache = CacheBuilder.newBuilder()
      .softValues()
      .build(new CacheLoader<Class<?>, Schema<?>>() {
        @Override
        public Schema<?> load(Class<?> clazz) throws Exception {
          return RuntimeSchema.getSchema(clazz);
        }
      });

  @Override
  public String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public String getExtension() {
    return TYPE.getExtension();
  }

  @Override
  @SuppressWarnings("unchecked")
  public byte[] translate(RequestMapping mapping, Object data, Context ctx) throws IOException {
    Schema schema;
    try {
      schema = schemaCache.get(data.getClass());
    } catch (ExecutionException e) {
      schema = RuntimeSchema.getSchema(data.getClass());
    }
    return GraphIOUtil.toByteArray(data, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
  }
}
