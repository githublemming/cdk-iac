/*
 * MIT License
 *
 * Copyright (c) 2018 Mark Haskins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.MIT License
 */

package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import software.amazon.awscdk.App;

/**
 * Creates and InfluxDb Stack
 */
public class InfluxDb  extends CdkIacTemplate {

    private InfluxDb() throws TemplateException {
        super();
    }

    @Override
    protected void defineStacks(App app) throws MissingPropertyException, StackException {
        new io.haskins.cdkiac.stack.application.InfluxDb(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {

        try {
            new InfluxDb();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}
