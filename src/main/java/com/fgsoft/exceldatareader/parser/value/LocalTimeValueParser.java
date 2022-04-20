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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;


/**
 * Parseable cells for LocalTime should meet one of the following condition:
 * <ul>
 *     <li>The cell content is numeric formatted as a time</li>
 *     <li>The cell content is the string representing a LocalTime formatted as per ISO format</li>
 * </ul>
 * Time in Excel is represented as double value but formatted as time. Therefore, only cells that  meet
 * these criteria will be accepted.
 */
public class LocalTimeValueParser extends AbstractSingleCellValueParser<LocalTime> {
    @Override
    protected LocalTime getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected LocalTime getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        final LocalTime localTime;
        final Row row = worksheet.getRow(rowIndex);
        final Cell cell = row.getCell(colIndex);
        if (DateUtil.isCellDateFormatted(cell)) {
            final LocalDateTime localDateTime = cell.getLocalDateTimeCellValue();
            localTime = localDateTime.toLocalTime();
        } else {
            throw new IncorrectValueForTypeException(null, value, LocalTime.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return localTime;
    }

    @Override
    protected LocalTime getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, value, LocalTime.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected LocalTime getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        final LocalTime localTime;
        try {
            localTime = LocalTime.parse(value);
        } catch (DateTimeParseException exc) {
            throw new IncorrectValueForTypeException(exc, value, LocalTime.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return localTime;
    }
}
