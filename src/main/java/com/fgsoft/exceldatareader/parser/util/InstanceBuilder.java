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
package com.fgsoft.exceldatareader.parser.util;

import com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode;
import com.fgsoft.exceldatareader.exception.ExcelReaderException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * This class is responsible for building any new object instance.
 */
public class InstanceBuilder {
    public static <T> T buildInstance(Class<T> clazz) {
        final T instance;
        final Optional<Method> builderMethodOption = getBuilder(clazz);
        if (!BeanAnalyzer.hasNonNullField(clazz) && builderMethodOption.isPresent()) {
            instance = getInstanceFromBuilder(builderMethodOption.get());
        } else {
            instance = getInstanceFromConstructor(clazz);
        }
        return instance;
    }

    private static <T> T getInstanceFromConstructor(Class<T> clazz) {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exc) {
            throw new ExcelReaderException(exc, ExcelReaderErrorCode.UNKNOWN);
        }
    }

    @SuppressWarnings({"unchecked", "java:S3011"})
    private static <T> T getInstanceFromBuilder(Method method) {
        try {
            final Object builder = method.invoke(null);
            final Method[] allMethods = builder.getClass().getDeclaredMethods();
            final Optional<Method> optionalBuilder = Arrays.stream(allMethods)
                    .filter(item -> "build".equals(item.getName()))
                    .findFirst();
            if (optionalBuilder.isPresent()) {
                final Method buildMethod = optionalBuilder.get();
                buildMethod.setAccessible(true);
                return (T) buildMethod.invoke(builder);
            } else {
                throw new ExcelReaderException(ExcelReaderErrorCode.UNKNOWN);
            }
        } catch (IllegalAccessException | InvocationTargetException exc) {
            throw new ExcelReaderException(exc, ExcelReaderErrorCode.UNKNOWN);
        }
    }

    private static <T> Optional<Method> getBuilder(Class<T> clazz) {
        final Method[] allMethods = clazz.getMethods();
        return Arrays.stream(allMethods).filter(method -> "builder".equals(method.getName())).findFirst();
    }

    private InstanceBuilder() {
        // prevents instantiation
    }
}
