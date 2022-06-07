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
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateValueParser extends AbstractSingleCellValueParser<Date> {
    @Override
    protected Date getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected Date getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        final Date date;
        final Row row = worksheet.getRow(rowIndex);
        final Cell cell = row.getCell(colIndex);
        if (DateUtil.isCellDateFormatted(cell)) {
            date = cell.getDateCellValue();
        } else {
            throw new IncorrectValueForTypeException(null, value, Date.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return date;
    }

    @Override
    protected Date getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, value, Date.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected Date getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        final Date date;
        try {
            final LocalDateTime localDateTime = LocalDateTime.parse(value);
            date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException exc) {
            throw new IncorrectValueForTypeException(exc, value, Date.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return date;
    }
}
