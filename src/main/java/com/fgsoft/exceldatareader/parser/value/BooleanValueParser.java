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
    private static final Map<String, Boolean> mapValues = new HashMap<>();
    static {
        mapValues.put("YES", true);
        mapValues.put("NO", false);
        mapValues.put("Y", true);
        mapValues.put("N", false);
        mapValues.put("Yes", true);
        mapValues.put("No", false);
        mapValues.put("true", true);
        mapValues.put("false", false);
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
        if (mapValues.containsKey(value)) {
            return mapValues.get(value);
        } else {
            throw new IncorrectValueForTypeException(null, value, Boolean.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
    }
}
