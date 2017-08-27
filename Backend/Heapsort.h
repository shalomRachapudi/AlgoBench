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
/*
* File:   Heapsort.h
* Author: eziama ubachukwu
*
* Created on June 8, 2015, 9:06 AM
*/

#ifndef HEAPSORT_H
#define HEAPSORT_H

#pragma once
#include "stdafx.h"
#include "global.h"
#include "Algorithm.h"

namespace inf2b
{
    class Heapsort 
    {
    private:
        long m_cursor;
        InputVectorType& m_input;

        void buildHeap();
         
        void hsort();
         
        void heapify( long nodeIndex );

        bool hasLeftChild( long nodeIndex );
        bool hasRightChild( long nodeIndex );
        long getLeftChild( long index );
        long getRightChild( long index ); 
        long getParent( long index );

        void swap( long indexX, long indexY );

    public:

        Heapsort( InputVectorType& v ) : m_cursor( 0 ), m_input( v ){}
        ~Heapsort() {}

        void operator()();
    };
}

#endif
