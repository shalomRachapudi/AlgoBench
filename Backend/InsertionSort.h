/*
 * Created by Yufen Wang.
 * 2016
 */

/* 
 * File:   Insertsort.h
 * Author: yufenwang
 *
 * Created on July 18, 2016, 7:11 PM
 */
#ifndef INSERTIONSORT_H
#define INSERTIONSORT_H

#pragma once
#include "stdafx.h"
#include "global.h"

namespace inf2b
{
    class InsertionSort
    {
    private:
        InputVectorType& m_input;
    
        void insertionSort();
    public:
        InsertionSort( InputVectorType& in ) : m_input( in ) {}
        
        void operator()();
    };
}

#endif
