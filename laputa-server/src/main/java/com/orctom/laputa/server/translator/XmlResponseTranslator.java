package com.orctom.laputa.server.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.orctom.laputa.server.example.model.MediaType;

import java.io.IOException;

/**
 * Encode data to xml
 * Created by hao on 11/25/15.
 */
public class XmlResponseTranslator implements ResponseTranslator {

  public static final MediaType TYPE = MediaType.APPLICATION_XML;

  private static ObjectMapper mapper = new XmlMapper();

  @Override
  public String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public byte[] translate(Object data) throws IOException {
    return mapper.writeValueAsBytes(data);
  }
}
