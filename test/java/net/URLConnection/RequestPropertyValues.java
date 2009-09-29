/*
 * Copyright 2002-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * @test
 * @bug 4644775 6230836
 * @summary Test URLConnection Request Proterties
 */

import java.net.URL;
import java.net.URLConnection;

/**
 * Part1:
 *   bug 4644775: Unexpected NPE in setRequestProperty(key, null) call
 * Part2:
 *   bug 6230836: A few methods of class URLConnection implemented incorrectly
 */

public class RequestPropertyValues {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    public static void part1() throws Exception {
        URL[] urls = { new URL("http://localhost:8088"),
                        new URL("file:/etc/passwd"),
                        new URL("ftp://foo:bar@foobar.com/etc/passwd"),
                        new URL("jar:http://foo.com/bar.html!/foo/bar")
                    };

        boolean failed = false;

        for (int proto = 0; proto < urls.length; proto++) {
            URLConnection uc = (URLConnection) urls[proto].openConnection();
            try {
                uc.setRequestProperty("TestHeader", null);
            } catch (NullPointerException npe) {
                System.out.println("setRequestProperty is throwing NPE" +
                                " for url: " + urls[proto]);
                failed = true;
            }
            try {
                uc.addRequestProperty("TestHeader", null);
            } catch (NullPointerException npe) {
                System.out.println("addRequestProperty is throwing NPE" +
                                " for url: " + urls[proto]);
                failed = true;
            }
        }
        if (failed) {
            throw new Exception("RequestProperty setting/adding is" +
                                " throwing NPE for null values");
        }
    }

    public static void part2() throws Exception {
        URL url = null;
        String[] goodKeys = {"", "$", "key", "Key", " ", "    "};
        String[] goodValues = {"", "$", "value", "Value", " ", "    "};

        URLConnection conn = getConnection(url);

        for (int i = 0; i < goodKeys.length; ++i) {
            for (int j = 0; j < goodValues.length; ++j) {
                // If a property with the key already exists, overwrite its value with the new value
                conn.setRequestProperty(goodKeys[i], goodValues[j]);
                String value = conn.getRequestProperty(goodKeys[i]);

                if (!((goodValues[j] == null && value == null) || (value != null && value.equals(goodValues[j]))))
                    throw new RuntimeException("Method setRequestProperty(String,String) works incorrectly");
            }
        }
    }

    static URLConnection getConnection(URL url) {
        return new DummyURLConnection(url);
    }

    static class DummyURLConnection extends URLConnection {

        DummyURLConnection(URL url) {
            super(url);
        }

        public void connect() {
            connected = true;
        }
    }

}
