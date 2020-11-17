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

import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import org.apache.poi.ss.usermodel.Sheet;

import static com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE;

public class DoubleValueParser extends AbstractValueParser<Double> {
    @Override
    protected Double getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected Double getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        return value;
    }

    @Override
    protected Double getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new ExcelReaderException(INCORRECT_VALUE_FOR_TYPE, value, Double.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected Double getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        final double retVal;
        try {
            retVal = Double.parseDouble(value);
        } catch (NumberFormatException exc) {
            throw new ExcelReaderException(exc, INCORRECT_VALUE_FOR_TYPE, value, Double.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return retVal;
    }

    @Override
    protected Double getValueForNullCell() {
        return null;
    }
}
