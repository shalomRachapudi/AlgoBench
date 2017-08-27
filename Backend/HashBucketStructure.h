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

#ifndef HASHBUCKETSTRUCTURE_H
#define HASHBUCKETSTRUCTURE_H
#pragma once
#include "stdafx.h"
#include "global.h"

namespace inf2b
{
    class HashBucketStructure 
    {
    public:
        HashBucketStructure( size_t& s, size_t&& numElems ) : m_arraySize( s ), m_numElements( numElems ) {
            // MS VS 2013 C++11 compiler bug (error C2979) doesn't allow list initialization
            // inside member initializer list, hence the workaround here.
            m_hashBucket.resize( s );
        }

        void addEntry( size_t hashValue, std::pair<std::string, std::string>&& entry );

        void addEntry( size_t hashValue, std::pair<std::string, std::string>& entry );

        size_t size();
        
        void printBucket( std::ostream& outputStream );

        HashBucketStructure( const HashBucketStructure& rhs ) = delete;
        HashBucketStructure( HashBucketStructure&& rhs ) = delete;
        HashBucketStructure& operator=( const HashBucketStructure& rhs ) = delete;
        HashBucketStructure& operator=( HashBucketStructure&& rhs ) = delete;
        ~HashBucketStructure() {}

    private:
        size_t m_arraySize;
        size_t m_numElements;
        std::vector< HashDictionaryType > m_hashBucket;
    };
}

#endif
