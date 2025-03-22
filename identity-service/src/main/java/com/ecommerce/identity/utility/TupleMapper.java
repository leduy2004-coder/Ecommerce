package com.ecommerce.identity.utility;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TupleMapper {

    private static final Logger logger = LoggerFactory.getLogger(TupleMapper.class);
    // Phương thức chuyển đổi Tuple thành Map
    private static Map<String, Object> convertTupleToMap(Tuple tuple) {
        Map<String, Object> tupleMap = new HashMap<>();
        for (TupleElement<?> element : tuple.getElements()) {
            String alias = element.getAlias();
            tupleMap.put(alias, tuple.get(element));
        }
        return tupleMap;
    }
    public static <T> T mapToDto(Tuple tuple, Class<T> dtoClass) {
        try {
            T instance = dtoClass.getDeclaredConstructor().newInstance();
            Map<String, Object> tupleMap = convertTupleToMap(tuple);

            for (Field field : dtoClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();

                // Kiểm tra nếu tuple có chứa trường đó
                if (tupleMap.containsKey(fieldName)) {
                    Object value = tupleMap.get(fieldName);
                    if (value != null) {
                        field.set(instance, value);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            logger.error("Error mapping tuple to DTO", e);
            throw new RuntimeException("Error mapping tuple to DTO", e);
        }
    }



    public static <T> List<T> mapListToDto(List<Tuple> tuples, Class<T> dtoClass) {
        List<T> dtos = new ArrayList<>();
        try {
            for (Tuple tuple : tuples) {
                T instance = dtoClass.getDeclaredConstructor().newInstance();
                Map<String, Object> tupleMap = convertTupleToMap(tuple);

                for (Field field : dtoClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    // Kiểm tra nếu tuple có chứa trường đó
                    if (tupleMap.containsKey(fieldName)) {
                        Object value = tupleMap.get(fieldName);
                        if (value != null) {
                            field.set(instance, value);
                        }
                    }
                }
                dtos.add(instance);
            }
        } catch (Exception e) {
            logger.error("Error mapping tuple to DTO", e);
            throw new RuntimeException("Error mapping tuple to DTO", e);
        }
        return dtos;
    }

}
