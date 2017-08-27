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
package inf2b.algobench.model;

import java.io.*;

/**
 *
 * @author eziama ubachukwu
 */
public final class ClientPipe {

    RandomAccessFile rafPipe = null;

    public ClientPipe() {
        StartClient();
    }

    public void StopClient() {
        try {
            rafPipe.close();
        }
        catch (IOException e) {
            System.out.println("IOException while closing the pipe streams");
            e.printStackTrace();
        }
        rafPipe = null;
    }

    public void StartClient() {
        //create the pipe
        try {
            rafPipe = new RandomAccessFile("awb_pipe", "rws");
        }
        catch (FileNotFoundException e) {
            System.out.println("FileNotFound exception while creating the pipe...");
            e.printStackTrace();
        }
    }

    public boolean write(byte[] m_pTxDataBuffer, short totalpktLength) {
        boolean wSuccess = false;
        try {
            rafPipe.write(m_pTxDataBuffer, 0, totalpktLength);
            wSuccess = true;
        }
        catch (IOException e) {
            System.out.println("IOException while writing to the pipe...");
            e.printStackTrace();
        }
        return wSuccess;
    }

    public int Read(byte[] pchBuff, int inBufferSize) {
        int byteTransfer = 0;
        try {
            byteTransfer = rafPipe.read(pchBuff, 0, inBufferSize);
        }
        catch (IOException e) {
            System.out.println("IO Exception while reading from the pipe.");
            e.printStackTrace();
        }
        return byteTransfer;
    }
}
