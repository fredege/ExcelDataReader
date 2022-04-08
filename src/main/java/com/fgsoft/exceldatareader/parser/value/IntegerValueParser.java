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

public class IntegerValueParser extends AbstractSingleCellValueParser<Integer> {
    @Override
    protected Integer getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected Integer getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        return (int) value;
    }

    @Override
    protected Integer getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, value, Integer.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected Integer getValueForCell(String strValue, int rowIndex, int colIndex, Sheet worksheet) {
        final int intValue;
        try {
            intValue = Integer.parseInt(strValue);
        } catch (NumberFormatException exc){
            throw new IncorrectValueForTypeException(exc, strValue, Integer.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return intValue;
    }
}
