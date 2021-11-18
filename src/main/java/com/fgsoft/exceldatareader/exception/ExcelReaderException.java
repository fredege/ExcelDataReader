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

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelReaderException extends RuntimeException {
    public ExcelReaderException(@NonNull final ExcelReaderErrorCode code, final Object... args) {
        this(null, code, args);
    }

    public ExcelReaderException(final Throwable cause, final ExcelReaderErrorCode code,
                                final Object... args) {
        super(String.format(code.getMessage(), args));
        if (cause != null) {
            this.initCause(cause);
        }
        log.error(this.getMessage());
    }

}
