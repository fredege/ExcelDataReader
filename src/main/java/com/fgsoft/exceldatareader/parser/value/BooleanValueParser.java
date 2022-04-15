/* Copyright 2020 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.value;

import com.fgsoft.exceldatareader.exception.IncorrectValueForTypeException;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;

public class BooleanValueParser extends AbstractSingleCellValueParser<Boolean> {
    private static final Map<String, Boolean> MAP_VALUES = new HashMap<>();
    static {
        MAP_VALUES.put("YES", true);
        MAP_VALUES.put("NO", false);
        MAP_VALUES.put("Y", true);
        MAP_VALUES.put("N", false);
        MAP_VALUES.put("Yes", true);
        MAP_VALUES.put("No", false);
        MAP_VALUES.put("true", true);
        MAP_VALUES.put("false", false);
    }
    @Override
    protected Boolean getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null; //NOSONAR
    }

    @Override
    protected Boolean getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        if (value == 0.0) {
            // Case of formula return
            return null; //NOSONAR
        } else {
            throw new IncorrectValueForTypeException(null, String.valueOf(value), Boolean.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
    }

    @Override
    protected Boolean getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        return value;
    }

    @Override
    protected Boolean getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        if (MAP_VALUES.containsKey(value)) {
            return MAP_VALUES.get(value);
        } else {
            throw new IncorrectValueForTypeException(null, value, Boolean.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
    }
}
