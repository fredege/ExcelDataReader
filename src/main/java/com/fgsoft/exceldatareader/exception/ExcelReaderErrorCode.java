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
package com.fgsoft.exceldatareader.exception;

public enum ExcelReaderErrorCode {
    INCORRECT_VALUE_FOR_TYPE("Incorrect value '%s' for type '%s' in cell (%d, %d) of sheet '%s'" ),
    INVALID_TYPE("Unable to find a single cell value parser for type '%s'"),
    UNEXPECTED_VALUE("Unexpected value in cell (%d, %d) of sheet '%s'"),
    UNKNOWN("Unknown error code");

    private final String message;

    ExcelReaderErrorCode(final String aMessage) {
        this.message = aMessage;
    }

    public String getMessage() {
        return message;
    }
}
