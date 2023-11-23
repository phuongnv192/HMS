package com.module.project.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public class JsonService {

    private final ObjectMapper om;
    private static JsonService instance;

    private JsonService() {
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.findAndRegisterModules();
    }

    public static JsonService getInstance() {
        if (instance == null) {
            instance = new JsonService();
        }
        return instance;
    }

    public static ObjectMapper getObjectMapper() {
        return getInstance().om;
    }

    public static String writeStringSkipError(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (Exception ex) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V toObject(String json, Class<?> clazz) {
        try {
            return (V) getObjectMapper().readValue(json, clazz);
        } catch (Exception ex) {
            log.warn("toObject: Exception: {}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V toObject(Object value, Class<?> clazz) {
        try {
            return (V) getObjectMapper().convertValue(value, clazz);
        } catch (Exception ex) {
            log.warn("toObject: Exception: {}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V toObject(Object value, TypeReference<?> type) {
        try {
            return (V) getObjectMapper().convertValue(value, type);
        } catch (Exception ex) {
            log.warn("toObject: Exception: {}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    public static <T> T strToObject(String str, TypeReference<T> valueTypeRef) {
        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            return objectMapper.readValue(str, valueTypeRef);
        } catch (Exception ex) {
            log.warn("Exception strResponseToObject: {}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }
}