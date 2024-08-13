/* Copyright 2024 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.object;

import com.fgsoft.exceldatareader.parser.util.BeanAnalyzer;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * This class is responsible for selecting the appropriate parser depending on the type of the object to parse.
 */
public class ObjectParserRouter {
    @SuppressWarnings("unchecked")
    public static  <T> TestDataParser<T> findParser(Field field, CellRangeAddress dataCellRange, CellRangeAddress headerCellRange) {
        final Class<T> fieldType = (Class<T>) field.getType();
        if (List.class.isAssignableFrom(field.getType())) {
            return (TestDataParser<T>) findListParser(field, dataCellRange, headerCellRange);
        } else {
            return new ObjectTestDataParser<>(fieldType, dataCellRange, headerCellRange);
        }
    }

    private static <T extends List<V>, V> TestDataParser<T> findListParser(Field field, CellRangeAddress dataCellRange, CellRangeAddress headerCellRange) {
        final Class<V> genericType = getGenericType(field);
        if (BeanAnalyzer.isSingleCellType(genericType)) {
            return new SingleCellValuesListParser<>(genericType, dataCellRange, headerCellRange);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <V> Class<V> getGenericType(Field field) {
        final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        return (Class<V>) parameterizedType.getActualTypeArguments()[0];
    }

    private ObjectParserRouter() {
        // Prevents instantiation
    }
}
