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
package com.fgsoft.exceldatareader.parser.object;

import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;

public interface TestDataParser<T> {
    /**
     * Build and populate an object using data from a worksheet.
     * @param worksheetAnalyser worksheet analyzer
     * @param clazz class of the object to instantiate and populate
     * @return built instance
     */
    T parse(final WorksheetAnalyser worksheetAnalyser, Class<T> clazz);
}
