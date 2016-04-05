package com.jigsaw.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;






public class JsonUtils
{
  private static final ObjectMapper mapper = new ObjectMapper();
  static { mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); }
  
  public static String toString(Object object)
  {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public JsonUtils() {}
}
