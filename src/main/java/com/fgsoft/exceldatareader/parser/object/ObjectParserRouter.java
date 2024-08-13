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

import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * This class is responsible for selecting the appropriate parser depending on the type of the object to parse.
 */
public class ObjectParserRouter {
    @SuppressWarnings("unchecked")
    public static  <T> TestDataParser<T> findParser(Class<T> type, CellRangeAddress dataCellRange, CellRangeAddress headerCellRange) {
        if (type.isAssignableFrom(List.class)) {
            return (TestDataParser<T>) findListParser(dataCellRange, headerCellRange);
        } else {
            return new ObjectTestDataParser<>(dataCellRange, headerCellRange);
        }
    }

    private static <V> TestDataParser<List<V>> findListParser(CellRangeAddress dataCellRange, CellRangeAddress headerCellRange) {
        return new ListTestDataParser<>(dataCellRange, headerCellRange);
    }

    private ObjectParserRouter() {
        // Prevents instantiation
    }
}
