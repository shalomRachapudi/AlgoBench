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

#ifndef GENERATOR_H
#define GENERATOR_H

#pragma once
#include "stdafx.h"
#include "global.h"
#include "GraphAlgo.h"

namespace inf2b
{
    class Generator 
    {

    private:
        static void generateRandom( InputVectorType& input,
                                    const size_t& size,
                                    InputIntType min,
                                    InputIntType max
                                  );
        
        /**
         * Generates random integers without repetition
         * 
         * The generated sequence is then shuffled using std::random_shuffle()
         * to minimize the risk of getting (semi) sorted integer seqence
         */
        static void generateUniqueRandom( InputVectorType& input,
                                          const size_t& size,
                                          InputIntType min,
                                          InputIntType max
                                  );

        static void generateRepeated( InputVectorType& input, 
                                      const size_t& size, 
                                      int thId = 0
                                  );
        
        static void generateSorted( InputVectorType& input,
                                    const size_t& size, 
                                    InputIntType min,
                                    int thId = 0
                                  );
        
        static void generateRSorted( InputVectorType& input,
                                     const size_t& size, 
                                     InputIntType max, 
                                     int thId = 0
                                  );
        
        /*
         static void generateWorstCaseInput( InputVectorType& input,
                                             InputIntType min, 
                                             int thId = 0
                                           );
        */

        static void generateWorstRecursive( InputVectorType& input, 
                                            long low,
                                            long high, 
                                            long leastValue
                                          );
        
        static void generateWorst( InputVectorType& input,
                                   long low, 
                                   long high,
                                   long leastValue
                                 );
        
        static void generateRandomLong( long& target, 
                                        long min, 
                                        long max 
                                );
        
        static void generateRandomKey( InputIntType& target, 
                                       InputIntType min,
                                       InputIntType max
                                );

    public:
        static void generateArray( InputVectorType& input, 
                                   const size_t inputSize,
                                   const int inputDistribution,
                                   const int runNumber,
                                   InputIntType minValue,
                                   InputIntType maxValue
                                 );
        
        static void generateGraph( Graph& graph,
                                   const size_t& size,
                                   size_t fixedParamSize,
                                   bool directed,
                                   bool allowSelfLoop,
                                   bool fixedEdges
                                 );

        static void generateHashList( PairList& list,
                                      const size_t numEntries,
                                      const int hashKeyType,
                                      const std::string& filename
                                    );
        
        static void generateHashInput( InputVectorType& input, 
                                       const size_t numEntries,
                                       const int hashKeyType, 
                                       const std::string& filename
                                     );
        
        static void generateSearchKey( InputVectorType input,
                                       InputIntType& key,
                                       const int searchKeyType,
                                       const int runNumber,
                                       InputIntType minValue,
                                       InputIntType maxValue
                                     );
        static void generateTreeInput( InputVectorType& input,
                                       const size_t size,
                                       const InputIntType min,
                                       const InputIntType max,
                                       const int treeType
                                     );
    };
}

#endif
