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
package com.fgsoft.exceldatareader.parser;

import java.util.List;

/**
 * Common interface for all parsers
 * @param <T> type of objet to be parsed
 */
public interface ExcelFileParser<T> {
    /**
     * Get an object value
     * @param testName reference name as given in the first column of the worksheet
     * @return object read from parsing
     */
    T getValue(String testName);

    /**
     * Get all the test names of the worksheet
     */
    List<String> getAllTestNames();
}
