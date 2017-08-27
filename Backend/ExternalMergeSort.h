/*
 * Created by Yufen Wang.
 * 2016
 */

#ifndef EXTERNALMERGESORT_H
#define EXTERNALMERGESORT_H

#pragma once
#include "stdafx.h"
#include "global.h"

namespace inf2b
{

    class ExternalMergeSort 
    {
    private:
        InputVectorType& m_input;
        std::vector< std::vector<InputIntType> > m_tempFile;
        InputVectorType m_RAM;
        InputVectorType m_RAM_A;
        InputVectorType m_RAM_B;
        InputVectorType m_RAM_C;
        InputVectorType m_tmpRAM;
        int m_numLimit; //how many elements could fit in RAM
        int m_numLimit_A;
        int m_numLimit_B;
        int m_numLimit_C;
        int m_blockNumber = 0;
        int m_blockSize;
        int m_fileToSort = 0; //which m_tempFile contain unsorted elements in. 0 or 1
        int m_fileSorted = 1; //which m_tempFile to put sorted elements in. 0 or 1
        
        //print a vector for debug process, never called in normal process.
        void printVector( InputVectorType v );
        
        void sort_block();
        
        void merge_RAM();
        
        void merge_block(int i);
        
        /** 
         * Merge different blocks. It makes use of merge_block(int)
         */
        void merge();
        
        /**
         * Perform mergesort on a subset of given input
         */
        void mergeSort( InputVectorType& RAM, InputVectorType& tmpRAM, int left, int right );
        
        /**
         * merge step for the mergeSort(...)
         */
        void merge( InputVectorType& RAM, InputVectorType& tmpRAM, int left, int center, int right );
        
    public:
        ExternalMergeSort( InputVectorType& in, double ram ): m_input( in ) 
        {
            int ramlimit = ram * 1024;
            m_numLimit = ramlimit / sizeof( InputIntType );
            //numlimit = ram;
            m_numLimit_A = m_numLimit / 3;
            m_numLimit_B = m_numLimit_A;
            m_numLimit_C = m_numLimit - m_numLimit_A - m_numLimit_B;
            m_blockSize = m_numLimit;
            m_tempFile.resize( 2 );
        }
        
        ~ExternalMergeSort(){}
        
        void operator()();
    };
}

#endif
