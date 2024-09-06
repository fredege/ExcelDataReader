/* Copyright 2024 Frederic GEDIN
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
package com.fgsoft.exceldatareader.builder;

import com.fgsoft.exceldatareader.reader.ExcelDataReader;

import java.util.List;

public abstract class AbstractDataBuilder {
    private final ExcelDataReader reader;

    protected AbstractDataBuilder(final String fileName) {
        this.reader = new ExcelDataReader(fileName);
    }

    public <T> T build(final Class<T> clazz, final String testName, final String sheetName, final String... ignore) {
        return build(clazz, testName, sheetName, false, ignore);
    }

    public <T> T build(final Class<T> clazz, final String testName, final String sheetName, boolean skipMissingHeader, final String... ignore) {
        return reader.getTestData(clazz, testName, sheetName, skipMissingHeader, ignore);
    }

    public <T> List<T> buildAll(final Class<T> clazz, final String sheetNamer, final String... ignore) {
        return buildAll(clazz, sheetNamer, false, ignore);
    }


    public <T> List<T> buildAll(final Class<T> clazz, final String sheetName, final boolean skipMissingHeader, final String... ignore) {
        return reader.getAllTestData(clazz, sheetName, skipMissingHeader, ignore);
    }

}
