package com.orctom.laputa.service.example.processor;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.orctom.laputa.service.example.util.AES;
import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class AESCryptor implements Filter {

  private static final byte[] KEY = "BGNJLEfl4MJaGXwYZc4DosxYHJaKTJmEAV55FaFfES+ecSIHomxK0d2exkxhDm+k".getBytes();
  private static final String UTF8 = "UTF-8";

  @Override
  public void doFilter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain) {
    try {
      decryptQueryString(requestWrapper);

      filterChain.doFilter(requestWrapper, responseWrapper);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void decryptQueryString(RequestWrapper requestWrapper) throws UnsupportedEncodingException {
    Map<String, List<String>> requestParams = requestWrapper.getParams();

    List<String> encrypted = requestParams.remove("data");
    if (null == encrypted || encrypted.isEmpty()) {
      return;
    }

    String decrypted = Base64.getEncoder().encodeToString(AES.decrypt(encrypted.get(0).getBytes(), KEY));
    String decoded = URLDecoder.decode(decrypted, UTF8);
    Splitter
        .on("&")
        .omitEmptyStrings()
        .trimResults()
        .withKeyValueSeparator("=")
        .split(decoded)
        .forEach((key, value) -> requestParams.put(key, Lists.newArrayList(value)));
  }
}
