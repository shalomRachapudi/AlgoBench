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
  * 
  * Modified by Yufen Wang, 2016
  */

#ifndef GLOBAL_H
#define GLOBAL_H

#pragma once
// variables that the entire application sees
#include "Semaphore.h"
namespace inf2b
{
    typedef uint32_t InputIntType;
    typedef std::vector< InputIntType > InputVectorType;
    typedef std::vector< size_t > SizeTArrayType;
    typedef std::pair< std::string, std::string > StringPair;
    typedef std::vector< StringPair > PairList;
    typedef std::unordered_map< std::string, std::string > HashDictionaryType;
    typedef std::function< size_t( const std::string ) > HashFunctionType;

    const int DEFAULT_BUFFERSIZE = 1024;
    const char INSTRUCTION_DELIM = ':';
    // algo groups
    const int SORT = 1;
    const int SEARCH = 2;
    const int GRAPH = 3;
    const int HASH = 4;
    const int TREE = 5;
    // algos - ALGO
    const int QUICK_SORT = 10;
    const int HEAP_SORT = 11;
    const int EXTERNAL_MERGE_SORT = 12;
    const int INTERNAL_MERGE_SORT = 13;
    const int INSERTION_SORT = 14;
    const int LINEAR_SEARCH = 20;
    const int BINARY_SEARCH = 21;
    const int GRAPH_BFS = 30;
    const int GRAPH_DFS = 31;
    const int HASHING = 40;
    const int BINARY_SEARCH_TREE = 50;
    const int AVL_TREE = 51;
    // input distributions - INPUT_DISTRIBUTION
    const int INPUT_RANDOM = 1;
    const int INPUT_SORTED = 2;
    const int INPUT_REVERSE_SORTED = 3;
    const int INPUT_REPEATED = 4;
    const int INPUT_WORST_CASE = 5;
    // pivot positions - QUICKSORT_PIVOT_CHOICE
    const int QUICKSORT_PIVOT_LEFT = 1;
    const int QUICKSORT_PIVOT_CENTRE = 2;
    const int QUICKSORT_PIVOT_RIGHT = 3;
    const int QUICKSORT_PIVOT_MEDIAN = 4;
    // hash function types
    const int GOOD_HASH = 1;
    const int BAD_HASH = 2;
    // hash KEY types
    const int HASH_KEY_ALPHABET = 1;
    const int HASH_KEY_NUMBER = 2;
    // search key type
    const int SEARCH_KEY_ALWAYS_IN_ARRAY = 1;
    const int SEARCH_KEY_NOT_IN_ARRAY = 2;
    const int SEARCH_KEY_RANDOM = 3;
    // tree type
    const int ROOTED_TREE = 0;
    const int LEFT_SKEWED_TREE = 1;
    const int RIGHT_SKEWED_TREE = 2;
    // heartbeat message char
    const char HEARTBEAT_CHAR[] = "\0\n";

    const long minLong = 0;  // use C style limits
    const long maxLong = ULONG_MAX;

    extern std::string ALGO_INPUT_FILENAME;
    extern std::map<std::string, size_t> Command;
    extern size_t MemoryUsage;
    extern std::vector<std::string> progressReport;
    extern std::mutex myMutex;
    extern Semaphore progressSemaphore;
    extern Semaphore socketReadySemaphore;
    
    template < class Type >
    Type stringToNum( const std::string& str )
    {
            std::istringstream iss( str );
            Type num;
            iss >> num;
            return num;
    }
}

#endif
