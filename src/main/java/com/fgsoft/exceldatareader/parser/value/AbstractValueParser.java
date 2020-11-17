/*
 * Copyright 2020 Frederic GEDIN
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

import com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode;
import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Generic value parser. This class contains the common code to the different types of value parser.
 *
 * @param <T> type of data to be parsed
 */
@Slf4j
@RequiredArgsConstructor
@Getter
abstract class AbstractValueParser<T> {
    /**
     * Parse the value for a given cell
     *
     * @param cell      Cell to parse
     * @param evaluator Formula evaluator call in case of cell containing a formula
     */
    public T getValue(final Cell cell, @NonNull FormulaEvaluator evaluator) {
        T value;
        if (cell == null) {
            value = getValueForNullCell();
        } else {
            final int rowIndex = cell.getRowIndex();
            final int colIndex = cell.getColumnIndex();
            final Sheet worksheet = cell.getSheet();
            try {
                switch (cell.getCellType()) {
                    case FORMULA:
                        value = getValueFromFormula(cell, evaluator);
                        break;
                    case BLANK:
                        value = getValueForEmptyCell(rowIndex, colIndex, worksheet);
                        break;
                    case NUMERIC:
                        value = getValueForCell(cell.getNumericCellValue(), rowIndex, colIndex, worksheet);
                        break;
                    case BOOLEAN:
                        value = getValueForCell(cell.getBooleanCellValue(), rowIndex, colIndex, worksheet);
                        break;
                    case STRING:
                        value = getValueForCell(cell.getStringCellValue(), rowIndex, colIndex, worksheet);
                        break;
                    case ERROR:
                    default:
                        reportError(rowIndex, colIndex, worksheet);
                        value = null;
                }
            } catch (IllegalStateException exc) {
                value = null;
                reportError(rowIndex, colIndex, worksheet);
            }
        }
        return value;
    }

    /**
     * @param rowIndex  row index
     * @param colIndex  column index
     * @param worksheet current worksheet
     * @return the value for this parser in case of an empty cell
     */
    protected abstract T getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet);

    /**
     * @param rowIndex  row index
     * @param colIndex  column index
     * @param worksheet current worksheet
     * @return the value for this parser in case of a numeric cell
     */
    protected abstract T getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet);

    /**
     * @param rowIndex  row index
     * @param colIndex  column index
     * @param worksheet current worksheet
     * @return the value for this parser in case of a boolean cell
     */
    protected abstract T getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet);

    /**
     * @param rowIndex  row index
     * @param colIndex  column index
     * @param worksheet current worksheet
     * @return the value for this parser in case of a string cell
     */
    protected abstract T getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet);

    protected abstract T getValueForNullCell();

    private T getValueFromFormula(Cell cell, FormulaEvaluator evaluator) {
        T value;
        final int rowIndex = cell.getRowIndex();
        final int colIndex = cell.getColumnIndex();
        final Sheet worksheet = cell.getSheet();
        try {
            final CellValue cellValue = evaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case BLANK:
                    value = getValueForEmptyCell(rowIndex, colIndex, worksheet);
                    break;
                case NUMERIC:
                    value = parseNumericCell(cellValue, rowIndex, colIndex, worksheet);
                    break;
                case BOOLEAN:
                    value = getValueForCell(cellValue.getBooleanValue(), rowIndex, colIndex, worksheet);
                    break;
                case STRING:
                    value = getValueForCell(cellValue.getStringValue(), rowIndex, colIndex, worksheet);
                    break;
                default:
                    value = null;
                    reportError(rowIndex, colIndex, worksheet);
            }
        } catch (IllegalStateException exc) {
            value = null;
            reportError(rowIndex, colIndex, worksheet);
        }
        return value;
    }

    private T parseNumericCell(final CellValue cellValue,
                               final int rowIndex, final int colIndex, final Sheet worksheet) {
        final T value;
        final T tmpDblValue = getValueForCell(cellValue.getNumberValue(),
                rowIndex, colIndex, worksheet);
        if (tmpDblValue instanceof String && "0.0".equals(tmpDblValue)) {
            value = null;
        } else {
            value = tmpDblValue;
        }
        return value;
    }

    private void reportError(final int rowIndex, final int colIndex, final Sheet worksheet) {
        final String name = worksheet.getSheetName();
        throw new ExcelReaderException(ExcelReaderErrorCode.UNEXPECTED_VALUE, rowIndex, colIndex, name);
    }

}
