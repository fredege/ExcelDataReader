/* Copyright 2023 Frederic GEDIN
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

import com.fgsoft.exceldatareader.parser.HeaderDescriptor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is responsible to analyze an Excel worksheet in order to:
 * <ul>
 *     <li>Collect headers matching </li>
 *     <li>Locate the cell where to get a value to parse</li>
 * </ul>
 */
@RequiredArgsConstructor
public class WorksheetAnalyser {
    private final Sheet worksheet;
    private final HeaderDescriptor headerDescriptor;

    /**
     * Scan the worksheet in order to get the map of headers and fields of the object to get value from parsing
     * @param clazz class of the object to get value from parsing
     * @return Map of object fields headers and columns in worksheet.
     */
    public Map<String, Integer> getHeadersMap(Class<?> clazz) {
        final Map<String, Integer> retMap = new HashMap<>();
        final List<String> headerNames = parseHeaders();
        for (final Field field : clazz.getDeclaredFields()) {
            final String fieldName = field.getName();
            if (headerNames.contains(fieldName)) {
                retMap.put(fieldName, headerNames.indexOf(fieldName));
            }
        }
        return retMap;
    }

    private List<String> parseHeaders() {
        final List<String> headerNames = new ArrayList<>();
        for (int rowNum = headerDescriptor.getFirstTitleRow(); rowNum <= headerDescriptor.getLastTitleRow(); rowNum++) {
            final Row row = worksheet.getRow(rowNum);
            for (Cell cell : row) {
                final String value = cell.getStringCellValue();
                final int column = cell.getColumnIndex();
                if (headerNames.size() <= column) {
                    headerNames.add(column, value);
                } else {
                    final String previous = headerNames.get(column);
                    headerNames.set(column, String.format("%s.%s", previous, value));
                }
            }
        }
        return headerNames;
    }
}
