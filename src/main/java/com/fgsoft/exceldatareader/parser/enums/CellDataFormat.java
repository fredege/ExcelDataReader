/* Copyright 2025 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum CellDataFormat {
    GENERIC((short) 0),
    NUMBER_2((short) 2),
    FORMAT_CURRENCY_1((short) 7),
    FORMAT_CURRENCY_2((short) 8),
    FORMAT_ACCOUNTING_1((short) 42),
    FORMAT_ACCOUNTING_2((short) 164),
    ;
    private final short code;

    public static CellDataFormat getFromCode(final Short aCode) {
        return Arrays.stream(CellDataFormat.values())
                .filter(e -> e.code == aCode)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported format %d.", aCode)));
    }
}
