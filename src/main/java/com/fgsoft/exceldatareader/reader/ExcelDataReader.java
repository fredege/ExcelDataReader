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
package com.fgsoft.exceldatareader.reader;

import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import com.fgsoft.exceldatareader.parser.HeaderDescriptor;
import com.fgsoft.exceldatareader.parser.object.ObjectParserRouter;
import com.fgsoft.exceldatareader.parser.object.TestDataParser;
import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.InputStream;

import static com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode.FILE_NOT_FOUND;
import static com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode.UNABLE_TO_OPEN_FILE;

/**
 * Reader used to retrieve test data from excel file.
 *
 */
public class ExcelDataReader {
    private final Workbook workbook;
    private final FormulaEvaluator formulaEvaluator;

    public ExcelDataReader(final String filename) {
        this.workbook = getWorkbook(filename);
        this.formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
    }

    private Workbook getWorkbook(String filename) {
        final InputStream inputStream = this.getClass().getResourceAsStream(filename);
        if (inputStream == null) {
            throw new ExcelReaderException(FILE_NOT_FOUND, filename);
        }
        try {
            return WorkbookFactory.create(inputStream);
        } catch (IOException exc) {
            throw new ExcelReaderException(exc, UNABLE_TO_OPEN_FILE, filename);
        }
    }

    public <T> T getTestData(final Class<T> dataType, final String testName, final String sheetName, HeaderDescriptor headerDescriptor) {
        assert(workbook != null);
        final Sheet sheet = workbook.getSheet(sheetName);
        final WorksheetAnalyser worksheetAnalyser = new WorksheetAnalyser(sheet, headerDescriptor, formulaEvaluator);
        final CellRangeAddress headerRange = worksheetAnalyser.getMainHeaderRange();
        final CellRangeAddress dataRange = worksheetAnalyser.findTestDataRange(testName);
        final TestDataParser<T> parser = ObjectParserRouter.findParser(dataType, dataRange, headerRange);
        return parser.parse(worksheetAnalyser, dataType);
    }
}
