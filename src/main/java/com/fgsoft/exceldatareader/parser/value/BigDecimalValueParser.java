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

import java.math.BigDecimal;

/**
 * BigDecimal is not a format stored in Excel.Therefore, this parser will rely on a {@link DoubleValueParser} to parse
 * values. The behavior of this parser is then:
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
public class BigDecimalValueParser extends AbstractSingleCellValueParser<BigDecimal> {
    private final DoubleValueParser doubleValueParser = new DoubleValueParser();
    @Override
    protected BigDecimal getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected BigDecimal getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        final Double doubleValue = doubleValueParser.getValueForCell(value, rowIndex, colIndex, worksheet);
        return transformToBigDecimal(doubleValue);
    }

    @Override
    protected BigDecimal getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(value, BigDecimal.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected BigDecimal getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        try {
            final Double doubleValue = doubleValueParser.getValueForCell(value, rowIndex, colIndex, worksheet);
            return transformToBigDecimal(doubleValue);
        } catch (IncorrectValueForTypeException exc) {
            throw new IncorrectValueForTypeException(value, BigDecimal.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
    }

    private BigDecimal transformToBigDecimal(Double doubleValue) {
        return BigDecimal.valueOf(doubleValue);
    }
}
