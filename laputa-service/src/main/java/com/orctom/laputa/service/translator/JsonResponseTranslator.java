package com.orctom.laputa.service.translator;

import com.alibaba.fastjson.JSON;
import com.orctom.laputa.service.model.MediaType;

import java.io.IOException;

/**
 * Encode data to json
 * Created by hao on 11/25/15.
 */
public class JsonResponseTranslator implements ResponseTranslator {

  public static final MediaType TYPE = MediaType.APPLICATION_JSON;

  @Override
  public String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public byte[] translate(Object data) throws IOException {
    return toBytes(JSON.toJSONString(data));
  }
}
