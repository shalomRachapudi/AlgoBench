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
 * This interface defines the signature of a thread that can notify another
 * thread of completing a task. E.g. implementer is TaskMaster class.
 * 
 * @author eziama ubachukwu
 */
public interface ITaskCompleteNotifier {

    /**
     * Adds the specified listener to the notification list
     * @param listener 
     */
    public void addListener(ITaskCompleteListener listener);

    /**
     * Removes the specified listener from the notification list
     * @param listener 
     */
    public void removeListener(ITaskCompleteListener listener);

    /**
     * Notifies those in the list
     */
    public void notifyListeners();
}
