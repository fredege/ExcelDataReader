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

import com.fgsoft.exceldatareader.parser.object.list.BeanWithOnlySingleCellValuesListParser;
import com.fgsoft.exceldatareader.parser.object.list.ListTestDataParser;
import com.fgsoft.exceldatareader.parser.object.list.SingleCellValuesListParser;
import com.fgsoft.exceldatareader.parser.object.object.ObjectTestDataParser;
import com.fgsoft.exceldatareader.parser.util.BeanAnalyzer;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * This class is responsible for selecting the appropriate parser depending on the type of the object to parse.
 */
public class ObjectParserRouter {
    public static <T> TestDataParser<T> findParser(Class<T> dataType, CellRangeAddress dataRange, CellRangeAddress headerRange) {
        return new ObjectTestDataParser<>(dataType, dataRange, headerRange);
    }

    @SuppressWarnings("unchecked")
    public static <T> TestDataParser<T> findParser(Field field, CellRangeAddress dataCellRange, CellRangeAddress headerCellRange) {
        final Class<?> fieldType = field.getType();
        if (List.class.isAssignableFrom(fieldType)) {
            final Class<?> genericType = getGenericType(field);
            return (TestDataParser<T>) findListParser(genericType, dataCellRange, headerCellRange);
        } else {
            final Class<T> dataType = (Class<T>) field.getType();
            return new ObjectTestDataParser<>(dataType, dataCellRange, headerCellRange);
        }
    }

    private static <T extends List<V>, V> TestDataParser<T> findListParser(Class<V> type, CellRangeAddress dataCellRange, CellRangeAddress headerCellRange) {
        if (BeanAnalyzer.isSingleCellType(type)) {
            return new SingleCellValuesListParser<>(type, dataCellRange, headerCellRange);
        } else if (BeanAnalyzer.isBeanWithOnlySingleCellValueFields(type)) {
            return new BeanWithOnlySingleCellValuesListParser<>(type, dataCellRange, headerCellRange);
        } else {
            return new ListTestDataParser<>(type, dataCellRange, headerCellRange);
        }
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
