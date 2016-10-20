package com.orctom.laputa.server.example.processor;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orctom.laputa.server.processor.PreProcessor;
import com.orctom.laputa.server.example.util.AES;
import com.orctom.laputa.server.model.RequestWrapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AESCryptor implements PreProcessor {

  private static final byte[] KEY = "BGNJLEfl4MJaGXwYZc4DosxYHJaKTJmEAV55FaFfES+ecSIHomxK0d2exkxhDm+k".getBytes();

  @Override
  public void process(RequestWrapper requestWrapper) {
    Map<String, List<String>> requestParams = requestWrapper.getParams();

    // dummy
    if (requestParams.containsKey("hello")) {
      requestParams.put("version", Lists.newArrayList("1.0"));
    }

    List<String> encryped = requestParams.get("q");
    if (null == encryped || encryped.isEmpty()) {
      return;
    }

    String decryped = Base64.getEncoder().encodeToString(AES.decrypt(encryped.get(0).getBytes(), KEY));
    if (Strings.isNullOrEmpty(decryped)) {
      return;
    }

    Map<String, List<String>> newParams = new HashMap<>();
    Splitter.on("&").split(decryped).forEach(item -> {
      String[] param = item.split("=");
      String key = param[0];
      String value = param[1];
      List<String> values = newParams.get(key);
      if (null == values) {
        values = new ArrayList<>();
        newParams.put(key, values);
      }

      values.add(value);
    });

    requestWrapper.setParams(newParams);
  }
}
