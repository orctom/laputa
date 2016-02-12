package com.orctom.laputa.server.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orctom.laputa.server.model.MediaType;

import java.io.IOException;

/**
 * Encode data to json
 * Created by hao on 11/25/15.
 */
public class JsonResponseTranslator implements ResponseTranslator {

  public static final MediaType TYPE = MediaType.APPLICATION_JSON;

  private static ObjectMapper mapper = new ObjectMapper();

  @Override
  public String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public byte[] translate(Object data) throws IOException {
    return mapper.writeValueAsBytes(data);
  }
}
