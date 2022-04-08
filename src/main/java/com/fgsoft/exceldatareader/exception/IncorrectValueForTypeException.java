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
package com.fgsoft.exceldatareader.exception;

import static com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE;

public class IncorrectValueForTypeException extends ExcelReaderException{
    public IncorrectValueForTypeException(Throwable exc, Object... args) {
        super(exc, INCORRECT_VALUE_FOR_TYPE, args);
    }
}
