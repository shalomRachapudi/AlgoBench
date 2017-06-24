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

#ifndef HASHING_H
#define HASHING_H

#pragma once
#include "stdafx.h"
#include "global.h"
#include "HashBucketStructure.h"

namespace inf2b
{
    class Hashing 
    {
    public:
        // TODO: allow custom functions to be supplied from frontend
        Hashing( PairList& input, size_t bucketSize, int hashType, std::string& output )
            : m_input(input), 
              m_bucket( bucketSize, input.size()),
              m_hashType( hashType ),
              m_output( output ) 
            {}

        Hashing( Hashing& rhs ) = delete;
        ~Hashing() { m_outputFile.close(); }

        size_t defaultGoodHash( const std::string& key );

        size_t defaultBadHash( const std::string& key );

        void operator()();
        
    private:
        void printResults();
                
        long m_recursionCounter;
        long m_executionCount;
        long m_maxRecursionDepth;
        int m_hashType;
        std::ofstream m_outputFile;
        HashBucketStructure m_bucket;
        //HashFunctionType& m_hashFunction;
        PairList& m_input;
        std::string& m_output;
    };
}

#endif
