/* 
 * File:   InsertionSort.cpp
 * Author: yufenwang
 * 
 * Created on July 18, 2016, 7:11 PM
 */

#include "InsertionSort.h"
using namespace inf2b;


void InsertionSort::insertionSort()
{
    for ( int i = 1; i < m_input.size(); i++ ) {
        if ( m_input[ i - 1 ] > m_input[ i ] ) {
            InputIntType temp = m_input[ i ];
            int j = i;
            while ( j >= 0 && m_input[ j ] > temp ) {
                j--;
            }
            m_input.insert( ( m_input.begin() + j + 1 ), temp );
            m_input.erase( m_input.begin() + i + 1 );
        }
    }
}

void InsertionSort::operator()()
{
    std::cout << "[TASKRUNNER] Executing insertsort..." << std::endl;
    insertionSort();
}
