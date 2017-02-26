package com.orctom.laputa.service.translator;

import com.alibaba.fastjson.JSON;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestMapping;

import java.io.IOException;

/**
 * Encode data to json
 * Created by hao on 11/25/15.
 */
class JsonResponseTranslator implements ResponseTranslator {

  static final MediaType TYPE = MediaType.APPLICATION_JSON;

  @Override
  public String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public byte[] translate(RequestMapping mapping, Object data, Context ctx) throws IOException {
    return toBytes(JSON.toJSONString(data));
  }
}
