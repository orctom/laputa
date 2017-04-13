package com.orctom.laputa.service.translator.content;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
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
class ProtoBufContentTranslator implements ContentTranslator {

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
  public byte[] translate(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) throws IOException {
    Object result = responseWrapper.getResult();
    Schema schema;
    try {
      schema = schemaCache.get(result.getClass());
    } catch (ExecutionException e) {
      schema = RuntimeSchema.getSchema(result.getClass());
    }
    return GraphIOUtil.toByteArray(result, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
  }
}
