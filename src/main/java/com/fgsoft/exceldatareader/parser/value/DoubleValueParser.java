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

/**
 * Single cell value parser in charge of returning a Double. The Double to be returned can be defined
 * from some types of cell content:
 * <ul>
 *     <li>STRING type cell content is returned as the string parsing of the cell content. In case of
 *     inappropriate string format, a @see com.fgsoft.exceldatareader.exception.ExcelReaderException
 *     is raised </li>
 *     <li>NUMERIC type cell content is directly returned</li>
 *     <li>For BOOLEAN type cell content a @see com.fgsoft.exceldatareader.exception.ExcelReaderException
 *     is raised</li>
 *     <li>For BLANK type cell, a null value is returned</li>
 * </ul>
 */
public class DoubleValueParser extends AbstractSingleCellValueParser<Double> {
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
        throw new IncorrectValueForTypeException(null, value, Double.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected Double getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        final double retVal;
        try {
            retVal = Double.parseDouble(value);
        } catch (NumberFormatException exc) {
            throw new IncorrectValueForTypeException(null, value, Double.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return retVal;
    }
}
