/* Copyright 2022 Frederic GEDIN
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;


/**
 * Parseable cells for LocalDateTime should meet one of the following condition:
 * <ul>
 *     <li>The cell content is numeric formatted as a date</li>
 *     <li>The cell content is the string representing a LocalDateTime formatted as per ISO format</li>
 * </ul>
 * Date and time in Excel are represented as double value but formatted as date. Therefore, only cells that  meet
 * these criteria will be accepted.
 */
public class LocalDateTimeValueParser extends AbstractSingleCellValueParser<LocalDateTime> {
    @Override
    protected LocalDateTime getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected LocalDateTime getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        final LocalDateTime localDateTime;
        final Row row = worksheet.getRow(rowIndex);
        final Cell cell = row.getCell(colIndex);
        if (DateUtil.isCellDateFormatted(cell)) {
            localDateTime = cell.getLocalDateTimeCellValue();
        } else {
            throw new IncorrectValueForTypeException(null, value, LocalDateTime.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return localDateTime;
    }

    @Override
    protected LocalDateTime getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, value, LocalDateTime.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected LocalDateTime getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        final LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(value);
        } catch (DateTimeParseException exc) {
            throw new IncorrectValueForTypeException(exc, value, LocalDateTime.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return localDateTime;
    }
}
