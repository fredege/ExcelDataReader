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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode.*;

/**
 * Reader used to retrieve test data from excel file.
 */
public class ExcelDataReader {
    private final Workbook workbook;
    private final FormulaEvaluator formulaEvaluator;
    private final Map<Class<?>, String> sheetNames = new HashMap<>();

    public ExcelDataReader(final String filename) {
        this.workbook = getWorkbook(filename);
        this.formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
    }

    public void register(Class<?> type, String sheetName) {
        sheetNames.put(type, sheetName);
    }

    public <T> T getTestData(final Class<T> dataType, final String testName, boolean skipMissingHeader, String... ignore) {
        assert (workbook != null);
        final String sheetName = sheetNames.get(dataType);
        final Sheet sheet = workbook.getSheet(sheetName);
        final WorksheetAnalyser worksheetAnalyser = new WorksheetAnalyser(sheet, formulaEvaluator);
        final CellRangeAddress headerRange = worksheetAnalyser.getMainHeaderRange();
        final CellRangeAddress dataRange = worksheetAnalyser.findTestDataRange(testName);
        final TestDataParser<T> parser = ObjectParserRouter.findParser(dataType, dataRange, headerRange);
        return parser.parse(this, worksheetAnalyser, dataType, skipMissingHeader, ignore);
    }

    public <T> List<T> getAllTestData(Class<T> dataType, boolean skipMissingHeader, String... ignore) {
        assert (workbook != null);
        final String sheetName = sheetNames.get(dataType);
        final Sheet sheet = workbook.getSheet(sheetName);
        final WorksheetAnalyser worksheetAnalyser = new WorksheetAnalyser(sheet, formulaEvaluator);
        final CellRangeAddress headerRange = worksheetAnalyser.getMainHeaderRange();
        final List<String> testNames = worksheetAnalyser.getAllTestNames();
        return testNames.stream().map(name -> {
                    final CellRangeAddress dataRange = worksheetAnalyser.findTestDataRange(name);
                    final TestDataParser<T> parser = ObjectParserRouter.findParser(dataType, dataRange, headerRange);
                    return parser.parse(this, worksheetAnalyser, dataType, skipMissingHeader, ignore);
                })
                .toList();
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

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> getRegisteredClass(String className) {
        final Class<?> clazz = sheetNames.keySet().stream()
                .filter(c -> c.getSimpleName().equals(className))
                .findFirst().orElseThrow(() -> new ExcelReaderException(WORKSHEET_NOT_FOUND, className));
        return (Class<? extends T>) clazz;
    }
}
