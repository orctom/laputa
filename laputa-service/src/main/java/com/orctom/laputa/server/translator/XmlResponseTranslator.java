package com.orctom.laputa.server.translator;

import com.orctom.laputa.server.model.MediaType;
import com.thoughtworks.xstream.XStream;

import java.io.IOException;

/**
 * Encode data to xml
 * Created by hao on 11/25/15.
 */
public class XmlResponseTranslator implements ResponseTranslator {

  public static final MediaType TYPE = MediaType.APPLICATION_XML;

  private static XStream xstream = new XStream();

  @Override
  public String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public byte[] translate(Object data) throws IOException {
    return toBytes(xstream.toXML(data));
  }
}
