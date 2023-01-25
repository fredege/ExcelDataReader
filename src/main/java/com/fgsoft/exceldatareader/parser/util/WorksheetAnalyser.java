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
import com.fgsoft.exceldatareader.parser.HeaderDescriptor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

/**
 * This class is responsible to analyze an Excel worksheet in order to:
 * <ul>
 *     <li>Collect headers matching </li>
 *     <li>Locate the cell where to get a value to parse</li>
 * </ul>
 */
@RequiredArgsConstructor
public class WorksheetAnalyser {
    private final Sheet worksheet;
    private final HeaderDescriptor headerDescriptor;

    /**
     * Scan the worksheet in order to get the map of headers and fields of the object to get value from parsing
     * @return Map of object fields headers and columns in worksheet.
     */
    public Map<String, Integer> getHeadersMap() {
        final Map<String, Integer> retMap = new HashMap<>();
        final List<Row> titleRows = buildTitleRows();
        final Row largestTitleRow = findLargestTitleRow(titleRows);
        for (Cell cell : largestTitleRow) {
            final int columnIndex = cell.getColumnIndex();
            if (columnIndex > 0) { // First column is assigned to the test name
                final String headerValue = computeHeader(columnIndex, titleRows).trim();
                retMap.put(headerValue, columnIndex);
            }
        }
        return retMap;
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
        } else {
            throw new ExcelReaderException(ExcelReaderErrorCode.UNKNOWN);
        }
    }

    private String computeHeader(int columnIndex, List<Row> titleRows) {
        final List<String> tmpHeaders = new ArrayList<>(titleRows.size());
        for (final Row row : titleRows) {
            final Cell combinedCell = getCellWithMerge(row.getRowNum(), columnIndex);
            if (combinedCell != null && CellType.BLANK != combinedCell.getCellType()) {
                final String titleHeader = combinedCell.getStringCellValue();
                tmpHeaders.add(0, titleHeader);
            }
        }
        return StringUtils.join(tmpHeaders, '.');
    }

    /**
     * Returns the top left cell corresponding to the given coordinates. This allows to get the contents for coordinates
     * even if it corresponds to a merged region.
     * @param rowIndex index of the row
     * @param columnIndex index of the column
     * @return cell to get the value from
     */
    private Cell getCellWithMerge(int rowIndex, int columnIndex) {
        final Cell cell;
        if (rowIndex >= 0) {
            int topRow = rowIndex;
            int firstColumn = columnIndex;
            final List<CellRangeAddress> merges = worksheet.getMergedRegions();
            for  (CellRangeAddress merge : merges) {
                if (merge.isInRange(rowIndex, columnIndex)) {
                    topRow = merge.getFirstRow();
                    firstColumn = merge.getFirstColumn();
                    break;
                }
            }
            cell = worksheet.getRow(topRow).getCell(firstColumn);
        } else {
            cell = null;
        }
        return  cell;
    }
}
