/* Copyright 2022 Frederic GEDIN
 *
 *       Licensed under the Apache License,Version2.0(the"License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing,software
 *       distributed under the License is distributed on an"AS IS"BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */
package com.fgsoft.exceldatareader.parser.util;

import com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode;
import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
public class BeanAnalyzer {
    private static final List<Class<?>> SINGLE_CELL_TYPES = Arrays.asList(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            Character.class,
            String.class,
            Date.class,
            LocalTime.class,
            LocalDate.class,
            LocalDateTime.class,
            BigDecimal.class,
            BigInteger.class,
            Enumeration.class,
            Currency.class,
            UUID.class
    );

    /**
     * Checks is an object can be read from a single Excel cell. The following types are considered as single cell value:
     * <ul>
     *     <li>Java primitive types</li>
     *     <li>String</li>
     *     <li>Enum</li>
     *     <li>Java time objects, i.e. LocalDate, LocalTime, LocalDateTime</li>
     * </ul>
     *
     * @param object object to be checked
     * @return true when can be parsed from a single cell
     */
    public static boolean hasSingleCellValue(@NonNull final Object object) {
        return isSingleCellType(object.getClass());
    }

    public static boolean isSingleCellType(@NonNull Class<?> type) {
        return type.isPrimitive() || type.isEnum() || SINGLE_CELL_TYPES.contains(type);
    }

    public static boolean isBeanWithOnlySingleCellValueFields(@NonNull Class<?> clazz) {
        return FieldUtils.getAllFieldsList(clazz).stream()
                .filter(f -> !isSingleCellType(f.getType()))
                .toList().isEmpty();
    }


    public static <T> List<Field> getSingleCellValues(Class<T> clazz) {
        return FieldUtils.getAllFieldsList(clazz).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> isSingleCellType(field.getType()))
                .toList();
    }

    public static <T> List<Field> getMultipleCellsValues(Class<T> clazz) {
        return FieldUtils.getAllFieldsList(clazz).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !isSingleCellType(field.getType()))
                .toList();
    }

    public static <T, V> void setValueOnField(T instance, Field field, V value) {
        if (value != null) {
            try {
                final BeanInfo beanInfo = Introspector.getBeanInfo(instance.getClass());
                Optional<PropertyDescriptor> propertyDescriptor = Arrays.stream(beanInfo.getPropertyDescriptors())
                        .filter(pd -> StringUtils.uncapitalize(pd.getName()).equals(field.getName()))
                        .findAny();
                if (propertyDescriptor.isPresent()) {
                    setValueOnField(instance, field, propertyDescriptor.get(), value);
                } else {
                    throw new ExcelReaderException(ExcelReaderErrorCode.UNKNOWN);
                }
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException exc) {
                throw new ExcelReaderException(ExcelReaderErrorCode.UNKNOWN);
            }
        }
    }

    private static <T, V> void setValueOnField(T instance, Field field, PropertyDescriptor propertyDescriptor, V value) throws InvocationTargetException, IllegalAccessException {
        final Method setter = propertyDescriptor.getWriteMethod();
        if (hasSameType(value, field)) {
            if (setter != null) {
                setter.invoke(instance, value);
            } else {
                throw new ExcelReaderException(ExcelReaderErrorCode.NO_SETTER, field.getName());
            }
        } else {
            throw new ExcelReaderException(ExcelReaderErrorCode.MISMATCHING_DATA_TYPES, field.getType().getName(),
                    value.getClass().getName(), field.getName());
        }
    }

    private static <V> boolean hasSameType(V value, Field field) {
        final Class<?> fieldType = field.getType().isPrimitive() ?
                ClassUtils.primitiveToWrapper(field.getType()) :
                field.getType();
        return fieldType.isAssignableFrom(value.getClass());
    }

    private BeanAnalyzer() {
        // Prevents instantiation
    }

}
