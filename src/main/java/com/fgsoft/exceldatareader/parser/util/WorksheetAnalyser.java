/* Copyright 2023 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.util;

import com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode;
import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import com.fgsoft.exceldatareader.exception.HeaderNotFoundException;
import com.fgsoft.exceldatareader.exception.TestNotFoundException;
import com.fgsoft.exceldatareader.parser.HeaderDescriptor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

/**
 * This class is responsible to analyze an Excel worksheet in order to:
 * <ul>
 *     <li>Collect headers matching </li>
 *     <li>Locate the cell where to get a value to parse</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public class WorksheetAnalyser {
    private final Sheet worksheet;
    private final HeaderDescriptor headerDescriptor;
    private final FormulaEvaluator formulaEvaluator;

    /**
     * Scan the worksheet in order to get the range of cells corresponding to the data for the given test name.
     *
     * @return range of cells
     */
    public CellRangeAddress findTestDataRange(@NotBlank final String testName) {
        final int lastRowNum = worksheet.getLastRowNum();
        final int firstTestRowNum = findFirstTestRowNum(testName);
        final int lastTestRowNum = findLastTestRowNum(firstTestRowNum, lastRowNum);
        final int lastTestColumnNum = findLargestTitleRow(buildTitleRows()).getLastCellNum() - 1;
        final CellRangeAddress tmpRange =  new CellRangeAddress(firstTestRowNum, lastTestRowNum,
                1, lastTestColumnNum);
        return removeTrailingEmptyRows(tmpRange);
    }

    public CellRangeAddress getMainHeaderRange() {
        final CellRangeAddress testNameRange = getMergedCell(0, 0);
        final int firstHeaderRowNum = testNameRange.getFirstRow();
        final int lastHeaderRowNum = testNameRange.getLastRow() - headerDescriptor.getLastHeaderRow() + headerDescriptor.getLastTitleRow();
        final int firstHeaderColumnNum = testNameRange.getLastColumn() + 1;
        final int lastHeaderColumnNum = findLargestTitleRow(firstHeaderRowNum, lastHeaderRowNum) -1;
        return new CellRangeAddress(firstHeaderRowNum, lastHeaderRowNum,
                firstHeaderColumnNum, lastHeaderColumnNum);
    }

    private int findLargestTitleRow(int first, int last) {
        int largestTitleRowNum = 0;
        for (int cnt = first; cnt <= last; cnt++) {
            int width = worksheet.getRow(cnt).getLastCellNum();
            if (width > largestTitleRowNum) {
                largestTitleRowNum = width;
            }
        }
        return largestTitleRowNum;
    }


    public Cell getCell(@NotBlank String name, @NotNull CellRangeAddress cellRange, @NotNull CellRangeAddress headerRange) {
        final Row headerRow = worksheet.getRow(headerRange.getFirstRow());
        final Row valueRow = worksheet.getRow(cellRange.getFirstRow());
        final Iterator<Cell> headerIterator = headerRow.cellIterator();
        final int columnIndex;
        while (headerIterator.hasNext()) {
            final Cell headerCell = headerIterator.next();
            if (name.equals(headerCell.getStringCellValue())) {
                columnIndex = headerCell.getColumnIndex();
                return valueRow.getCell(columnIndex);
            }
        }
        throw new HeaderNotFoundException(name);
    }

    /**
     * Extracts the address of the Excel region containing the test data corresponding to a composite field.
     *
     * @param name        name of the field
     * @param cellRange   address of the region containing the encompassing object test data
     * @param headerRange address of the region containing the headers corresponding to the encompassing object test data
     * @return computed address of the Excel region containing test data for the composite field
     */
    public CellRangeAddress getCellRange(String name, CellRangeAddress cellRange, CellRangeAddress headerRange) {
        final int firstColumn = findFirstColumn(name, headerRange);
        final int firstHeaderRow = headerRange.getFirstRow();
        final CellRangeAddress headerCell = getMergedCell(firstHeaderRow, firstColumn);
        final CellRangeAddress foundRange = new CellRangeAddress(cellRange.getFirstRow(), cellRange.getLastRow(),
                headerCell.getFirstColumn(), headerCell.getLastColumn());
        return removeTrailingEmptyRows(foundRange);
    }

    public CellRangeAddress getHeaderRange(String name, CellRangeAddress headerRange) {
        final int firstColumn = findFirstColumn(name, headerRange);
        final int firstHeaderRow = headerRange.getFirstRow();
        final CellRangeAddress headerCell = getMergedCell(firstHeaderRow, firstColumn);
        if (headerCell.getNumberOfCells() > 1) {
            return new CellRangeAddress(headerRange.getFirstRow() + 1, headerRange.getLastRow(),
                    headerCell.getFirstColumn(), headerCell.getLastColumn());
        } else {
            return headerCell;
        }
    }

    private List<Row> buildTitleRows() {
        final int firstTitleRow = headerDescriptor.getFirstTitleRow();
        final int lastTitleRow = headerDescriptor.getLastTitleRow();
        final List<Row> titleRows = new ArrayList<>(lastTitleRow - firstTitleRow + 1);
        for (int cnt = firstTitleRow; cnt <= lastTitleRow; cnt++) {
            titleRows.add(worksheet.getRow(cnt));
        }
        Collections.reverse(titleRows);
        return titleRows;
    }

    private Row findLargestTitleRow(List<Row> titleRows) {
        final Optional<Row> largestRow = titleRows.stream().max(Comparator.comparingInt(Row::getPhysicalNumberOfCells));
        if (largestRow.isPresent()) {
            return largestRow.get();
        } else { // Should never happen
            throw new ExcelReaderException(ExcelReaderErrorCode.UNKNOWN);
        }
    }

    /**
     * Return the merged cell encompassing the given cell row and column
     *
     * @param rowNum row number
     * @param columnNum column number
     * @return corresponding cell range address
     */
    private CellRangeAddress getMergedCell(int rowNum, int columnNum) {
        final List<CellRangeAddress> merges = worksheet.getMergedRegions();
        for (CellRangeAddress merge : merges) {
            if (merge.isInRange(rowNum, columnNum)) {
                return merge;
            }
        }
        return new CellRangeAddress(rowNum, rowNum, columnNum, columnNum);
    }

    private int findFirstTestRowNum(String testName) {
        for (Row row : worksheet) {
            final Cell testNameCell = row.getCell(0);
            if (testNameCell != null && testName.equals(testNameCell.getStringCellValue())) {
                return row.getRowNum();
            }
        }
        throw new TestNotFoundException(testName);
    }

    private int findLastTestRowNum(int firstTestRowNum, int lastRowNum) {
        for (Row row : worksheet) {
            if (row.getRowNum() > firstTestRowNum &&
                    row.getCell(0) != null &&
                    row.getCell(0).getCellType() != CellType.BLANK) {
                return row.getRowNum() - 1;
            }
        }
        return lastRowNum;
    }

    private int findFirstColumn(String name, CellRangeAddress headerRange) {
        for (int index = headerRange.getFirstColumn(); index <= headerRange.getLastColumn(); index++) {
            final Cell headerCell = worksheet.getRow(headerRange.getFirstRow()).getCell(index);
            if (name.equals(headerCell.getStringCellValue())) {
                return index;
            }
        }
        throw new HeaderNotFoundException(name);
    }

    private CellRangeAddress removeTrailingEmptyRows(CellRangeAddress range) {
        if (hasMoreThanOneLine(range) && hasLastLineEmpty(range)) {
            final CellRangeAddress tmpRange = new CellRangeAddress(range.getFirstRow(), range.getLastRow() -1,
                    range.getFirstColumn(), range.getLastColumn());
            return removeTrailingEmptyRows(tmpRange);
        } else {
            return range;
        }
    }

    private boolean hasMoreThanOneLine(CellRangeAddress range) {
        return range.getLastRow() > range.getFirstRow();
    }

    private boolean hasLastLineEmpty(CellRangeAddress range) {
        final Row row = worksheet.getRow(range.getLastRow());
        for (int index = range.getFirstColumn(); index <= range.getLastColumn(); index++) {
                if (row.getCell(index) != null && row.getCell(index).getCellType() != CellType.BLANK) {
                    return false;
                }
            }
        return true;
    }

}
