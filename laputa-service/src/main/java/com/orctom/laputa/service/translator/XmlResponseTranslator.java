package com.orctom.laputa.service.translator;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestMapping;
import com.thoughtworks.xstream.XStream;

import java.io.IOException;

/**
 * Encode data to xml
 * Created by hao on 11/25/15.
 */
class XmlResponseTranslator implements ResponseTranslator {

  static final MediaType TYPE = MediaType.APPLICATION_XML;

  private static XStream xstream = new XStream();

  @Override
  public MediaType getMediaType() {
    return TYPE;
  }

  @Override
  public byte[] translate(RequestMapping mapping, Object data, Context ctx) throws IOException {
    return toBytes(xstream.toXML(data));
  }
}
