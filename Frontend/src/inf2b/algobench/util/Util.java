/*
 * The MIT License
 *
 * Copyright 2015 Eziama Ubachukwu (eziama.ubachukwu@gmail.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package inf2b.algobench.util;

/**
 * Utility methods to help others.
 *
 * @author eziama
 */
public class Util {

    public static String getElapsedTime(long referenceTime, boolean isFormatted, boolean isAbbreviated) {
        Long elaspedTime = (System.currentTimeMillis() - referenceTime) / 1000;
        if (!isFormatted) {
            return elaspedTime.toString();
        }
        String feedback = "";
        long minute = 60;
        long hour = minute * 60;
        long day = hour * 24;
        long week = day * 7;
        long month = day * 30;
        long year = month * 12;

        if (elaspedTime > year) {
            long num = elaspedTime / year;
            feedback += isAbbreviated ? num + "y" : num > 1 ? num + " years" : num + " year";
            feedback += " ";
            elaspedTime = elaspedTime % year;
        }
        if (elaspedTime > month) {
            long num = elaspedTime / month;
            feedback += isAbbreviated ? num + "mon" : num > 1 ? num + " months" : num + " month";
            elaspedTime = elaspedTime % month;
        }
        if (elaspedTime > week) {
            long num = elaspedTime / week;
            feedback += isAbbreviated ? num + "w" : num > 1 ? num + " weeks" : num + " week";
            elaspedTime = elaspedTime % week;
        }
        if (elaspedTime > day) {
            long num = elaspedTime / day;
            feedback += isAbbreviated ? num + "d" : num > 1 ? num + " days" : num + " day";
            feedback += " ";
            elaspedTime = elaspedTime % day;
        }
        if (elaspedTime > hour) {
            long num = elaspedTime / hour;
            feedback += isAbbreviated ? num + "h" : num > 1 ? num + " hours" : num + " hour";
            feedback += " ";
            elaspedTime = elaspedTime % hour;
        }
        if (elaspedTime > minute) {
            long num = elaspedTime / minute;
            feedback += isAbbreviated ? num + "m" : num > 1 ? num + " minutes" : num + " minute";
            feedback += " ";
            elaspedTime = elaspedTime % minute;
        }
        if (elaspedTime > 0) {
            feedback += isAbbreviated ? elaspedTime + "s" : elaspedTime > 1 ? elaspedTime + " seconds" : elaspedTime + " second";
        }
        return feedback;
    }
}
