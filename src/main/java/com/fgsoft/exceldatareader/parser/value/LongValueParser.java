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

public class LongValueParser extends AbstractSingleCellValueParser<Long> {
    @Override
    protected Long getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected Long getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        return (long) value;
    }

    @Override
    protected Long getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, value, Long.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected Long getValueForCell(String strValue, int rowIndex, int colIndex, Sheet worksheet) {
        final long longValue;
        try {
            longValue = Long.parseLong(strValue);
        } catch (NumberFormatException exc){
            throw new IncorrectValueForTypeException(exc, strValue, Long.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return longValue;
    }
}
