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
#pragma once

#include "stdafx.h"
//#include "Algorithm.h"
#include "global.h"

namespace inf2b
{
    class Quicksort {
    private:

        long recursionCounter;
        long executionCount;
        long maxRecursionDepth;
        const int pivotPosition;
        InputVectorType& input;
        bool debug = true;


        void qsort(long low, long high) {
            ++executionCount;
            if (executionCount % 10000 == 0) {
                //std::cout << executionCount << std::endl;
            }
            if (++recursionCounter > maxRecursionDepth) {
                maxRecursionDepth = recursionCounter;
            }

            //    if (false && (low+1000) < high) {
            if (low < high) {
                long pivotIndex = partition(low, high);
                if (pivotIndex >= high) {
                    // then qsort will NOT be called on the RIGHT subarray.
                    // return call to the left array only to enable tail call optimisation
                    //return qsort(input, low, pivotIndex);
                }
                if (low >= pivotIndex) {
                    // then qsort will NOT be called on the LEFT subarray.
                    // return call only to the RIGHT array to enable tail call optimisation
                    //return qsort(input, pivotIndex + 1, high);
                }
                qsort(low, pivotIndex);
                //        qsort(input, pivotIndex + 1, high);
                qsort(pivotIndex + 1, high);
            }
            // decrement counter whenever this exits
            --recursionCounter;
        }

        void qsort(long low, long high, bool b) {

            struct Args {
                long low;
                long high;
                long pivotIndex;
                int status;
            };
            std::vector<Args> customStack; // what changes if you use pointer to struct std::vector<Args*>?
            
            long pivotIndex = partition(low, high);
            long stackDepth;
            Args args = {low, high, pivotIndex, 0};
            customStack.push_back(args);

            while (customStack.size() > 0) {
                stackDepth = customStack.size();
                // DEBUGGING: log maximum recursion depth
                if (stackDepth > maxRecursionDepth) {
                    maxRecursionDepth = stackDepth;
                }
                if (customStack[stackDepth - 1].status == 1) {
                    // then we've visited both left and right of this frame before
                    customStack.pop_back();
                    continue;
                }
                // Handle LHS of sub-array recursively
                while (pivotIndex > low) { // there is a LHS with > 0 elements
                    auto top = customStack[customStack.size() - 1];
                    low = top.low;
                    high = top.pivotIndex;
                    pivotIndex = partition(low, pivotIndex);
                    Args args = {low, high, pivotIndex, 0};
                    customStack.push_back(args);
                    ++executionCount; // DEBUG
                }
                // take it for granted that pivotIndex==low, 
                // and that's why we're here in the first place
                // RHS runs here after LHS is done
                // TODO: Find out why you lvalue refs throw errors here: 
                // "concurrent" modifications or some referencing dependency cycles??
                auto top = customStack[customStack.size() - 1]; // lvalue ref. for aliasing - should be
                customStack[customStack.size() - 1].status = 1; // visited both l and r, so pop next time
                low = top.pivotIndex + 1;
                high = top.high;
                pivotIndex = partition(low, high);

                if (low < high) {// check if rhs contains > 0 elements

                    Args args = {low, high, pivotIndex, 0};
                    customStack.push_back(args);
                }
                ++executionCount;  // DEBUG
            }
        }

        const long partition(long low, long high) {
            assert((long) input.size() > high && low >= 0);
            // use middle(-left) element as pivot
               
            long pivotIndex;
            if (pivotPosition == QUICKSORT_PIVOT_CENTRE) {
                pivotIndex = static_cast<InputIntType> ((low + high) / 2);
            }
            else { // use left
                pivotIndex = low;
            }
            InputIntType pivot = input[pivotIndex];

            // use (low-1) and (high) instead of (low) and (high-1) to allow use of 
            // pre-increment, which is more efficient than post-increment
            long i = low - 1, j = high + 1;
            while (true) {
                while (input[--j] > pivot) {// move high pointer to the left
                }
                while (input[++i] < pivot) {// move low pointer to the right}
                }
                if (i < j) {
                    std::swap(input[i], input[j]);
                }
                else break;
            }
            // check that it's a bad split
            //assert(j == low);
            return j;
        }

    public:

        Quicksort(InputVectorType& v, const int p) : input(v), pivotPosition {p},
            recursionCounter {0}, executionCount {0}, maxRecursionDepth {0} { };
        Quicksort(InputVectorType& v, const int p, bool db) : input(v), pivotPosition {p},
            recursionCounter {0}, executionCount {0}, maxRecursionDepth {0}, debug{db}{ };

        //Quicksort(const Quicksort& orig) = delete;

        ~Quicksort() { };

        InputVectorType& operator()() {
            if(!debug){
                std::cout << "[TASKRUNNER] Executing quicksort..." << std::endl;
            }
            qsort(0, input.size() - 1, 0); // heap
            //qsort(0, input.size() - 1); // stack
            if(!debug){
                std::cout << "[MAXRECURSION]\t" << maxRecursionDepth << std::endl;
            }
            return input;
        }
    };
}
